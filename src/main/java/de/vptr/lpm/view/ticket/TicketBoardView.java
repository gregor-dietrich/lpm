package de.vptr.lpm.view.ticket;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.dto.TicketDto;
import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.entity.TicketStatus;
import de.vptr.lpm.service.ProjectService;
import de.vptr.lpm.service.TicketService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;

/**
 * Kanban board view for tickets organized by status.
 */
@Route(value = "board", layout = MainLayout.class)
@PageTitle("Ticket Board | LPM")
@PermitAll
public class TicketBoardView extends VerticalLayout implements BeforeEnterObserver {

    @Inject
    TicketService ticketService;

    @Inject
    ProjectService projectService;

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

        final var header = new HorizontalLayout();
        final var title = new H2("Ticket Board");
        final var createButton = new Button("Create Ticket");
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> this.openTicketDialog());
        header.add(title, createButton);

        final var boardContainer = new HorizontalLayout();
        boardContainer.setWidthFull();
        boardContainer.setSpacing(true);

        // Get all statuses and create columns
        final var statuses = TicketStatus.<TicketStatus>listAll();
        final var tickets = this.ticketService.listAll();

        for (final var status : statuses) {
            final var column = this.createStatusColumn(status, tickets);
            boardContainer.add(column);
        }

        this.add(header, boardContainer);
    }

    private VerticalLayout createStatusColumn(final TicketStatus status, final List<TicketDto> allTickets) {
        final var column = new VerticalLayout();
        column.setWidthFull();
        column.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "4px")
                .set("padding", "12px")
                .set("background-color", "var(--lumo-base-color)");

        final var statusTitle = new H4(status.name);
        statusTitle.getStyle().set("margin-top", "0");

        // Filter tickets by status
        final var ticketsInStatus = allTickets.stream()
                .filter(t -> status.id.equals(t.statusId()))
                .collect(Collectors.toList());

        final var ticketsContainer = new VerticalLayout();
        ticketsContainer.setSpacing(true);
        ticketsContainer.setPadding(false);

        for (final var ticket : ticketsInStatus) {
            final var card = this.createTicketCard(ticket);
            ticketsContainer.add(card);
        }

        column.add(statusTitle, ticketsContainer);
        return column;
    }

    private VerticalLayout createTicketCard(final TicketDto ticket) {
        final var card = new VerticalLayout();
        card.getStyle()
                .set("background-color", "var(--lumo-secondary-text-color)")
                .set("border-radius", "4px")
                .set("padding", "12px")
                .set("cursor", "pointer");

        final var key = new Span(ticket.ticketKey());
        key.getStyle().set("font-weight", "bold")
                .set("font-size", "12px")
                .set("color", "var(--lumo-primary-text-color)");

        final var cardTitle = new Span(ticket.title());
        cardTitle.getStyle().set("word-wrap", "break-word")
                .set("white-space", "normal");

        card.add(key, cardTitle);
        card.setSpacing(false);
        card.setPadding(false);

        card.addClickListener(e -> {
            this.getUI().ifPresent(ui -> ui.navigate("tickets/" + ticket.id()));
        });

        return card;
    }

    private void openTicketDialog() {
        final var dialog = new Dialog();
        dialog.setHeaderTitle("Create Ticket");
        dialog.setModal(true);
        dialog.setDraggable(true);

        final var form = new FormLayout();

        final var projectSelect = new Select<ProjectDto>();
        projectSelect.setLabel("Project");
        projectSelect.setItems(this.projectService.listAll());
        projectSelect.setItemLabelGenerator(ProjectDto::name);
        projectSelect.setWidthFull();

        final var saveButton = new Button("Create");
        final var cancelButton = new Button("Cancel", e -> dialog.close());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (projectSelect.getValue() == null) {
                projectSelect.setInvalid(true);
                return;
            }
            new TicketFormDialog(null, projectSelect.getValue().id(), this.currentUser.id(), () -> {
                this.initializeContent();
                dialog.close();
            }).open();
        });

        form.add(projectSelect);
        dialog.add(form);
        dialog.getFooter().add(saveButton, cancelButton);
        dialog.open();
    }
}
