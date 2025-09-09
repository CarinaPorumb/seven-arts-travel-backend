package ro.sevenartstravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ro.sevenartstravel.entity.Artist;
import ro.sevenartstravel.enums.Nationality;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID>, JpaSpecificationExecutor<Artist> {

    Optional<Artist> findByNameIgnoreCase(String name);

    Optional<Artist> findFirstByNameIgnoreCaseAndNationalityAndBirthYear(String name, Nationality nationality, Integer birthYear);

}