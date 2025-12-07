package de.vptr.lpm.view.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.entity.ProjectStatus;
import de.vptr.lpm.service.ProjectService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;

/**
 * Project settings view for managing project configuration.
 */
@Route(value = "projects/:projectId/settings", layout = MainLayout.class)
@PageTitle("Project Settings | LPM")
@PermitAll
public class ProjectSettingsView extends VerticalLayout
        implements BeforeEnterObserver, HasUrlParameter<String> {

    @Inject
    ProjectService projectService;

    private ProjectDto currentProject;
    private UserDto currentUser;

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

        final var title = new H2("Settings: " + this.currentProject.name());

        final var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        final var nameField = new TextField("Project Name");
        nameField.setValue(this.currentProject.name());
        nameField.setWidthFull();

        final var keyField = new TextField("Project Key");
        keyField.setValue(this.currentProject.projectKey());
        keyField.setReadOnly(true);
        keyField.setWidthFull();

        final var descriptionField = new TextArea("Description");
        descriptionField.setValue(this.currentProject.description() != null ? this.currentProject.description() : "");
        descriptionField.setWidthFull();

        final var statusSelect = new Select<ProjectStatus>();
        statusSelect.setLabel("Status");
        statusSelect.setItems(ProjectStatus.<ProjectStatus>listAll());
        statusSelect.setItemLabelGenerator(s -> s.name);
        if (this.currentProject.statusId() != null) {
            final var status = ProjectStatus.<ProjectStatus>findById(this.currentProject.statusId());
            if (status != null) {
                statusSelect.setValue(status);
            }
        }

        form.add(nameField, keyField, descriptionField, statusSelect);

        final var actions = new HorizontalLayout();
        final var saveButton = new Button("Save Changes");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (nameField.getValue().isBlank()) {
                nameField.setInvalid(true);
                return;
            }
            this.projectService.updateProject(
                    this.currentProject.id(),
                    nameField.getValue(),
                    descriptionField.getValue());
            this.getUI().ifPresent(ui -> ui.navigate("projects/" + this.currentProject.id()));
        });

        final var deleteButton = new Button("Delete Project");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            final var confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Confirm Deletion");
            confirmDialog.add(new com.vaadin.flow.component.html.Paragraph(
                    "Are you sure you want to delete this project? This action cannot be undone."));
            final var confirmButton = new Button("Delete", confirmed -> {
                this.projectService.deleteProject(this.currentProject.id());
                confirmDialog.close();
                this.getUI().ifPresent(ui -> ui.navigate("projects"));
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            final var cancelButton = new Button("Cancel", confirmed -> confirmDialog.close());
            confirmDialog.getFooter().add(cancelButton, confirmButton);
            confirmDialog.open();
        });

        actions.add(saveButton, deleteButton);

        this.add(backButton, title, form, actions);
    }
}
