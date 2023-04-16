package pbm.com.exchange.service.mapper;

import org.mapstruct.*;
import pbm.com.exchange.domain.Comment;
import pbm.com.exchange.service.dto.CommentDTO;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, ProfileMapper.class })
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "id")
    CommentDTO toDto(Comment s);
}
