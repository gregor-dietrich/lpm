package de.vptr.lpm.view.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
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
public class UserManagementView extends VerticalLayout implements BeforeEnterObserver {

    @Inject
    transient UserService userService;

    private Grid<UserDto> userGrid;
    private boolean initialized = false;

    /**
     * Initializes the user management view with grid and controls.
     */
    public UserManagementView() {
        this.setPadding(true);
        this.setSpacing(true);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (this.initialized) {
            return;
        }

        final var currentUser = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null || currentUser.roles().stream()
                .noneMatch(r -> "ADMIN".equals(r.name()))) {
            event.forwardTo("dashboard");
            return;
        }

        this.initializeContent();
        this.initialized = true;
    }

    private void initializeContent() {

        final var title = new H2("User Management");
        final var createButton = new Button("Create User", event -> this.openUserDialog(null));

        this.userGrid = new Grid<>(UserDto.class);
        this.userGrid.setColumns("id", "username", "email", "displayName", "status");
        this.userGrid.addColumn(user -> "Edit / Delete")
                .setHeader("Actions")
                .setWidth("150px")
                .setRenderer(new com.vaadin.flow.data.renderer.ComponentRenderer<>(user -> {
                    final var editBtn = new Button("Edit", event -> this.openUserDialog(user));
                    editBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL);
                    final var deleteBtn = new Button("Delete", event -> this.deleteUser(user));
                    deleteBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL,
                            com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
                    final var layout = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(editBtn, deleteBtn);
                    layout.setSpacing(true);
                    return layout;
                }));

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

    private void deleteUser(final UserDto user) {
        final var confirmDialog = new com.vaadin.flow.component.dialog.Dialog();
        confirmDialog.setHeaderTitle("Confirm Delete");
        confirmDialog.add(new com.vaadin.flow.component.html.Paragraph(
                "Delete user \"" + user.username() + "\"? This cannot be undone."));

        final var deleteBtn = new Button("Delete", event -> {
            try {
                this.userService.deleteUser(user.id());
                this.refreshUserList();
                confirmDialog.close();
                com.vaadin.flow.component.notification.Notification.show("User deleted successfully");
            } catch (final Exception e) {
                com.vaadin.flow.component.notification.Notification.show("Error deleting user: " + e.getMessage());
            }
        });
        deleteBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);

        final var cancelBtn = new Button("Cancel", event -> confirmDialog.close());

        confirmDialog.getFooter().add(cancelBtn, deleteBtn);
        confirmDialog.open();
    }
}
