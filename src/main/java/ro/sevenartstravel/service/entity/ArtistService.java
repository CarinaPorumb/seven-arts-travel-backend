package ro.sevenartstravel.service.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ro.sevenartstravel.dto.ArtistDTO;
import ro.sevenartstravel.enums.Nationality;
import ro.sevenartstravel.service.crud.CrudService;

import java.util.UUID;

public interface ArtistService extends CrudService<ArtistDTO, UUID> {

    Page<ArtistDTO> getAllArtists(String name, String biography, Integer birthYear, Integer deathYear, Nationality nationality, Pageable pageable);

    Page<ArtistDTO> searchArtist(String keyword, Pageable pageable);

}