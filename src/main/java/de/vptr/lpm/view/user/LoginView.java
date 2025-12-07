package de.vptr.lpm.view.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.service.AuthenticationService;
import de.vptr.lpm.util.NotificationUtil;
import de.vptr.lpm.view.MainLayout;
import jakarta.inject.Inject;

/**
 * Login view for user authentication.
 */
@Route(value = "login", layout = MainLayout.class)
@PageTitle("Login | LPM")
public class LoginView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(LoginView.class);

    @Inject
    private transient AuthenticationService authenticationService;

    /**
     * Initializes the login view with authentication form.
     */
    public LoginView() {
        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);

        final var title = new H1("Libre Project Manager - Login");

        final var usernameField = new TextField("Username");
        usernameField.setPrefixComponent(LineAwesomeIcon.USER_SOLID.create());
        usernameField.setRequired(true);
        usernameField.setWidth("300px");

        final var passwordField = new PasswordField("Password");
        passwordField.setPrefixComponent(LineAwesomeIcon.LOCK_SOLID.create());
        passwordField.setRequired(true);
        passwordField.setWidth("300px");

        final var loginButton = new Button("Login");
        loginButton.addClickListener(e -> {
            final var username = usernameField.getValue();
            final var password = passwordField.getValue();

            try {
                // Disable button during authentication
                loginButton.setEnabled(false);
                loginButton.setText("Authenticating...");

                // Perform authentication
                final var userOpt = this.authenticationService.authenticate(username, password);

                LOG.trace("Authentication result - Present: {}", userOpt.isPresent());

                if (userOpt.isPresent()) {
                    LOG.trace("Authentication successful, storing user in session");
                    final var user = userOpt.get();
                    VaadinSession.getCurrent().setAttribute("user", user);
                    LOG.trace("Navigating to dashboard");
                    this.getUI().ifPresent(ui -> ui.navigate("dashboard"));
                } else {
                    LOG.trace("Invalid credentials");
                    NotificationUtil.showError("Invalid username or password");
                    passwordField.clear();
                    passwordField.focus();
                }

            } catch (final Exception ex) {
                LOG.error("Exception during authentication", ex);
                NotificationUtil.showError("Unexpected error: " + ex.getMessage());
            } finally {
                // Re-enable button
                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }
        });

        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.setWidth("300px");

        this.add(title, usernameField, passwordField, loginButton);

        usernameField.focus();
    }
}
