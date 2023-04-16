package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.app.rest.vm.PutProfileDTO;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.service.dto.ProfileDTO;

/**
 * Mapper for the entity {@link Profile} and its DTO {@link ProfileDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, CityMapper.class, LevelMapper.class })
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    @Mapping(target = "city", source = "city", qualifiedByName = "id")
    @Mapping(target = "level", source = "level", qualifiedByName = "id")
    ProfileDTO toDto(Profile s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoId(Profile profile);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Profile entity, PutProfileDTO dto);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget PutProfileDTO dto, Profile entity);

    @Named("partialUpdate")
    @Mapping(target = "username", source = "login")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget PutProfileDTO dto, User entity);
}
