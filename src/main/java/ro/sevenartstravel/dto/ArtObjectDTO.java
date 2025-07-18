package ro.sevenartstravel.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ro.sevenartstravel.enums.ArtCategory;
import ro.sevenartstravel.enums.ArtObjectType;
import ro.sevenartstravel.enums.Status;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class ArtObjectDTO {

    private UUID id;
    @NotBlank(message = "Name must not be blank")
    private String title;
    private String shortDescription;
    private String longDescription;
    private Integer year;
    private ArtCategory artCategory;
    private String location;
    private String imageUrl;
    private String externalUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isTemporary;
    private Status status;
    private ArtObjectType artObjectType;

    private UUID movementId;
    private Set<UUID> artistIds = new HashSet<>();

}