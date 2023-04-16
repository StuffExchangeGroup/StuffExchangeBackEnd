package pbm.com.exchange.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pbm.com.exchange.app.rest.request.UpdateProductReq;
import pbm.com.exchange.app.rest.respone.GetProductRes;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.service.dto.ProductDTO;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { RatingMapper.class, ProductPurposeMapper.class, CityMapper.class, ProfileMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "rating", source = "rating", qualifiedByName = "id")
    @Mapping(target = "city", source = "city", qualifiedByName = "id")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "id")
    ProductDTO toDto(Product s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoId(Product product);

    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "title", source = "name")
    GetProductRes toGetProductDto(Product s);

    @Mapping(target = "name", source = "title")
    Product toEntity(GetProductRes dto);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "likedCount", source = "favoriteCount")
    GetProductRes toDTO(Product dto);

    @Named("partialUpdate")
    @Mapping(target = "name", source = "title")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Product entity, UpdateProductReq dto);

    @Named("partialUpdate")
    @Mapping(target = "title", source = "name")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget UpdateProductReq dto, Product entity);
}
