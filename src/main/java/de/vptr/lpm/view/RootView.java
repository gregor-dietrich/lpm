package de.vptr.lpm.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import de.vptr.lpm.service.AuthenticationService;
import jakarta.inject.Inject;

/**
 * Root view that redirects to login or dashboard based on authentication
 * status.
 */
@Route(value = "", layout = MainLayout.class)
public class RootView extends VerticalLayout implements BeforeEnterObserver {

    @Inject
    transient AuthenticationService authenticationService;

    /**
     * Initialize the root view.
     */
    public RootView() {
        this.setSizeFull();
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        final var currentUser = this.authenticationService.getCurrentUser();
        if (currentUser.isPresent()) {
            event.rerouteTo("dashboard");
        } else {
            event.rerouteTo("login");
        }
    }
}
