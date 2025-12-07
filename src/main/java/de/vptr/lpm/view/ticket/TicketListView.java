package de.vptr.lpm.view.ticket;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.vptr.lpm.dto.TicketDto;
import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.TicketService;
import de.vptr.lpm.service.UserService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;

/**
 * View for listing and managing tickets.
 */
@Route(value = "tickets", layout = MainLayout.class)
@PageTitle("Tickets | LPM")
@PermitAll
public class TicketListView extends VerticalLayout implements BeforeEnterObserver {

    @Inject
    TicketService ticketService;

    @Inject
    UserService userService;

    private Grid<TicketDto> grid;
    private ListDataProvider<TicketDto> dataProvider;
    private UserDto currentUser;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        this.currentUser = (UserDto) event.getUI().getSession().getAttribute("user");
        if (this.currentUser == null) {
            event.forwardTo("login");
            return;
        }
        this.initializeContent();
    }

    private void initializeContent() {
        this.setSpacing(true);
        this.setPadding(true);

        // Header
        final var header = new HorizontalLayout();
        final var title = new H2("Tickets");
        final var createButton = new Button("Create Ticket");
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> this.openTicketDialog(null));
        header.add(title, createButton);

        // Search field
        final var searchField = new TextField("Search tickets...");
        searchField.setPlaceholder("Search by title or key");
        searchField.setWidthFull();
        searchField.addValueChangeListener(event -> {
            final var filter = event.getValue().toLowerCase();
            this.dataProvider.setFilter(
                    ticket -> (ticket.title() == null ? "" : ticket.title().toLowerCase()).contains(filter) ||
                            (ticket.ticketKey() == null ? "" : ticket.ticketKey().toLowerCase()).contains(filter));
        });

        // Grid
        this.grid = new Grid<>(TicketDto.class, false);
        this.grid.addColumn(TicketDto::ticketKey).setHeader("Key");
        this.grid.addColumn(TicketDto::title).setHeader("Title");
        this.grid.addColumn(TicketDto::typeName).setHeader("Type");
        this.grid.addColumn(TicketDto::statusName).setHeader("Status");
        this.grid.addColumn(TicketDto::priorityName).setHeader("Priority");
        this.grid.addColumn(TicketDto::assigneeName).setHeader("Assignee");
        this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        this.dataProvider = new ListDataProvider<>(this.ticketService.listAll());
        this.grid.setDataProvider(this.dataProvider);

        this.grid.asSingleSelect().addValueChangeListener(event -> {
            final var ticket = event.getValue();
            if (ticket != null) {
                this.getUI().ifPresent(ui -> ui.navigate("tickets/" + ticket.id()));
            }
        });

        this.add(header, searchField, this.grid);
    }

    private void openTicketDialog(final TicketDto editingTicket) {
        final var dialog = new Dialog();
        dialog.setHeaderTitle(editingTicket == null ? "Create Ticket" : "Edit Ticket");
        dialog.setModal(true);
        dialog.setDraggable(true);

        final var form = new FormLayout();

        final var titleField = new TextField("Title");
        titleField.setRequired(true);
        titleField.setWidthFull();
        if (editingTicket != null) {
            titleField.setValue(editingTicket.title());
        }

        final var descriptionField = new TextField("Description");
        descriptionField.setWidthFull();
        if (editingTicket != null) {
            descriptionField.setValue(editingTicket.description() != null ? editingTicket.description() : "");
        }

        final var saveButton = new Button("Save");
        final var cancelButton = new Button("Cancel", e -> dialog.close());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (titleField.getValue().isBlank()) {
                titleField.setInvalid(true);
                return;
            }
            dialog.close();
        });

        form.add(titleField, descriptionField);
        dialog.add(form);
        dialog.getFooter().add(saveButton, cancelButton);
        dialog.open();
    }
}
