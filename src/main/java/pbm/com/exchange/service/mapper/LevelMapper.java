package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Level;
import pbm.com.exchange.service.dto.LevelDTO;

/**
 * Mapper for the entity {@link Level} and its DTO {@link LevelDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LevelMapper extends EntityMapper<LevelDTO, Level> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LevelDTO toDtoId(Level level);
}
