package pbm.com.exchange.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.service.dto.AppConfigurationDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.AppConfiguration}.
 */
public interface AppConfigurationService {
    /**
     * Save a appConfiguration.
     *
     * @param appConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    AppConfigurationDTO save(AppConfigurationDTO appConfigurationDTO);

    /**
     * Partially updates a appConfiguration.
     *
     * @param appConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AppConfigurationDTO> partialUpdate(AppConfigurationDTO appConfigurationDTO);

    /**
     * Get all the appConfigurations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AppConfigurationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" appConfiguration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AppConfigurationDTO> findOne(Long id);

    /**
     * Delete the "id" appConfiguration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
