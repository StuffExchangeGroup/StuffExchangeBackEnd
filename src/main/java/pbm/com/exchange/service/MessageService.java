package pbm.com.exchange.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.service.dto.MessageDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Message}.
 */
public interface MessageService {
    /**
     * Save a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    MessageDTO save(MessageDTO messageDTO);

    /**
     * Partially updates a message.
     *
     * @param messageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MessageDTO> partialUpdate(MessageDTO messageDTO);

    /**
     * Get all the messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" message.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MessageDTO> findOne(Long id);

    /**
     * Delete the "id" message.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
