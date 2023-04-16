package pbm.com.exchange.service;

import com.google.firebase.messaging.BatchResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pbm.com.exchange.app.rest.request.SendNotificationToDeviceReq;
import pbm.com.exchange.app.rest.request.SendNotificationToMultiDevicesReq;
import pbm.com.exchange.app.rest.request.SendNotificationToTopicReq;
import pbm.com.exchange.app.rest.request.SubcriptionReq;
import pbm.com.exchange.app.rest.request.UnsubcriptionReq;
import pbm.com.exchange.service.dto.NotificationTokenDTO;

/**
 * Service Interface for managing {@link pbm.com.exchange.domain.NotificationToken}.
 */
public interface NotificationTokenService {
    /**
     * Save a notificationToken.
     *
     * @param notificationTokenDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationTokenDTO save(NotificationTokenDTO notificationTokenDTO);

    /**
     * Partially updates a notificationToken.
     *
     * @param notificationTokenDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationTokenDTO> partialUpdate(NotificationTokenDTO notificationTokenDTO);

    /**
     * Get all the notificationTokens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationTokenDTO> findAll(Pageable pageable);

    /**
     * Get the "id" notificationToken.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationTokenDTO> findOne(Long id);

    /**
     * Delete the "id" notificationToken.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * save and update device token
     *
     * @param notificationTokenDTO
     */
    boolean addNotificationToken(NotificationTokenDTO notificationTokenDTO);

    /**
     * delete token before logout
     *
     * @param token
     * @return Boolean
     */
    boolean deleteNotificationToken(String token);

    /**
     * subscribe to topic
     *
     * @param subcriptionReq
     */
    void subscribeToTopic(SubcriptionReq subcriptionReq);

    /**
     * unsubscribe from topic
     *
     * @param unsubcriptionReq
     */
    void unsubscribeFromTopic(UnsubcriptionReq unsubcriptionReq);

    /**
     * send notification to device
     *
     * @param sendNotificationToDeviceReq
     * @return response
     */
    String sendNotificationToDevice(SendNotificationToDeviceReq sendNotificationToDeviceReq);

    /**
     * send notification to topic
     *
     * @param sendNotificationToTopicReq
     * @return response
     */
    String sendNotificationToTopic(SendNotificationToTopicReq sendNotificationToTopicReq);

    /**
     * send notification to multiple devices
     *
     * @param sendNotificationToMultiDevicesReq
     * @return batchResponse
     */
    BatchResponse sendNotificationToMultipleDevices(SendNotificationToMultiDevicesReq sendNotificationToMultiDevicesReq);
}
