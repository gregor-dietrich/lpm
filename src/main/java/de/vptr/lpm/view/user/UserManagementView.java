package de.vptr.lpm.view.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.UserService;
import de.vptr.lpm.view.MainLayout;
import jakarta.inject.Inject;

/**
 * User management view for admin user CRUD operations.
 */
@Route(value = "admin/users", layout = MainLayout.class)
@PageTitle("User Management | LPM")
public class UserManagementView extends VerticalLayout {

    @Inject
    transient UserService userService;

    private Grid<UserDto> userGrid;

    /**
     * Initializes the user management view with grid and controls.
     */
    public UserManagementView() {
        this.setPadding(true);
        this.setSpacing(true);

        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null || currentUser.roles().stream()
                .noneMatch(r -> "ADMIN".equals(r.name()))) {
            this.add(new H2("Access Denied"));
            this.getUI().ifPresent(ui -> ui.navigate("dashboard"));
            return;
        }

        final var title = new H2("User Management");
        final var createButton = new Button("Create User", event -> this.openUserDialog(null));

        this.userGrid = new Grid<>(UserDto.class);
        this.userGrid.setColumns("id", "username", "email", "displayName", "status");
        this.userGrid.addColumn(user -> "Edit")
                .setHeader("Actions")
                .setWidth("100px");

        this.refreshUserList();

        this.add(title, createButton, this.userGrid);
    }

    private void refreshUserList() {
        final var users = this.userService.listAll();
        this.userGrid.setItems(new ListDataProvider<>(users));
    }

    private void openUserDialog(final UserDto user) {
        final var dialog = new UserFormDialog(this.userService, this::refreshUserList);
        if (user != null) {
            dialog.editUser(user);
        }
        dialog.open();
    }
}
