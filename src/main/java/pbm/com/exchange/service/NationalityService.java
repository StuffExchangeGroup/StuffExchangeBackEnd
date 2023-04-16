package pbm.com.exchange.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.service.dto.NationalityDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Nationality}.
 */
public interface NationalityService {
    /**
     * Save a nationality.
     *
     * @param nationalityDTO the entity to save.
     * @return the persisted entity.
     */
    NationalityDTO save(NationalityDTO nationalityDTO);

    /**
     * Partially updates a nationality.
     *
     * @param nationalityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NationalityDTO> partialUpdate(NationalityDTO nationalityDTO);

    /**
     * Get all the nationalities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NationalityDTO> findAll(Pageable pageable);

    /**
     * Get the "id" nationality.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NationalityDTO> findOne(Long id);

    /**
     * Delete the "id" nationality.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * find all nationality
     *
     * @return List<NationalityDTO>
     */
    List<NationalityDTO> findAll();
}
