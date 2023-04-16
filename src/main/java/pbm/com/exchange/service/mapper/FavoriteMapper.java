package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Favorite;
import pbm.com.exchange.service.dto.FavoriteDTO;

/**
 * Mapper for the entity {@link Favorite} and its DTO {@link FavoriteDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, ProfileMapper.class })
public interface FavoriteMapper extends EntityMapper<FavoriteDTO, Favorite> {
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "id")
    FavoriteDTO toDto(Favorite s);
}
