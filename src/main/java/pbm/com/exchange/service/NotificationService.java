package pbm.com.exchange.service;

import org.springframework.data.domain.Pageable;

import com.google.firebase.messaging.BatchResponse;

import pbm.com.exchange.app.rest.request.SendChatNotificationReq;
import pbm.com.exchange.app.rest.request.SendNotificationToMultiDevicesReq;
import pbm.com.exchange.app.rest.respone.NotificationRes;
import pbm.com.exchange.app.rest.vm.NotificationDTO;

public interface NotificationService {
    BatchResponse sendNotificationToMultipleDevices(SendNotificationToMultiDevicesReq req);

    /**
     * getNotificationsByCurrentUser
     * 
     * @return List NotificationDTO
     */
    NotificationRes getNotificationsByCurrentUser(Pageable pageable);

    /**
     *
     * @param notificationId
     * @return NotificationDTO
     */
    NotificationDTO getNotificationById(Long notificationId);

    /**
     * send notification when send a message in conversation
     *
     * @param sendChatNotificationReq
     * @return batchRespone
     */
    BatchResponse sendChatNotification(SendChatNotificationReq sendChatNotificationReq);
    
    Integer countNotificationHaveNotSeen();
    
    boolean markAllAsRead();

}
