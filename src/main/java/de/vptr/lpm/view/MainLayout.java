package de.vptr.lpm.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.component.button.ThemeToggleButton;
import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.ThemeService;
import de.vptr.lpm.view.user.LoginView;
import jakarta.inject.Inject;

/**
 * Main application layout with navigation sidebar and header.
 */
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static final Logger LOG = LoggerFactory.getLogger(MainLayout.class);

    @Inject
    transient ThemeService themeService;

    private boolean initialized = false;
    private boolean drawerInitialized = false;
    private com.vaadin.flow.component.applayout.DrawerToggle drawerToggle;
    private Button logoutButton;

    /**
     * Initializes the main layout with drawer and header.
     */
    public MainLayout() {
        this.setPrimarySection(Section.DRAWER);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (!this.initialized) {
            this.addHeaderContent();
            this.themeService.applyTheme(this.themeService.getCurrentTheme());
            this.initialized = true;
        }

        final var targetView = event.getNavigationTarget();
        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");

        LOG.trace("MainLayout.beforeEnter - Target: {}", targetView.getSimpleName());

        // Allow LoginView and RootView without authentication
        if (targetView == LoginView.class || targetView == RootView.class) {
            LOG.trace("Navigating to login/root view, hiding drawer and logout");
            if (this.drawerToggle != null) {
                this.drawerToggle.setVisible(false);
            }
            if (this.logoutButton != null) {
                this.logoutButton.setVisible(false);
            }
            return;
        }

        // Show drawer toggle and logout for authenticated views
        if (this.drawerToggle != null) {
            this.drawerToggle.setVisible(true);
        }
        if (this.logoutButton != null) {
            this.logoutButton.setVisible(true);
        }

        // Add drawer content only for authenticated users (and only once)
        if (currentUser != null && !this.drawerInitialized) {
            this.addDrawerContent();
            this.drawerInitialized = true;
        }

        // Redirect to login if not authenticated
        if (currentUser == null) {
            LOG.trace("User not authenticated, redirecting to login");
            event.forwardTo(LoginView.class);
            return;
        }

        LOG.trace("All checks passed for {}", targetView.getSimpleName());
    }

    private void addDrawerContent() {
        final var logo = new H1("LPM");
        logo.addClassNames("logo");

        final var nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", "/dashboard"));
        nav.addItem(new SideNavItem("Projects", "/projects"));
        nav.addItem(new SideNavItem("Tickets", "/tickets"));
        nav.addItem(new SideNavItem("Board", "/board"));
        nav.addItem(new SideNavItem("Profile", "/profile"));

        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser != null && currentUser.roles().stream()
                .anyMatch(r -> "ADMIN".equals(r.name()))) {
            nav.addItem(new SideNavItem("User Management", "/admin/users"));
        }

        final var scroller = new Scroller(nav);
        scroller.setClassName("nav-scroller");

        this.addToDrawer(logo, scroller);
    }

    private void addHeaderContent() {
        this.drawerToggle = new com.vaadin.flow.component.applayout.DrawerToggle();
        this.drawerToggle.setAriaLabel("Menu");

        final var header = new HorizontalLayout(this.drawerToggle);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames("toolbar");

        final var themeButton = new ThemeToggleButton(this.themeService);
        themeButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        themeButton.setAriaLabel("Toggle theme");

        this.logoutButton = new Button("Logout", event -> {
            VaadinSession.getCurrent().setAttribute("user", null);
            this.getUI().ifPresent(ui -> ui.navigate("login"));
        });

        header.add(themeButton, this.logoutButton);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        this.addToNavbar(false, header);
    }
}
