package de.vptr.lpm.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.dto.UserDto;
import jakarta.annotation.security.PermitAll;

/**
 * Root view that redirects to login or dashboard based on authentication
 * status.
 */
@Route(value = "")
@PageTitle("LPM")
@PermitAll
public class RootView extends VerticalLayout implements BeforeEnterObserver {

    /**
     * Initializes the root view and redirects accordingly.
     */
    public RootView() {
        this.setSizeFull();
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser != null) {
            event.rerouteTo("dashboard");
        } else {
            event.rerouteTo("login");
        }
    }
}
