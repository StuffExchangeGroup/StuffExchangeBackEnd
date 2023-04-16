package pbm.com.exchange.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.app.rest.respone.WishProductRes;
import pbm.com.exchange.service.dto.FavoriteDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Favorite}.
 */
public interface FavoriteService {
    /**
     * Save a favorite.
     *
     * @param favoriteDTO the entity to save.
     * @return the persisted entity.
     */
    FavoriteDTO save(FavoriteDTO favoriteDTO);

    /**
     * Partially updates a favorite.
     *
     * @param favoriteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FavoriteDTO> partialUpdate(FavoriteDTO favoriteDTO);

    /**
     * Get all the favorites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FavoriteDTO> findAll(Pageable pageable);

    /**
     * Get the "id" favorite.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FavoriteDTO> findOne(Long id);

    /**
     * Delete the "id" favorite.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Check products is favorite
     *
     * @param productId
     * @return Boolean
     */
    Boolean isFavouriteExists(Long productId);

    /**
     * Get wish list
     *
     * @param pageable
     * @return List<WishProductRes>
     */
    List<WishProductRes> getWishList(Pageable pageable);
}
