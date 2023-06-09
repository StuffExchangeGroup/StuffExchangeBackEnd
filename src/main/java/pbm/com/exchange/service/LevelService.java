package pbm.com.exchange.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.service.dto.LevelDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Level}.
 */
public interface LevelService {
    /**
     * Save a level.
     *
     * @param levelDTO the entity to save.
     * @return the persisted entity.
     */
    LevelDTO save(LevelDTO levelDTO);

    /**
     * Partially updates a level.
     *
     * @param levelDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LevelDTO> partialUpdate(LevelDTO levelDTO);

    /**
     * Get all the levels.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LevelDTO> findAll(Pageable pageable);

    /**
     * Get the "id" level.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LevelDTO> findOne(Long id);

    /**
     * Delete the "id" level.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
