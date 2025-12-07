package de.vptr.lpm.view.ticket;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import de.vptr.lpm.dto.TicketDto;
import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.service.TicketService;
import de.vptr.lpm.view.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;

/**
 * View for displaying detailed ticket information.
 */
@Route(value = "tickets/:ticketId", layout = MainLayout.class)
@PageTitle("Ticket Details | LPM")
@PermitAll
public class TicketDetailView extends VerticalLayout
        implements BeforeEnterObserver, HasUrlParameter<String> {

    @Inject
    TicketService ticketService;

    private TicketDto currentTicket;
    private UserDto currentUser;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        this.currentUser = (UserDto) event.getUI().getSession().getAttribute("user");
        if (this.currentUser == null) {
            event.forwardTo("login");
            return;
        }
    }

    @Override
    public void setParameter(final com.vaadin.flow.router.BeforeEvent event, final String ticketId) {
        try {
            final var id = Long.parseLong(ticketId);
            this.currentTicket = this.ticketService.findById(id).orElse(null);
            if (this.currentTicket == null) {
                event.forwardTo("tickets");
                return;
            }
            this.initializeContent();
        } catch (final NumberFormatException e) {
            event.forwardTo("tickets");
        }
    }

    private void initializeContent() {
        this.setSpacing(true);
        this.setPadding(true);

        final var backButton = new Button("← Back to Tickets");
        backButton.addClickListener(e -> this.getUI().ifPresent(ui -> ui.navigate("tickets")));

        final var title = new H2(this.currentTicket.ticketKey() + ": " + this.currentTicket.title());

        final var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        form.addFormItem(this.createReadOnlyField(this.currentTicket.description()),
                "Description");
        form.addFormItem(this.createReadOnlyField(this.currentTicket.typeName()),
                "Type");
        form.addFormItem(this.createReadOnlyField(this.currentTicket.statusName()),
                "Status");
        form.addFormItem(this.createReadOnlyField(this.currentTicket.priorityName()),
                "Priority");
        form.addFormItem(
                this.createReadOnlyField(
                        this.currentTicket.assigneeName() != null ? this.currentTicket.assigneeName() : "(Unassigned)"),
                "Assignee");
        form.addFormItem(this.createReadOnlyField(this.currentTicket.reporterName()),
                "Reporter");
        form.addFormItem(
                this.createReadOnlyField(
                        this.currentTicket.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))),
                "Created");
        form.addFormItem(
                this.createReadOnlyField(
                        this.currentTicket.updatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))),
                "Updated");

        final var actions = new HorizontalLayout();
        final var editButton = new Button("Edit");
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClickListener(e -> {
            // Edit functionality would be added here
        });

        final var deleteButton = new Button("Delete");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            // Delete functionality would be added here
        });

        actions.add(editButton, deleteButton);

        this.add(backButton, title, form, actions);
    }

    private Paragraph createReadOnlyField(final String value) {
        final var paragraph = new Paragraph(value != null ? value : "");
        paragraph.setWidthFull();
        return paragraph;
    }
}
