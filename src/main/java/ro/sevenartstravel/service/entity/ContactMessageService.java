package ro.sevenartstravel.service.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ro.sevenartstravel.dto.ContactMessageDTO;
import ro.sevenartstravel.service.crud.CrudService;

import java.time.LocalDate;
import java.util.UUID;

public interface ContactMessageService extends CrudService<ContactMessageDTO, UUID> {

    Page<ContactMessageDTO> getAllContactMessages(String username, String email, String subject, String message,
                                                  LocalDate createdDate, Pageable pageable);

    Page<ContactMessageDTO> searchContactMessages(String keyword, Pageable pageable);

}