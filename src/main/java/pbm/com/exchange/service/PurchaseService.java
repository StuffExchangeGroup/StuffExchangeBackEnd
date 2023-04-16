package pbm.com.exchange.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.service.dto.PurchaseDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Purchase}.
 */
public interface PurchaseService {
    /**
     * Save a purchase.
     *
     * @param purchaseDTO the entity to save.
     * @return the persisted entity.
     */
    PurchaseDTO save(PurchaseDTO purchaseDTO);

    /**
     * Partially updates a purchase.
     *
     * @param purchaseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PurchaseDTO> partialUpdate(PurchaseDTO purchaseDTO);

    /**
     * Get all the purchases.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PurchaseDTO> findAll(Pageable pageable);

    /**
     * Get the "id" purchase.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PurchaseDTO> findOne(Long id);

    /**
     * Delete the "id" purchase.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
