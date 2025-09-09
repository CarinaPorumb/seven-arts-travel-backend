package ro.sevenartstravel.service.entity.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.sevenartstravel.csv.ArtistCSV;
import ro.sevenartstravel.dto.ArtistDTO;
import ro.sevenartstravel.entity.Artist;
import ro.sevenartstravel.enums.Nationality;
import ro.sevenartstravel.exception.CsvProcessingException;
import ro.sevenartstravel.mapper.ArtistMapper;
import ro.sevenartstravel.repository.ArtistRepository;
import ro.sevenartstravel.service.crud.CrudServiceImpl;
import ro.sevenartstravel.service.entity.ArtistService;
import ro.sevenartstravel.service.util.CsvService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ro.sevenartstravel.service.util.SpecificationUtils.*;

@Slf4j
@Service
public class ArtistServiceImpl extends CrudServiceImpl<ArtistDTO, Artist, UUID> implements ArtistService, CsvService<ArtistCSV> {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        super(artistRepository, artistMapper);
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
    }

    private Specification<Artist> buildSpecification(String name, String biography, Integer birthYear, Integer deathYear, Nationality nationality) {
        Specification<Artist> specification = null;

        specification = andSpec(specification, likeIfPresent("name", name));
        specification = andSpec(specification, likeIfPresent("biography", biography));
        specification = andSpec(specification, equalsIfPresent("birthYear", birthYear));
        specification = andSpec(specification, equalsIfPresent("deathYear", deathYear));
        specification = andSpec(specification, equalsIfPresent("nationality", nationality));

        return specification;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> getAllArtists(String name, String biography, Integer birthYear, Integer deathYear, Nationality nationality, Pageable pageable) {
        Specification<Artist> specification = buildSpecification(name, biography, birthYear, deathYear, nationality);
        log.debug("Listing artists with filters: name={}, biography={}, birthYear={}, deathYear={}, nationality={}", name, biography, birthYear, deathYear, nationality);
        return artistRepository.findAll(specification, pageable).map(artistMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> searchArtist(String keyword, Pageable pageable) {
        log.debug("Searching artists with keyword: {}", keyword);

        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }

        Specification<Artist> specification = null;
        specification = orSpec(specification, likeIfPresent("name", keyword));
        specification = orSpec(specification, likeIfPresent("biography", keyword));

        Nationality nationality = parseNationality(keyword);

        if (nationality != null) {
            specification = orSpec(specification, equalsIfPresent("nationality", nationality));
        }

        return artistRepository.findAll(specification, pageable).map(artistMapper::toDTO);
    }

    private Nationality parseNationality(String keyword) {
        try {
            return Nationality.valueOf(keyword.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.debug("Keyword is not a valid Nationality enum.");
            return null;
        }
    }


    // == CSV ==
    @Override
    @Transactional
    public void convertCSV(File csvFile) {
        log.debug("Processing artist CSV file: {}", csvFile.getName());

        try (FileReader fileReader = new FileReader(csvFile)) {
            List<ArtistCSV> rows = new CsvToBeanBuilder<ArtistCSV>(fileReader)
                    .withType(ArtistCSV.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            if (rows.isEmpty()) {
                log.warn("CSV {} is empty. No artists imported.", csvFile.getName());
                return;
            }

            List<Artist> toSave = new ArrayList<>();

            rows.forEach(elem -> {
                try {
                    String rawName = elem.getName();

                    if (rawName == null || rawName.isBlank()) {
                        log.warn("Skipping row with empty name.");
                        return;
                    }
                    String name = rawName.trim();

                    validateCsvFields(elem);

                    Optional<Artist> existing = artistRepository.findByNameIgnoreCase(name);

                    if (existing.isPresent()) {
                        Artist entity = existing.get();
                        applyCSVtoEntity(elem, entity);
                        toSave.add(entity);
                        log.debug("Updated existing artist by name: {}", name);
                    } else {
                        ArtistDTO dto = convertCsvToDto(elem);
                        dto.setName(name);
                        Artist newEntity = artistMapper.toEntity(dto);
                        toSave.add(newEntity);
                        log.debug("Prepared new artist: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Error for artist {}: {}", elem.getName(), e.getMessage());
                }
            });

            if (!toSave.isEmpty()) {
                artistRepository.saveAll(toSave);
                log.info("Successfully saved {} artists from CSV {}", toSave.size(), csvFile.getName());
            } else {
                log.info("No valid artists to save from CSV {}", csvFile.getName());
            }
        } catch (IOException e) {
            log.error("Could not read CSV file: {}", csvFile.getName(), e);
            throw new CsvProcessingException("CSV read failure", e);
        }
    }

    private void applyCSVtoEntity(ArtistCSV csv, Artist entity) {
        if (csv.getBiography() != null && !csv.getBiography().isBlank()) {
            entity.setBiography(csv.getBiography());
        }
        if (csv.getImageUrl() != null && !csv.getImageUrl().isBlank()) {
            entity.setImageUrl(csv.getImageUrl());
        }
        Nationality nationality = checkNationalityOrNull(csv.getNationality());
        if (nationality != null) {
            entity.setNationality(nationality);
        }
        if (csv.getBirthYear() != null) {
            entity.setBirthYear(csv.getBirthYear());
        }
        if (csv.getDeathYear() != null) {
            entity.setDeathYear(csv.getDeathYear());
        }
    }

    private Nationality checkNationalityOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Nationality.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private ArtistDTO convertCsvToDto(ArtistCSV csv) {
        ArtistDTO dto = new ArtistDTO();
        dto.setName(csv.getName());
        dto.setBiography(csv.getBiography());
        dto.setImageUrl(csv.getImageUrl());
        dto.setBirthYear(csv.getBirthYear());
        dto.setDeathYear(csv.getDeathYear());
        dto.setNationality(checkNationalityOrNull(csv.getNationality()));
        return dto;
    }

    private void validateCsvFields(ArtistCSV csv) {
        int currentYear = java.time.Year.now().getValue();
        Integer birthYear = csv.getBirthYear();
        Integer deathYear = csv.getDeathYear();

        if (birthYear != null && birthYear > currentYear) {
            throw new CsvProcessingException("Birth year cannot be in the future for artist: " + csv.getName());
        }
        if (deathYear != null && deathYear > currentYear) {
            throw new CsvProcessingException("Death year cannot be in the future for artist: " + csv.getName());

        }
        if (birthYear != null && deathYear != null && birthYear > deathYear) {
            throw new CsvProcessingException("Birth year cannot be after death year for artist: " + csv.getName());
        }

    }

}