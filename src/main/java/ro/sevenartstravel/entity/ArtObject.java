package ro.sevenartstravel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ro.sevenartstravel.enums.ArtCategory;
import ro.sevenartstravel.enums.ArtObjectType;
import ro.sevenartstravel.enums.Status;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "art_object")
@ToString
public class ArtObject extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "artobject_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name must not be blank")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    private String longDescription;

    private Integer year;  // for artWork

    private String location;

    private String imageUrl;

    private String externalUrl;

    private LocalDateTime startTime; //for events

    private LocalDateTime endTime; //for events

    private Boolean isTemporary;   //for events

    @Enumerated(EnumType.STRING)
    private Status status; // for events

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtCategory artCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtObjectType artObjectType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_id")
    private Movement movement;


    @ManyToMany(mappedBy = "artObjects", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<Artist> artists = new HashSet<>();

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtObject other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }

}