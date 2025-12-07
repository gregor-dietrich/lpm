package de.vptr.lpm.view.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.service.ProjectService;
import de.vptr.lpm.view.MainLayout;
import jakarta.inject.Inject;

/**
 * List view for projects with filtering and project creation.
 */
@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects | LPM")
public class ProjectListView extends VerticalLayout {

    @Inject
    transient ProjectService projectService;

    private final Grid<ProjectDto> grid;
    private final ListDataProvider<ProjectDto> dataProvider;

    /**
     * Initializes the project list view with grid and filters.
     */
    public ProjectListView() {
        this.setPadding(true);
        this.setSpacing(true);

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
        searchField.addValueChangeListener(event -> this.updateGrid());

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

    private void updateGrid() {
        this.dataProvider.refreshAll();
    }

    private void openProjectDialog() {
        Notification.show("Project creation not yet implemented", 3000, Notification.Position.TOP_CENTER);
    }
}
