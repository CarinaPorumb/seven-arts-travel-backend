package ro.sevenartstravel.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ro.sevenartstravel.dto.ArtistDTO;
import ro.sevenartstravel.enums.Nationality;
import ro.sevenartstravel.exception.NotFoundException;
import ro.sevenartstravel.service.entity.ArtistService;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;


    @Operation(
            summary = "Retrieve a list of artists",
            description = "Returns a paginated list of artists. " +
                    "Supports optional filters by name, biography, birth year, death year, nationality, or a keyword search."
    )
    @GetMapping
    public ResponseEntity<Page<ArtistDTO>> getArtists(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String biography,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) Integer deathYear,
            @RequestParam(required = false) Nationality nationality,
            Pageable pageable) {

        search = StringUtils.hasText(search) ? search.trim() : null;
        name = StringUtils.hasText(name) ? name.trim() : null;
        biography = StringUtils.hasText(biography) ? biography.trim() : null;

        boolean hasFilters = name != null ||
                biography != null ||
                birthYear != null ||
                deathYear != null ||
                nationality != null;

        if (search != null) {
            log.info("Searching artists with keyword: {}", search);
            return ResponseEntity.ok(artistService.searchArtist(search, pageable));
        } else if (hasFilters) {
            log.info("Listing artists with filters: name={}, biography={}, birthYear={}, deathYear={}, nationality={}",
                    name, biography, birthYear, deathYear, nationality);
            return ResponseEntity.ok(artistService.getAllArtists(name, biography, birthYear, deathYear, nationality, pageable));
        } else {
            log.info("Returning all artists without filters.");
            return ResponseEntity.ok(artistService.getAll(pageable));
        }
    }


    @Operation(
            summary = "Get artist by ID",
            description = "Returns a single artist identified by UUID. Returns 404 if not found."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable("id") UUID id) {
        log.info("Request to get artist by id: {}", id);
        return ResponseEntity.ok(artistService.getById(id)
                .orElseThrow(() -> new NotFoundException("Artist", id)));
    }


    @Operation(
            summary = "Create a new artist",
            description = "Creates a new artist and returns it with HTTP 201 Created and the Location header."
    )
    @PostMapping
    public ResponseEntity<ArtistDTO> create(@Valid @RequestBody ArtistDTO artistDTO, UriComponentsBuilder uriBuilder) {
        log.info("Request to create artist: {}", artistDTO);
        ArtistDTO created = artistService.save(artistDTO);
        URI location = uriBuilder.path("/api/artists/{id}").buildAndExpand(created.getId()).toUri(); // URI for Location header in 201 Created response, pointing to the newly created resource
        return ResponseEntity.created(location).body(created);
    }


    @Operation(
            summary = "Update an existing artist",
            description = "Updates an existing artist by UUID. Returns 404 if the artist is not found."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ArtistDTO> update(@PathVariable("id") UUID id, @Valid @RequestBody ArtistDTO artistDTO) {
        log.info("Request to update artist with id {}: {}", id, artistDTO);
        return ResponseEntity.ok(artistService.update(id, artistDTO)
                .orElseThrow(() -> new NotFoundException("Artist", id)));
    }

    @Operation(
            summary = "Delete artist by ID",
            description = "Deletes the artist identified by UUID. Returns 204 No Content if successful or 404 if not found."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        log.info("Request to delete artist with id {}", id);
        boolean deleted = artistService.delete(id);
        if (!deleted) {
            throw new NotFoundException("Artist", id);
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Patch artist by ID",
            description = "Partially updates an existing artist. Only provided fields are modified. Returns 200 OK with the updated artist or 404 if not found."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ArtistDTO> patch(@PathVariable("id") UUID id, @Valid @RequestBody ArtistDTO artistDTO) {
        log.info("Request to patch artist with id {}: {}", id, artistDTO);
        return ResponseEntity.ok(artistService.patch(id, artistDTO)
                .orElseThrow(() -> new NotFoundException("Artist", id)));
    }

}