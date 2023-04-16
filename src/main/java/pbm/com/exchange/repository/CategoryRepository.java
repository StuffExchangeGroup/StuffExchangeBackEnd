package pbm.com.exchange.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Category;

/**
 * Spring Data SQL repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);

    List<Category> findByActive(Boolean active);

    Optional<Category> findOneByNameIgnoreCase(String name);
}
