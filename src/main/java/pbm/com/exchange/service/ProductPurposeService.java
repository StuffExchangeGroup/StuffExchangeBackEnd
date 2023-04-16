package pbm.com.exchange.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.service.dto.ProductPurposeDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.ProductPurpose}.
 */
public interface ProductPurposeService {
    /**
     * Save a productPurpose.
     *
     * @param productPurposeDTO the entity to save.
     * @return the persisted entity.
     */
    ProductPurposeDTO save(ProductPurposeDTO productPurposeDTO);

    /**
     * Partially updates a productPurpose.
     *
     * @param productPurposeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductPurposeDTO> partialUpdate(ProductPurposeDTO productPurposeDTO);

    /**
     * Get all the productPurposes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductPurposeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" productPurpose.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductPurposeDTO> findOne(Long id);

    /**
     * Delete the "id" productPurpose.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
