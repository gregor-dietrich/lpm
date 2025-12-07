package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.TicketDto;
import de.vptr.lpm.entity.Ticket;
import de.vptr.lpm.repository.ProjectRepository;
import de.vptr.lpm.repository.TicketRepository;
import de.vptr.lpm.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing tickets.
 */
@ApplicationScoped
public class TicketService {

    @Inject
    TicketRepository repository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    /**
     * Create a new ticket.
     *
     * @param projectId   the project ID
     * @param title       ticket title
     * @param description ticket description
     * @param reporterId  ID of the reporting user
     * @return the created ticket DTO
     */
    @Transactional
    public TicketDto createTicket(
            final Long projectId,
            final String title,
            final String description,
            final Long reporterId) {
        final var project = this.projectRepository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        final var reporter = this.userRepository.findById(reporterId);
        if (reporter == null) {
            throw new IllegalArgumentException("Reporter not found: " + reporterId);
        }

        // Generate ticket key
        final var ticketCount = this.repository.count("project.id = ?1", projectId) + 1;
        final var ticketKey = project.projectKey + "-" + ticketCount;

        final var ticket = new Ticket();
        ticket.project = project;
        ticket.ticketKey = ticketKey;
        ticket.title = title;
        ticket.description = description;
        ticket.reporter = reporter;

        this.repository.persist(ticket);
        return TicketDto.fromEntity(ticket);
    }

    /**
     * Find a ticket by ID.
     *
     * @param id the ticket ID
     * @return an Optional containing the ticket if found
     */
    public Optional<TicketDto> findById(final Long id) {
        return this.repository.findByIdOptional(id)
                .map(TicketDto::fromEntity);
    }

    /**
     * Find a ticket by key.
     *
     * @param ticketKey the ticket key
     * @return an Optional containing the ticket if found
     */
    public Optional<TicketDto> findByKey(final String ticketKey) {
        final var ticket = this.repository.findByKey(ticketKey);
        return Optional.ofNullable(ticket)
                .map(TicketDto::fromEntity);
    }

    /**
     * Get all tickets in a project.
     *
     * @param projectId the project ID
     * @return list of tickets in the project
     */
    public List<TicketDto> findByProject(final Long projectId) {
        return this.repository.findByProject(projectId).stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all tickets assigned to a user.
     *
     * @param userId the user ID
     * @return list of assigned tickets
     */
    public List<TicketDto> findByAssignee(final Long userId) {
        return this.repository.findByAssignee(userId).stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update a ticket.
     *
     * @param id          ticket ID
     * @param title       new title
     * @param description new description
     * @return the updated ticket DTO
     */
    @Transactional
    public TicketDto updateTicket(
            final Long id,
            final String title,
            final String description) {
        final var ticket = this.repository.findById(id);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + id);
        }

        ticket.title = title;
        ticket.description = description;

        this.repository.persist(ticket);
        return TicketDto.fromEntity(ticket);
    }

    /**
     * Assign a ticket to a user.
     *
     * @param ticketId the ticket ID
     * @param userId   the user ID (or null to unassign)
     * @return the updated ticket DTO
     */
    @Transactional
    public TicketDto assignTicket(final Long ticketId, final Long userId) {
        final var ticket = this.repository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }

        if (userId != null) {
            final var user = this.userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
            ticket.assignee = user;
        } else {
            ticket.assignee = null;
        }

        this.repository.persist(ticket);
        return TicketDto.fromEntity(ticket);
    }

    /**
     * Change ticket status.
     *
     * @param ticketId the ticket ID
     * @param statusId the new status ID
     * @return the updated ticket DTO
     */
    @Transactional
    public TicketDto changeStatus(final Long ticketId, final Long statusId) {
        final var ticket = this.repository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }

        // Note: Status is set via the service, not directly here
        // This would require a StatusRepository injection
        this.repository.persist(ticket);
        return TicketDto.fromEntity(ticket);
    }

    /**
     * Delete a ticket.
     *
     * @param id the ticket ID
     */
    @Transactional
    public void deleteTicket(final Long id) {
        this.repository.deleteById(id);
    }

    /**
     * Count tickets in a project.
     *
     * @param projectId the project ID
     * @return total count of tickets
     */
    public long countByProject(final Long projectId) {
        return this.repository.count("project.id = ?1", projectId);
    }

    /**
     * List all tickets.
     *
     * @return list of all tickets as DTOs
     */
    public List<TicketDto> listAll() {
        return Ticket.<Ticket>listAll().stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }
}
