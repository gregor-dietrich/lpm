package de.vptr.lpm.view.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.service.ProjectService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

/**
 * Detail view for a specific project with tabs for overview, members, and
 * settings.
 */
@Route(value = "projects/:projectId", layout = MainLayout.class)
@PageTitle("Project | LPM")
@RolesAllowed({ "ADMIN", "PROJECT_MANAGER", "DEVELOPER" })
public class ProjectDetailView extends VerticalLayout implements BeforeEnterObserver {

    @Inject
    transient ProjectService projectService;

    private ProjectDto project;
    private final VerticalLayout contentArea = new VerticalLayout();

    public ProjectDetailView() {
        this.setPadding(true);
        this.setSpacing(true);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        final var projectIdParam = event.getRouteParameters().get("projectId");
        if (projectIdParam.isEmpty()) {
            event.rerouteTo("projects");
            return;
        }

        try {
            final var projectId = Long.parseLong(projectIdParam.get());
            final var projectOpt = this.projectService.findById(projectId);
            if (projectOpt.isEmpty()) {
                event.rerouteTo("projects");
                return;
            }

            this.project = projectOpt.get();
            this.initializeView();
        } catch (final NumberFormatException ex) {
            event.rerouteTo("projects");
        }
    }

    private void initializeView() {
        this.removeAll();

        // Header
        final var header = new HorizontalLayout();
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.setWidthFull();

        final var title = new H2(this.project.name());
        final var backButton = new Button("← Back to Projects",
                event -> this.getUI().ifPresent(ui -> ui.navigate("projects")));

        header.add(backButton, title);

        // Tabs for different views
        final var overviewTab = new Tab("Overview");
        final var membersTab = new Tab("Members");
        final var settingsTab = new Tab("Settings");
        final var tabs = new Tabs(overviewTab, membersTab, settingsTab);

        this.contentArea.setWidthFull();
        this.showOverview();

        tabs.addSelectedChangeListener(event -> {
            this.contentArea.removeAll();
            final var selectedTab = event.getSelectedTab();
            if (selectedTab == overviewTab) {
                this.showOverview();
            } else if (selectedTab == membersTab) {
                this.showMembers();
            } else if (selectedTab == settingsTab) {
                this.showSettings();
            }
        });

        this.add(header, tabs, this.contentArea);
    }

    private void showOverview() {
        final var description = this.project.description() != null
                ? new Paragraph(this.project.description())
                : new Paragraph("No description provided");

        final var key = new Paragraph("Project Key: " + this.project.projectKey());
        final var status = new Paragraph("Status: " + this.project.statusName());

        this.contentArea.add(key, status, description);
    }

    private void showMembers() {
        final var addMemberButton = new Button("Add Member", event -> {
            Notification.show("Add member dialog not yet implemented", 3000, Notification.Position.TOP_CENTER);
        });
        addMemberButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.contentArea.add(addMemberButton);
    }

    private void showSettings() {
        final var editButton = new Button("Edit Project", event -> {
            Notification.show("Edit project dialog not yet implemented", 3000, Notification.Position.TOP_CENTER);
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var deleteButton = new Button("Delete Project", event -> {
            Notification.show("Delete confirmation not yet implemented", 3000, Notification.Position.TOP_CENTER);
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        this.contentArea.add(editButton, deleteButton);
    }
}
