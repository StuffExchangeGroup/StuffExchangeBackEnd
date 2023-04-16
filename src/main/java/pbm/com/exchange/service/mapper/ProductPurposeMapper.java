package pbm.com.exchange.service.mapper;

import java.util.Set;
import org.mapstruct.*;
import pbm.com.exchange.domain.ProductPurpose;
import pbm.com.exchange.service.dto.ProductPurposeDTO;

/**
 * Mapper for the entity {@link ProductPurpose} and its DTO {@link ProductPurposeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductPurposeMapper extends EntityMapper<ProductPurposeDTO, ProductPurpose> {
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ProductPurposeDTO> toDtoIdSet(Set<ProductPurpose> productPurpose);
}
