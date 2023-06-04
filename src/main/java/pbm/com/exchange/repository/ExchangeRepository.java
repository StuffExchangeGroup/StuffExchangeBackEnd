package pbm.com.exchange.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Exchange;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;

/**
 * Spring Data SQL repository for the Exchange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findBySendProductOrReceiveProduct(Product sendProduct, Product receiveProduct, Pageable pageable);
    List<Exchange> findBySendProductOrReceiveProduct(Product sendProduct, Product receiveProduct);

    List<Exchange> findBySendProductAndStatusOrReceiveProductAndStatus(
        Product sendProduct,
        ExchangeStatus status,
        Product receiveProduct,
        ExchangeStatus status1
    );

    Exchange findBySendProductAndReceiveProduct(Product sendProduct, Product receiveProduct);

    List<Exchange> findBySendProduct(Product sendProduct);

    List<Exchange> findBySendProduct(Product sendProduct, Pageable pageable);

    List<Exchange> findByReceiveProduct(Product receiveProduct);

    List<Exchange> findByReceiveProduct(Product receiveProduct, Pageable pageable);

    List<Exchange> findBySendProductAndStatus(Product sendProduct, ExchangeStatus status);

    List<Exchange> findByReceiveProductAndStatus(Product receiveProduct, ExchangeStatus status);

    boolean existsBySendProductAndReceiveProduct(Product sendProduct, Product receiveProduct);

    boolean existsByReceiveProductAndSendProduct(Product sendProduct, Product receiveProduct);

    boolean existsBySendProductOrReceiveProduct(Product sendProduct, Product receiveProduct);

    Long countBySendProductOrReceiveProduct(Product sendProduct, Product receiveProduct);
    
    Long countByStatus(ExchangeStatus status);
}
