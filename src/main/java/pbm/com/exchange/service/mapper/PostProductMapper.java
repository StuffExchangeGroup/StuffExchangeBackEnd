package pbm.com.exchange.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pbm.com.exchange.app.rest.request.PostProductReq;
import pbm.com.exchange.domain.Product;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostProductMapper {
    @Mapping(target = "title", source = "name")
    PostProductReq toDto(Product s);

    @Mapping(target = "name", source = "title")
    Product toEntity(PostProductReq dto);
}
