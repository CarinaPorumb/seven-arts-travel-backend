package ro.sevenartstravel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ro.sevenartstravel.enums.Nationality;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class Artist extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "artist_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    private String biography;
    private String imageUrl;

    @Min(value = -1000, message = "Birth year must be a realistic historical value (min: 1000 BC written -1000)")
    @Max(value = 2025, message = "Birth year cannot be in future")
    private Integer birthYear;

    @Min(value = -1000, message = "Death year must be a realistic historical value (min: 1000 BC written -1000)")
    @Max(value = 2025, message = "Death year cannot be in future")
    private Integer deathYear;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "artist_art_object",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "art_object_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<ArtObject> artObjects = new HashSet<>();

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }

}