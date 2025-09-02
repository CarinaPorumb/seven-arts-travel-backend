package ro.sevenartstravel.mapper;

import org.mapstruct.*;
import ro.sevenartstravel.dto.ArtistDTO;
import ro.sevenartstravel.entity.ArtObject;
import ro.sevenartstravel.entity.Artist;
import ro.sevenartstravel.service.crud.CrudMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArtistMapper extends CrudMapper<ArtistDTO, Artist> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "artObjects", ignore = true)
    Artist toEntity(ArtistDTO dto);

    @Override
    @Mapping(target = "artObjectIds", expression = "java(mapArtObjectsToIds(entity))")
    ArtistDTO toDTO(Artist entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "artObjects", ignore = true)
    void updateEntity(@MappingTarget Artist entity, ArtistDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "artObjects", ignore = true)
    void patchEntity(@MappingTarget Artist entity, ArtistDTO dto);

    default Set<UUID> mapArtObjectsToIds(Artist artist) {
        if (artist.getArtObjects() == null) return Set.of();
        return artist.getArtObjects().stream()
                .map(ArtObject::getId)
                .collect(Collectors.toSet());
    }
}