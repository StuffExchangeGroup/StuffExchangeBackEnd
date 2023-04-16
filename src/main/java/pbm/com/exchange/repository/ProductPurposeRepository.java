package pbm.com.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.ProductPurpose;

/**
 * Spring Data SQL repository for the ProductPurpose entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductPurposeRepository extends JpaRepository<ProductPurpose, Long> {}
