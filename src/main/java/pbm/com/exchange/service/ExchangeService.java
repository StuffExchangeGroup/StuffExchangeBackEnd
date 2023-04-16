package pbm.com.exchange.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.app.rest.request.ConfirmSwapReq;
import pbm.com.exchange.app.rest.request.RequestSwapReq;
import pbm.com.exchange.app.rest.vm.ItemSwapDTO;
import pbm.com.exchange.app.rest.vm.MySwapDTO;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;
import pbm.com.exchange.domain.enumeration.ExchangeType;
import pbm.com.exchange.domain.enumeration.NotificationType;
import pbm.com.exchange.service.dto.ExchangeDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.Exchange}.
 */
public interface ExchangeService {
    /**
     * Save a exchange.
     *
     * @param exchangeDTO the entity to save.
     * @return the persisted entity.
     */
    ExchangeDTO save(ExchangeDTO exchangeDTO);

    /**
     * Partially updates a exchange.
     *
     * @param exchangeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExchangeDTO> partialUpdate(ExchangeDTO exchangeDTO);

    /**
     * Get all the exchanges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExchangeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" exchange.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExchangeDTO> findOne(Long id);

    /**
     * Delete the "id" exchange.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get list of exchanges of current user by productId
     *
     * @param productId
     * @param type
     * @param pageable
     */
    List<ItemSwapDTO> getExchangesByProductId(Long productId, ExchangeType type, Pageable pageable);

    /**
     * start chat of exchange
     * @param exchangeId
     * @return true
     */
    boolean startChatting(Long exchangeId);

    /**
     * request swap to receiveProductId
     * @param requestSwapReq
     * @return true
     */
    boolean requestSwap(RequestSwapReq requestSwapReq);

    /**
     * update confirmStatus of exchange to accept or cancel
     * @param confirmSwapReq
     * @return true
     */
    boolean confirmSwap(ConfirmSwapReq confirmSwapReq);

    /**
     * get total of exchange by product id
     * @param productId
     * @return long
     */
    Long getTotalExchangeByProduct(Long productId);

    /**
     * Get list of exchanges of current user
     *
     * @param exchangeStatus
     * @param type
     * @param pageable
     * @return list of exchanges
     */
    List<MySwapDTO> getMyExchanges(ExchangeStatus exchangeStatus, ExchangeType type, Pageable pageable);

    /**
     * Push notification to user
     *
     * @param profile
     * @param product
     * @param notificationType
     * @return
     */
    boolean pushNotificationToUser(
        Profile senderProfile,
        Profile receiverProfile,
        Product senderProduct,
        Product receiverProduct,
        NotificationType notificationType
    );
}
