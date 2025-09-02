package ro.sevenartstravel.mapper;

import org.mapstruct.*;
import ro.sevenartstravel.dto.ContactMessageDTO;
import ro.sevenartstravel.entity.ContactMessage;
import ro.sevenartstravel.service.crud.CrudMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactMessageMapper extends CrudMapper<ContactMessageDTO, ContactMessage> {

    @Override
    @Mapping(target = "id", ignore = true)
    ContactMessage toEntity(ContactMessageDTO dto);

    @Override
    ContactMessageDTO toDTO(ContactMessage contactMessage);

    @Override
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget ContactMessage entity, ContactMessageDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void patchEntity(@MappingTarget ContactMessage entity, ContactMessageDTO dto);

}