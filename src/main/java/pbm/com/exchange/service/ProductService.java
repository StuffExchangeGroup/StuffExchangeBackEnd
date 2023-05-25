package pbm.com.exchange.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pbm.com.exchange.app.rest.request.PostProductReq;
import pbm.com.exchange.app.rest.request.UpdateProductReq;
import pbm.com.exchange.app.rest.respone.FilterProductRes;
import pbm.com.exchange.app.rest.respone.GetProductRes;
import pbm.com.exchange.app.rest.respone.MyProductRes;
import pbm.com.exchange.app.rest.respone.SimilarProductRes;
import pbm.com.exchange.app.rest.vm.CriteriaDTO;
import pbm.com.exchange.app.rest.vm.FilterDTO;
import pbm.com.exchange.domain.Category;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductStatus;
import pbm.com.exchange.domain.enumeration.ProductType;
import pbm.com.exchange.domain.enumeration.PurposeType;
import pbm.com.exchange.domain.enumeration.Status;
import pbm.com.exchange.service.dto.ProductDTO;
import pbm.com.exchange.specifications.model.Filter;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Product}.
 */
public interface ProductService {
    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    ProductDTO save(ProductDTO productDTO);

    /**
     * Partially updates a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductDTO> partialUpdate(ProductDTO productDTO);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductDTO> findAll(Pageable pageable);

    /**
     * Get all the products with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" product.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductDTO> findOne(Long id);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * save new product
     * @param productDTO
     * @param request
     * @return PostproductReq
     */
    PostProductReq save(PostProductReq productDTO, HttpServletRequest request);

    /**
     * get detail of product by productId
     * @param productId
     * @return GetProductRes
     */
    GetProductRes getProductDetail(Long productId);

    /**
     * delete product by productId
     * @param productId
     * @return true
     */
    boolean deleteProduct(Long productId);

    /**
     * get product of current user
     * @param pageable
     * @param status
     * @return List MyProductRes
     */
    List<MyProductRes> findMyProducts(Pageable pageable, ProductStatus status);

    /**
     * get similar product by productId
     * @param productId
     * @return List SimilarProductRes
     */
    List<SimilarProductRes> getSimilarProducts(Long productId);

    /**
     * update product
     *
     * @param productReq
     * @param request
     * @return UpdateProductReq
     */
    UpdateProductReq updateProduct(UpdateProductReq productReq, HttpServletRequest request);

    /**
     * search product with categoryIds, search, types, conditions, locations;
     * @param pageable
     * @param filterDTO
     * @return list of productDT)
     */
    FilterProductRes webFilterProducts(Pageable pageable, FilterDTO filterDTO);

    /**
     * search product with categoryId, search, type, condition, location;
     * @param pageable
     * @param criteriaDTO
     * @return list of productDTO
     */
    List<ProductDTO> filterProducts(Pageable pageable, CriteriaDTO criteriaDTO);

    List<Filter> filterBySearchName(List<Filter> filters, String search);

    List<String> getProductIdsFromCategories(List<Category> categories);

    List<Filter> filterByCategory(List<Filter> filters, Long categoryId);

    Map<String, Object> filterByType(List<Filter> filters, ProductType type, Pageable pageable);

    List<Filter> filterByStatus(List<Filter> filters, Status status);

    List<Filter> filterByIsBlock(List<Filter> filters);

    List<Filter> filterByLocation(List<Filter> filters, String location);

    List<Filter> filterByCity(List<Filter> filters, Long cityId);

    /**
     * get specification from general filters, search key filters and featured filters
     *
     * @param filters
     * @param searchFilters
     * @param featuredFilter
     *
     * @return product specification
     */
    Specification<Product> getSpecification(List<Filter> filters, List<Filter> searchFilters, List<Filter> featuredFilter);

    Specification<Product> getSpecification(
        List<Filter> filters,
        List<Filter> searchFilters,
        List<Filter> categoryFilters,
        List<Filter> cityFilters,
        List<Filter> conditionFilter,
        List<Filter> purposeFilters
    );

    List<String> getProductIdsFromCurrentUser();

    List<Filter> filterToIgnoreProductOfUser(List<Filter> filters);

    List<Filter> filterByCondition(List<Filter> filters, Condition condition);

    List<Filter> filterByPurpose(List<Filter> filters, PurposeType purpose);

    /**
     * get wish list of current user
     * @param user
     * @return list Long
     */
    List<Long> getWishlistFromUser(User user);

    /**
     * get all locations of product
     *
     * @return list of location for product
     */
    List<String> getAllProductLocations();
    
    /**
     * get detail of product by productId
     * @param productId
     * @return GetProductRes
     */
    GetProductRes getMyProductDetail(Long productId);
}
