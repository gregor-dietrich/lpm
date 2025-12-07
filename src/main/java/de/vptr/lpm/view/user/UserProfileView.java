package de.vptr.lpm.view.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.UserService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;

/**
 * User profile view for self-service profile editing.
 */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile | LPM")
@PermitAll
public class UserProfileView extends VerticalLayout {

    @Inject
    transient UserService userService;

    /**
     * Initializes the user profile view for current user.
     */
    public UserProfileView() {
        this.setPadding(true);
        this.setSpacing(true);

        final var title = new H2("My Profile");
        final var form = new FormLayout();

        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null) {
            this.add(new H2("Not logged in"));
            return;
        }

        final var usernameField = new TextField("Username");
        usernameField.setValue(currentUser.username());
        usernameField.setReadOnly(true);

        final var emailField = new EmailField("Email");
        emailField.setValue(currentUser.email());

        final var displayNameField = new TextField("Display Name");
        displayNameField.setValue(currentUser.displayName());

        final var saveButton = new Button("Save Changes", event -> this.handleSave(
                currentUser.id(),
                emailField.getValue(),
                displayNameField.getValue()));

        form.add(usernameField, emailField, displayNameField, saveButton);
        this.add(title, form);
    }

    private void handleSave(final Long userId, final String email, final String displayName) {
        try {
            final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
            final var updated = new UserDto(
                    currentUser.id(),
                    currentUser.username(),
                    email,
                    displayName,
                    currentUser.status(),
                    currentUser.roles(),
                    currentUser.createdAt(),
                    currentUser.updatedAt());
            final var result = this.userService.updateUser(userId, updated);
            VaadinSession.getCurrent().setAttribute("user", result);
            Notification.show("Profile updated successfully", 3000, Notification.Position.TOP_CENTER);
        } catch (final Exception e) {
            Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }
}
