package de.vptr.lpm.view.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.UserService;

/**
 * Dialog for creating or editing users.
 */
public class UserFormDialog extends Dialog {

    private final transient UserService userService;
    private final Runnable onSave;
    private transient UserDto editingUser;

    /**
     * Initializes the user form dialog.
     *
     * @param userService service for user operations
     * @param onSave      callback when form is saved
     */
    public UserFormDialog(final UserService userService, final Runnable onSave) {
        this.userService = userService;
        this.onSave = onSave;

        this.setHeaderTitle("Create User");
        this.setWidth("500px");

        final var form = new FormLayout();

        final var usernameField = new TextField("Username");
        usernameField.setRequired(true);
        usernameField.setRequiredIndicatorVisible(true);

        final var emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);

        final var passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setRequiredIndicatorVisible(true);

        final var displayNameField = new TextField("Display Name");
        displayNameField.setRequired(true);
        displayNameField.setRequiredIndicatorVisible(true);

        final var saveButton = new Button("Save", event -> this.handleSave(
                usernameField.getValue(),
                emailField.getValue(),
                passwordField.getValue(),
                displayNameField.getValue()));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var cancelButton = new Button("Cancel", event -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        final var buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        form.add(usernameField, emailField, passwordField, displayNameField);
        this.add(form, buttonLayout);
    }

    /**
     * Opens dialog for editing an existing user.
     *
     * @param user the user to edit
     */
    public void editUser(final UserDto user) {
        this.editingUser = user;
        this.setHeaderTitle("Edit User");
    }

    private void handleSave(
            final String username,
            final String email,
            final String password,
            final String displayName) {
        try {
            if (username.isEmpty() || email.isEmpty() || displayName.isEmpty()) {
                Notification.show("All fields except password are required", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            if (this.editingUser == null) {
                if (password.isEmpty()) {
                    Notification.show("Password is required for new users", 3000, Notification.Position.TOP_CENTER);
                    return;
                }
                this.userService.createUser(username, email, password, displayName);
            } else {
                this.userService.updateUser(this.editingUser.id(), username, email, displayName);
            }

            Notification.show("User saved successfully", 3000, Notification.Position.TOP_CENTER);
            this.close();
            this.onSave.run();
        } catch (final Exception e) {
            Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }
}
