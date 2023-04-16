package pbm.com.exchange.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Comment;

/**
 * Spring Data SQL repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}
