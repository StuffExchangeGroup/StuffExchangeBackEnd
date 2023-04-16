package pbm.com.exchange.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Auction;
import pbm.com.exchange.domain.Product;

/**
 * Spring Data SQL repository for the Auction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByProduct(Product product, Pageable pageable);
}
