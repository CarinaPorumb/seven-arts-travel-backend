package ro.sevenartstravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ro.sevenartstravel.entity.ContactMessage;

import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID>, JpaSpecificationExecutor<ContactMessage> {

}