package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Image;
import pbm.com.exchange.service.dto.ImageDTO;

/**
 * Mapper for the entity {@link Image} and its DTO {@link ImageDTO}.
 */
@Mapper(componentModel = "spring", uses = { FileMapper.class, ProductMapper.class })
public interface ImageMapper extends EntityMapper<ImageDTO, Image> {
    @Mapping(target = "imageFile", source = "imageFile", qualifiedByName = "id")
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    ImageDTO toDto(Image s);
}
