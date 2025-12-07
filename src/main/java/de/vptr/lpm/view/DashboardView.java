package de.vptr.lpm.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.dto.UserDto;
import jakarta.annotation.security.PermitAll;

/**
 * Dashboard view serving as the landing page after login.
 */
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | LPM")
@PermitAll
public class DashboardView extends VerticalLayout {

    /**
     * Initializes the dashboard view with welcome message.
     */
    public DashboardView() {
        this.setPadding(true);
        this.setSpacing(true);

        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null) {
            this.add(new H2("Not logged in"));
            return;
        }

        this.add(new H2("Welcome, " + currentUser.displayName()));
    }
}
