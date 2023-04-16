package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Purchase;
import pbm.com.exchange.service.dto.PurchaseDTO;

/**
 * Mapper for the entity {@link Purchase} and its DTO {@link PurchaseDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProfileMapper.class })
public interface PurchaseMapper extends EntityMapper<PurchaseDTO, Purchase> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "id")
    PurchaseDTO toDto(Purchase s);
}
