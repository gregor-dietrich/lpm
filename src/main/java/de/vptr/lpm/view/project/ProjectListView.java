package de.vptr.lpm.view.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.service.ProjectService;
import de.vptr.lpm.view.MainLayout;
import jakarta.inject.Inject;

/**
 * List view for projects with filtering and project creation.
 */
@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects | LPM")
public class ProjectListView extends VerticalLayout implements BeforeEnterObserver {

    @Inject
    transient ProjectService projectService;

    private Grid<ProjectDto> grid;
    private ListDataProvider<ProjectDto> dataProvider;
    private boolean initialized = false;

    /**
     * Initializes the project list view with grid and filters.
     */
    public ProjectListView() {
        this.setPadding(true);
        this.setSpacing(true);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (this.initialized) {
            return;
        }

        final var currentUser = (de.vptr.lpm.dto.UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null) {
            event.forwardTo("login");
            return;
        }

        this.initializeContent();
        this.initialized = true;
    }

    private void initializeContent() {

        // Header with title and create button
        final var header = new HorizontalLayout();
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.setWidthFull();

        final var title = new com.vaadin.flow.component.html.H2("Projects");
        final var createButton = new Button("Create Project", event -> this.openProjectDialog());
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        header.add(title, createButton);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Search field
        final var searchField = new TextField("Search projects...");
        searchField.setPlaceholder("Search by name or key");
        searchField.setWidthFull();
        searchField.addValueChangeListener(event -> {
            final var filter = event.getValue().toLowerCase();
            this.dataProvider.setFilter(
                    project -> (project.name() == null ? "" : project.name().toLowerCase()).contains(filter) ||
                            (project.projectKey() == null ? "" : project.projectKey().toLowerCase()).contains(filter));
        });

        // Grid
        this.grid = new Grid<>(ProjectDto.class, false);
        this.grid.addColumn(ProjectDto::projectKey).setHeader("Key");
        this.grid.addColumn(ProjectDto::name).setHeader("Name");
        this.grid.addColumn(ProjectDto::ownerName).setHeader("Owner");
        this.grid.addColumn(ProjectDto::statusName).setHeader("Status");
        this.grid.addColumn(ProjectDto::memberCount).setHeader("Members");
        this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        this.dataProvider = new ListDataProvider<>(this.projectService.listAll());
        this.grid.setDataProvider(this.dataProvider);

        this.grid.asSingleSelect().addValueChangeListener(event -> {
            final var project = event.getValue();
            if (project != null) {
                this.getUI().ifPresent(ui -> ui.navigate("projects/" + project.id()));
            }
        });

        this.add(header, searchField, this.grid);
    }

    private void openProjectDialog() {
        final var dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Create New Project");

        final var form = new com.vaadin.flow.component.formlayout.FormLayout();
        final var nameField = new TextField("Project Name");
        nameField.setRequired(true);
        final var keyField = new TextField("Project Key");
        keyField.setRequired(true);
        final var descField = new TextField("Description");

        form.add(nameField, keyField, descField);

        final var saveButton = new Button("Create", event -> {
            if (nameField.getValue().isEmpty() || keyField.getValue().isEmpty()) {
                com.vaadin.flow.component.notification.Notification.show("Name and Key are required");
                return;
            }
            try {
                final var currentUser = (de.vptr.lpm.dto.UserDto) VaadinSession.getCurrent().getAttribute("user");
                this.projectService.createProject(
                        nameField.getValue(),
                        keyField.getValue(),
                        descField.getValue(),
                        currentUser.id());
                this.dataProvider.refreshAll();
                dialog.close();
                com.vaadin.flow.component.notification.Notification.show("Project created successfully");
            } catch (final Exception e) {
                com.vaadin.flow.component.notification.Notification.show("Error creating project: " + e.getMessage());
            }
        });
        final var cancelButton = new Button("Cancel", event -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.add(form);
        dialog.open();
    }
}
