package ro.sevenartstravel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ro.sevenartstravel.dto.UserCreateDTO;
import ro.sevenartstravel.dto.UserDTO;
import ro.sevenartstravel.dto.UserUpdateDTO;
import ro.sevenartstravel.entity.Role;
import ro.sevenartstravel.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRolesToNames(user))")
    UserDTO toUser(User user);

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "emailConfirm", ignore = true)
    @Mapping(target = "passwordConfirm", ignore = true)
    @Mapping(target = "randomToken", ignore = true)
    @Mapping(target = "randomTokenEmail", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "version", ignore = true)
    User toEntity(UserCreateDTO dto);

    //UPDATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordConfirm", ignore = true)
    @Mapping(target = "emailConfirm", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "randomToken", ignore = true)
    @Mapping(target = "randomTokenEmail", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateUserEntity(@MappingTarget User entity, UserUpdateDTO dto);

    // PATCH
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordConfirm", ignore = true)
    @Mapping(target = "emailConfirm", ignore = true)
    @Mapping(target = "randomToken", ignore = true)
    @Mapping(target = "randomTokenEmail", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void patchUserEntity(@MappingTarget User entity, UserUpdateDTO dto);

    default Set<String> mapRolesToNames(User user) {
        if (user.getRoles() == null) return Set.of();

        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}