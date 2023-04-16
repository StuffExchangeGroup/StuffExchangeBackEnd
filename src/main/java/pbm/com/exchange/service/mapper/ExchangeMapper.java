package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Exchange;
import pbm.com.exchange.service.dto.ExchangeDTO;

/**
 * Mapper for the entity {@link Exchange} and its DTO {@link ExchangeDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, ProfileMapper.class })
public interface ExchangeMapper extends EntityMapper<ExchangeDTO, Exchange> {
    @Mapping(target = "sendProduct", source = "sendProduct", qualifiedByName = "id")
    @Mapping(target = "receiveProduct", source = "receiveProduct", qualifiedByName = "id")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "id")
    @Mapping(target = "exchanger", source = "exchanger", qualifiedByName = "id")
    ExchangeDTO toDto(Exchange s);
}
