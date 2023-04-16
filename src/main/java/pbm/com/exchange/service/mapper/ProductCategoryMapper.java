package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.ProductCategory;
import pbm.com.exchange.service.dto.ProductCategoryDTO;

/**
 * Mapper for the entity {@link ProductCategory} and its DTO {@link ProductCategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, CategoryMapper.class })
public interface ProductCategoryMapper extends EntityMapper<ProductCategoryDTO, ProductCategory> {
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    ProductCategoryDTO toDto(ProductCategory s);
}
