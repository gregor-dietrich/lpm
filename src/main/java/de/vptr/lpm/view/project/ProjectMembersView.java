package de.vptr.lpm.view.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.dto.ProjectMemberDto;
import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.ProjectMemberService;
import de.vptr.lpm.service.ProjectService;
import de.vptr.lpm.service.UserService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;

/**
 * View for managing project members.
 */
@Route(value = "projects/:projectId/members", layout = MainLayout.class)
@PageTitle("Project Members | LPM")
@PermitAll
public class ProjectMembersView extends VerticalLayout
        implements BeforeEnterObserver, HasUrlParameter<String> {

    @Inject
    ProjectService projectService;

    @Inject
    ProjectMemberService projectMemberService;

    @Inject
    UserService userService;

    private ProjectDto currentProject;
    private UserDto currentUser;
    private Grid<ProjectMemberDto> grid;
    private ListDataProvider<ProjectMemberDto> dataProvider;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        this.currentUser = (UserDto) event.getUI().getSession().getAttribute("user");
        if (this.currentUser == null) {
            event.forwardTo("login");
            return;
        }
    }

    @Override
    public void setParameter(final com.vaadin.flow.router.BeforeEvent event, final String projectId) {
        try {
            final var id = Long.parseLong(projectId);
            this.currentProject = this.projectService.findById(id).orElse(null);
            if (this.currentProject == null) {
                event.forwardTo("projects");
                return;
            }
            this.initializeContent();
        } catch (final NumberFormatException e) {
            event.forwardTo("projects");
        }
    }

    private void initializeContent() {
        this.setSpacing(true);
        this.setPadding(true);

        final var backButton = new Button("← Back to Project");
        backButton.addClickListener(
                e -> this.getUI().ifPresent(ui -> ui.navigate("projects/" + this.currentProject.id())));

        final var header = new HorizontalLayout();
        final var title = new H2("Members: " + this.currentProject.name());
        final var addButton = new Button("Add Member");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> this.openAddMemberDialog());
        header.add(title, addButton);

        // Grid
        this.grid = new Grid<>(ProjectMemberDto.class, false);
        this.grid.addColumn(ProjectMemberDto::userName).setHeader("User");
        this.grid.addColumn(ProjectMemberDto::role).setHeader("Role");
        this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        this.dataProvider = new ListDataProvider<>(this.projectMemberService.findByProject(this.currentProject.id()));
        this.grid.setDataProvider(this.dataProvider);

        this.add(backButton, header, this.grid);
    }

    private void openAddMemberDialog() {
        final var dialog = new Dialog();
        dialog.setHeaderTitle("Add Project Member");
        dialog.setModal(true);
        dialog.setDraggable(true);

        final var form = new FormLayout();

        final var userSelect = new Select<UserDto>();
        userSelect.setLabel("User");
        userSelect.setItemLabelGenerator(UserDto::displayName);
        final var existingMemberIds = this.projectMemberService.findByProject(this.currentProject.id())
                .stream()
                .map(ProjectMemberDto::userId)
                .toList();
        final var availableUsers = this.userService.listAll().stream()
                .filter(u -> !existingMemberIds.contains(u.id()))
                .toList();
        userSelect.setItems(availableUsers);
        userSelect.setWidthFull();

        final var roleSelect = new Select<String>();
        roleSelect.setLabel("Role");
        roleSelect.setItems("DEVELOPER", "PROJECT_MANAGER");
        roleSelect.setWidthFull();

        final var addButton = new Button("Add");
        final var cancelButton = new Button("Cancel", e -> dialog.close());

        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            if (userSelect.getValue() != null && roleSelect.getValue() != null) {
                this.projectMemberService.addMember(this.currentProject.id(), userSelect.getValue().id(),
                        roleSelect.getValue());
                this.dataProvider.getItems().addAll(
                        this.projectMemberService.findByProject(this.currentProject.id()));
                this.dataProvider.refreshAll();
                dialog.close();
            }
        });

        form.add(userSelect, roleSelect);
        dialog.add(form);
        dialog.getFooter().add(addButton, cancelButton);
        dialog.open();
    }
}
