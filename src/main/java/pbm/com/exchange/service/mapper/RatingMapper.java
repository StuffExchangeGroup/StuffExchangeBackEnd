package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Rating;
import pbm.com.exchange.service.dto.RatingDTO;

/**
 * Mapper for the entity {@link Rating} and its DTO {@link RatingDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RatingMapper extends EntityMapper<RatingDTO, Rating> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RatingDTO toDtoId(Rating rating);
}
