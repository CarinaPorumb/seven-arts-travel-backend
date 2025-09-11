package ro.sevenartstravel.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ro.sevenartstravel.dto.ContactMessageDTO;
import ro.sevenartstravel.exception.NotFoundException;
import ro.sevenartstravel.service.entity.ContactMessageService;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/contact-messages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactService;

    @Operation(
            summary = "Retrieve a list of contact messages",
            description = "Returns a paginated list of contact messages. Supports optional filters by username, email, subject, message. createdDate, or a keyword search."
    )
    @GetMapping
    public ResponseEntity<Page<ContactMessageDTO>> getContactMessages(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
            Pageable pageable) {

        search = StringUtils.hasText(search) ? search.trim() : null;
        username = StringUtils.hasText(username) ? username.trim() : null;
        email = StringUtils.hasText(email) ? email.trim() : null;
        subject = StringUtils.hasText(subject) ? subject.trim() : null;
        message = StringUtils.hasText(message) ? message.trim() : null;

        boolean hasFilters = username != null ||
                email != null ||
                subject != null ||
                message != null ||
                createdDate != null;

        if (search != null) {
            log.info("Searching contact messages with keyword: '{}'", search);
            return ResponseEntity.ok(contactService.searchContactMessages(search, pageable));
        } else if (hasFilters) {
            log.info("Listing contact messages with filters: username='{}', email='{}', subject='{}', message='{}', createdDate='{}'"
                    , username, email, subject, message, createdDate);

            return ResponseEntity.ok(contactService.getAllContactMessages(username, email, subject, message, createdDate, pageable));
        } else {
            log.info("Returning all contact messages without filters.");
            return ResponseEntity.ok(contactService.getAll(pageable));
        }
    }


    @Operation(
            summary = "Get contact message by ID",
            description = "Returns a single contact message identified by UUID. Returns 404 if not found."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ContactMessageDTO> getContactMessageById(@PathVariable("id") UUID id) {
        log.info("Request to get contact message by id: {}", id);
        return ResponseEntity.ok(contactService.getById(id)
                .orElseThrow(() -> new NotFoundException("ContactMessage", id)));
    }


    @Operation(
            summary = "Create a new contact message",
            description = "Creates a new contact message and returns it with HTTP 201 Created and the Location header")
    @PostMapping
    public ResponseEntity<ContactMessageDTO> create(@Valid @RequestBody ContactMessageDTO contactMessageDTO, UriComponentsBuilder uriBuilder) {
        log.info("Request to create contact message: {}", contactMessageDTO);
        ContactMessageDTO created = contactService.save(contactMessageDTO);
        URI location = uriBuilder.path("/api/contact-messages/{id}").buildAndExpand(created.getId()).toUri(); // URI for Location header in 201 Created response, pointing to the newly created resource
        return ResponseEntity.created(location).body(created);
    }


    @Operation(summary = "Update an existing contact message",
            description = "Updates an existing contact message by UUID. Returns 404 if the contact message is not found.")
    @PutMapping("/{id}")
    public ResponseEntity<ContactMessageDTO> update(@PathVariable("id") UUID id, @Valid @RequestBody ContactMessageDTO contactMessageDTO) {
        log.info("Request to update contact message with id {}: {}", id, contactMessageDTO);
        return ResponseEntity.ok(contactService.update(id, contactMessageDTO)
                .orElseThrow(() -> new NotFoundException("ContactMessage", id)));
    }


    @Operation(
            summary = "Delete contact message by ID",
            description = "Deletes the contact message identified by UUID. Returns 204 No Content if successful or 404 if not found."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        log.info("Request to delete contact message with id {}", id);
        boolean deleted = contactService.delete(id);
        if (!deleted) {
            throw new NotFoundException("ContactMessage", id);
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Patch contact message by ID",
            description = "Partially updates an existing contact message. Only provided fields are modified. Returns 200 OK with the updated contact message or 404 if not found."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ContactMessageDTO> patch(@PathVariable("id") UUID id, @RequestBody ContactMessageDTO contactMessageDTO) {
        log.info("Request to patch contact message with id {}: {}", id, contactMessageDTO);
        return ResponseEntity.ok(contactService.patch(id, contactMessageDTO)
                .orElseThrow(() -> new NotFoundException("ContactMessage", id)));
    }

}