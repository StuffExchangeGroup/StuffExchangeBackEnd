package pbm.com.exchange.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Category;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.ProductCategory;

/**
 * Spring Data SQL repository for the ProductCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByCategory(Category category);
    List<ProductCategory> findByProduct(Product product);
    void deleteByProduct(Product product);
}
