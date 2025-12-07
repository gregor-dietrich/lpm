package de.vptr.lpm.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.dto.UserDto;

/**
 * Main application layout with navigation sidebar and header.
 */
public class MainLayout extends AppLayout {

    /**
     * Initializes the main layout with drawer and header.
     */
    public MainLayout() {
        this.setPrimarySection(Section.DRAWER);
        this.addDrawerContent();
        this.addHeaderContent();
    }

    private void addDrawerContent() {
        final var logo = new H1("LPM");
        logo.addClassNames("logo");

        final var nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", "/dashboard"));
        nav.addItem(new SideNavItem("Projects", "/projects"));
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
        final var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu");

        final var header = new HorizontalLayout(toggle);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames("toolbar");

        final var logout = new Button("Logout", event -> {
            VaadinSession.getCurrent().setAttribute("user", null);
            this.getUI().ifPresent(ui -> ui.navigate("login"));
        });

        header.add(logout);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        this.addToNavbar(false, header);
    }
}
