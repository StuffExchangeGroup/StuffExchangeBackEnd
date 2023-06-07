package pbm.com.exchange.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.enumeration.ProductStatus;

/**
 * Spring Data SQL repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query(
        value = "select distinct product from Product product left join fetch product.purposes",
        countQuery = "select count(distinct product) from Product product"
    )
    Page<Product> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct product from Product product left join fetch product.purposes")
    List<Product> findAllWithEagerRelationships();

    @Query("select product from Product product left join fetch product.purposes where product.id =:id")
    Optional<Product> findOneWithEagerRelationships(@Param("id") Long id);

    List<Product> findByIdIn(List<Long> ids);

    List<Product> findByNameContains(String name);

    List<Product> findByIdInAndNameContains(List<Long> ids, String name);

    List<Product> findByIdIn(List<Long> ids, Pageable pageable);

    List<Product> findByNameContains(String name, Pageable pageable);

    List<Product> findByIdInAndNameContains(List<Long> ids, String name, Pageable pageable);

    List<Product> findByProfile(Profile profile);

    List<Product> findByProfile(Profile profile, Pageable pageable);

    List<Product> findByProfileAndStatus(Profile profile, ProductStatus status, Pageable pageable);

    List<Product> findByIdAndProfile(Long productId, Profile profile);

    @Query(
        value = "select p.* from product p\n" +
        "inner join product_category pc on p.id = pc.product_id\n" +
        "inner join category c on pc.category_id = c.id\n" +
        "where p.status = 'AVAILABLE' and category_id IN :categoryIds and p.id <> :productId and p.profile_id <> :profileId \n" +
        "order by created_date desc\n" +
        "limit 10",
        nativeQuery = true
    )
    List<Product> findSimilarProducts(
        @Param("categoryIds") Collection<Long> categoryIds,
        @Param("productId") Long productId,
        @Param("profileId") Long profileId
    );

    @Query(value = "select distinct location from product order by location asc", nativeQuery = true)
    List<String> findAllProductLocations();
    
    boolean existsByIdAndProfile(Long id, Profile profile);
    
    Page<Product> findByIsBlock(boolean status, Pageable pageable);
}
