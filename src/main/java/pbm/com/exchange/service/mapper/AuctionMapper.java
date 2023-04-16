package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Auction;
import pbm.com.exchange.service.dto.AuctionDTO;

/**
 * Mapper for the entity {@link Auction} and its DTO {@link AuctionDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, ProfileMapper.class })
public interface AuctionMapper extends EntityMapper<AuctionDTO, Auction> {
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "id")
    AuctionDTO toDto(Auction s);
}
