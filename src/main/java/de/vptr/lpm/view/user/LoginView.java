package de.vptr.lpm.view.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.service.UserService;
import jakarta.inject.Inject;

/**
 * Login view for user authentication.
 */
@Route(value = "login")
@PageTitle("Login | LPM")
public class LoginView extends VerticalLayout {

    @Inject
    transient UserService userService;

    /**
     * Initializes the login view with authentication form.
     */
    public LoginView() {
        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);

        final var loginForm = new FormLayout();
        loginForm.setMaxWidth("400px");

        final var title = new H1("Libre Project Manager");
        final var usernameField = new TextField("Username");
        usernameField.setWidthFull();
        final var passwordField = new PasswordField("Password");
        passwordField.setWidthFull();

        final var loginButton = new Button("Login", event -> this.handleLogin(
                usernameField.getValue(),
                passwordField.getValue()));
        loginButton.setWidthFull();

        loginForm.add(usernameField, passwordField, loginButton);

        this.add(title, loginForm);
    }

    private void handleLogin(final String username, final String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Notification.show("Username and password are required", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        final var userOpt = this.userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            final var user = userOpt.get();
            VaadinSession.getCurrent().setAttribute("user", user);
            this.getUI().ifPresent(ui -> ui.navigate("dashboard"));
        } else {
            Notification.show("Invalid username or password", 3000, Notification.Position.TOP_CENTER);
        }
    }
}
