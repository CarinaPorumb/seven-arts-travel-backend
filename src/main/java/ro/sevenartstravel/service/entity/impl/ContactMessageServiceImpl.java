package ro.sevenartstravel.service.entity.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.sevenartstravel.dto.ContactMessageDTO;
import ro.sevenartstravel.entity.ContactMessage;
import ro.sevenartstravel.mapper.ContactMessageMapper;
import ro.sevenartstravel.repository.ContactMessageRepository;
import ro.sevenartstravel.service.crud.CrudServiceImpl;
import ro.sevenartstravel.service.entity.ContactMessageService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import static ro.sevenartstravel.service.util.SpecificationUtils.*;

@Slf4j
@Service
public class ContactMessageServiceImpl extends CrudServiceImpl<ContactMessageDTO, ContactMessage, UUID> implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository, ContactMessageMapper contactMessageMapper) {
        super(contactMessageRepository, contactMessageMapper);
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
    }

    private Specification<ContactMessage> buildSpecification(String username, String email, String subject,
                                                             String message, LocalDate createdDate) {
        Specification<ContactMessage> specification = null;

        specification = andSpec(specification, likeIfPresent("username", username));
        specification = andSpec(specification, likeIfPresent("email", email));
        specification = andSpec(specification, likeIfPresent("subject", subject));
        specification = andSpec(specification, likeIfPresent("message", message));
        specification = andSpec(specification, onDayIfPresent("createdDate", createdDate));

        return specification;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageDTO> getAllContactMessages(String username, String email, String subject,
                                                         String message, LocalDate createdDate,
                                                         Pageable pageable) {
        Specification<ContactMessage> specification = buildSpecification(username, email, subject, message, createdDate);
        log.debug("Listing contact messages with filters: username={}, email={}, subject={}, message={}, createdDate={}",
                username, email, subject, message, createdDate);
        return contactMessageRepository.findAll(specification, pageable).map(contactMessageMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageDTO> searchContactMessages(String keyword, Pageable pageable) {
        log.debug("Searching contact messages with keyword: {}", keyword);

        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }
        String value = keyword.trim();

        Specification<ContactMessage> specification = null;

        specification = orSpec(specification, likeIfPresent("username", keyword));
        specification = orSpec(specification, likeIfPresent("email", keyword));
        specification = orSpec(specification, likeIfPresent("subject", keyword));
        specification = orSpec(specification, likeIfPresent("message", keyword));

        try {
            LocalDate day = LocalDate.parse(value);
            specification = orSpec(specification, onDayIfPresent("createdDate", day));
        } catch (DateTimeParseException e) {
            log.debug("Keyword '{}' is not a valid yyyy-MM-dd date; skipping date filter.", value);
        }

        return contactMessageRepository.findAll(specification, pageable)
                .map(contactMessageMapper::toDTO);
    }
}