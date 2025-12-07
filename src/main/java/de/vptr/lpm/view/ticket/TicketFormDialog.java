package de.vptr.lpm.view.ticket;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import de.vptr.lpm.dto.TicketDto;
import de.vptr.lpm.entity.TicketPriority;
import de.vptr.lpm.entity.TicketType;
import de.vptr.lpm.service.TicketService;
import jakarta.inject.Inject;

/**
 * Reusable dialog for creating and editing tickets.
 */
public class TicketFormDialog extends Dialog {

    @Inject
    TicketService ticketService;

    private final TicketDto editingTicket;
    private final Long projectId;
    private final Long reporterId;
    private final Runnable onSave;

    /**
     * Creates a new ticket form dialog.
     *
     * @param editingTicket the ticket to edit, or null to create new
     * @param projectId     the project ID (required for creation)
     * @param reporterId    the reporter ID (required for creation)
     * @param onSave        callback when ticket is saved
     */
    public TicketFormDialog(final TicketDto editingTicket, final Long projectId, final Long reporterId,
            final Runnable onSave) {
        this.editingTicket = editingTicket;
        this.projectId = projectId;
        this.reporterId = reporterId;
        this.onSave = onSave;

        this.setHeaderTitle(editingTicket == null ? "Create Ticket" : "Edit Ticket");
        this.setModal(true);
        this.setDraggable(true);

        this.initializeContent();
    }

    private void initializeContent() {
        final var form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        final var titleField = new TextField("Title");
        titleField.setRequired(true);
        titleField.setWidthFull();
        if (this.editingTicket != null) {
            titleField.setValue(this.editingTicket.title());
        }

        final var descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();
        if (this.editingTicket != null) {
            descriptionField.setValue(this.editingTicket.description() != null ? this.editingTicket.description() : "");
        }

        final var typeSelect = new Select<TicketType>();
        typeSelect.setLabel("Type");
        typeSelect.setItems(TicketType.<TicketType>listAll());
        typeSelect.setItemLabelGenerator(t -> t.name);
        if (this.editingTicket != null && this.editingTicket.typeId() != null) {
            final var type = TicketType.<TicketType>findById(this.editingTicket.typeId());
            if (type != null) {
                typeSelect.setValue(type);
            }
        }

        final var prioritySelect = new Select<TicketPriority>();
        prioritySelect.setLabel("Priority");
        prioritySelect.setItems(TicketPriority.<TicketPriority>listAll());
        prioritySelect.setItemLabelGenerator(p -> p.name);
        if (this.editingTicket != null && this.editingTicket.priorityId() != null) {
            final var priority = TicketPriority.<TicketPriority>findById(this.editingTicket.priorityId());
            if (priority != null) {
                prioritySelect.setValue(priority);
            }
        }

        form.add(
                titleField,
                descriptionField,
                typeSelect,
                prioritySelect);

        final var saveButton = new Button("Save");
        final var cancelButton = new Button("Cancel", e -> this.close());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (titleField.getValue().isBlank()) {
                titleField.setInvalid(true);
                return;
            }
            if (this.editingTicket == null) {
                this.ticketService.createTicket(this.projectId, titleField.getValue(), descriptionField.getValue(),
                        this.reporterId);
            } else {
                this.ticketService.updateTicket(this.editingTicket.id(), titleField.getValue(),
                        descriptionField.getValue());
            }
            this.onSave.run();
            this.close();
        });

        this.add(form);
        this.getFooter().add(saveButton, cancelButton);
    }
}
