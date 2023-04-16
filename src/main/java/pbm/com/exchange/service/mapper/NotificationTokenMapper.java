package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.NotificationToken;
import pbm.com.exchange.service.dto.NotificationTokenDTO;

/**
 * Mapper for the entity {@link NotificationToken} and its DTO {@link NotificationTokenDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProfileMapper.class })
public interface NotificationTokenMapper extends EntityMapper<NotificationTokenDTO, NotificationToken> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "id")
    NotificationTokenDTO toDto(NotificationToken s);
}
