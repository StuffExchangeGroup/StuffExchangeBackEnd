package pbm.com.exchange.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Rating;

/**
 * Spring Data SQL repository for the Rating entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {}
