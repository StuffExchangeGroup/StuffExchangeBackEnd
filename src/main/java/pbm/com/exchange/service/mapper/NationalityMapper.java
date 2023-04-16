package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Nationality;
import pbm.com.exchange.service.dto.NationalityDTO;

/**
 * Mapper for the entity {@link Nationality} and its DTO {@link NationalityDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface NationalityMapper extends EntityMapper<NationalityDTO, Nationality> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NationalityDTO toDtoId(Nationality nationality);
}
