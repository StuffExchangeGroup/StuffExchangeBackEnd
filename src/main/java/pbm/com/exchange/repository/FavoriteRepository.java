package pbm.com.exchange.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Favorite;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;

/**
 * Spring Data SQL repository for the Favorite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findByProductAndProfile(Product product, Profile profile);

    List<Favorite> findByProfile(Profile profile, Pageable pageable);

    List<Favorite> findByProfile(Profile profile);

    void deleteByProduct(Product product);

    boolean existsByProductAndProfile(Product product, Profile profile);
}
