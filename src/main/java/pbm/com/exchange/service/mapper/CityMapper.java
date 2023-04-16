package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.City;
import pbm.com.exchange.service.dto.CityDTO;

/**
 * Mapper for the entity {@link City} and its DTO {@link CityDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProvinceMapper.class })
public interface CityMapper extends EntityMapper<CityDTO, City> {
    @Mapping(target = "province", source = "province", qualifiedByName = "id")
    CityDTO toDto(City s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CityDTO toDtoId(City city);
}
