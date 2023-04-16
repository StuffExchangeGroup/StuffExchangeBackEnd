package pbm.com.exchange.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;

import pbm.com.exchange.app.rest.request.SendChatNotificationReq;
import pbm.com.exchange.app.rest.request.SendNotificationToMultiDevicesReq;
import pbm.com.exchange.app.rest.respone.NotificationRes;
import pbm.com.exchange.app.rest.respone.PaginateRes;
import pbm.com.exchange.app.rest.vm.NotificationDTO;
import pbm.com.exchange.domain.Notification;
import pbm.com.exchange.domain.NotificationToken;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.domain.enumeration.NotificationType;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.NotificationRepository;
import pbm.com.exchange.repository.NotificationTokenRepository;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.service.NotificationService;
import pbm.com.exchange.service.NotificationTokenService;
import pbm.com.exchange.service.ProfileService;
import pbm.com.exchange.service.UserService;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProfileService profileService;
    
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    public NotificationTokenService notificationTokenService;

    @Autowired
    public NotificationTokenRepository notificationTokenRepository;

    @Autowired
    public ProductRepository productRepository;

    public static final String CHAT_MESSAGE_DEFAULT = "You had received a new message";

    @Override
    public NotificationRes getNotificationsByCurrentUser(Pageable pageable) {
        log.debug("service get all notifications by current user");

        // add default sort by createdDate
        Pageable newPageable = pageable;
        if (pageable.getSort() == Sort.unsorted()) {
            newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("createdDate").descending());
        }

        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        List<Notification> notifications = notificationRepository.findByReceiverId(currentProfile.getId(), newPageable);
        List<NotificationDTO> notificationDTOs = new ArrayList<NotificationDTO>();

        for (Notification notification : notifications) {
            String notificationTypeString = this.convertNotificationTypeToString(notification.getNotificationType());
            Product product = productRepository.getById(notification.getReceiveProductId());

            NotificationDTO notificationDTO = NotificationDTO
                    .builder()
                    .id(notification.getId())
                    .subject(notification.getSubject())
                    .content(notification.getContent())
                    .createdDate(notification.getCreatedDate())
                    .notificationType(notification.getNotificationType())
                    .notificationTitle(notificationTypeString)
                    .thumbnail(product.getThumbnail())
                    .productId(product.getId())
                    .isSeen(notification.getIsSeen())
                    .build();
            notificationDTOs.add(notificationDTO);
        }
        
        int itemsPerPage = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int totalItems = (int) notificationRepository.countByReceiverId(currentProfile.getId());

        PaginateRes pagination = PaginateRes
                .builder()
                .itemsPerPage(itemsPerPage)
                .currentPage(currentPage)
                .totalItems(totalItems)
                .build();
        
        return new NotificationRes(notificationDTOs, pagination);    
    }

    @Override
    public NotificationDTO getNotificationById(Long notificationId) {
        log.debug("service get notification by id");
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (!notificationOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0091, notificationId),
                    new Throwable());
        }

        Notification notification = notificationOptional.get();

        String notificationTypeString = this.convertNotificationTypeToString(notification.getNotificationType());
        // set isSeen for notification
        if (!notification.getIsSeen()) {
            notification.setIsSeen(true);
            notificationRepository.save(notification);
        }

        NotificationDTO notificationDTO = NotificationDTO
                .builder()
                .id(notification.getId())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .createdDate(notification.getCreatedDate())
                .notificationType(notification.getNotificationType())
                .notificationTitle(notificationTypeString)
                .isSeen(notification.getIsSeen())
                .build();

        return notificationDTO;
    }

    @Override
    public BatchResponse sendChatNotification(SendChatNotificationReq sendChatNotificationReq) {
        Optional<Profile> receiverProfile = profileRepository.findOneByUid(sendChatNotificationReq.getPartnerUID());
        Optional<Profile> senderProfile = profileRepository.findOneByUid(sendChatNotificationReq.getUserUID());

        if (!receiverProfile.isPresent()) {
            throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0090, sendChatNotificationReq.getPartnerUID()),
                    new Throwable());
        }
        if (!senderProfile.isPresent()) {
            throw new BadRequestException(
                    MessageHelper.getMessage(Message.Keys.E0090, sendChatNotificationReq.getUserUID()),
                    new Throwable());
        }

        if (sendChatNotificationReq.getDefaultMessage() == null) {
            sendChatNotificationReq.setDefaultMessage(CHAT_MESSAGE_DEFAULT);
        }

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.CHAT_SWAP.toString());
        data.put("username", receiverProfile.get().getDisplayName());
        data.put("avatarUrl", receiverProfile.get().getAvatar());
        data.put("message", sendChatNotificationReq.getMessage());
        data.put("defaultMessage", sendChatNotificationReq.getDefaultMessage());
        data.put("userUID", sendChatNotificationReq.getPartnerUID());
        data.put("partnerUID", sendChatNotificationReq.getUserUID());
        data.put("myProductId", sendChatNotificationReq.getMyProductId().toString());
        data.put("partnerProductId", sendChatNotificationReq.getPartnerProductId().toString());

        List<NotificationToken> notificationTokenList = notificationTokenRepository
                .findByProfile(receiverProfile.get());
        List<String> tokens = notificationTokenList
                .stream()
                .map(notificationToken -> notificationToken.getToken())
                .collect(Collectors.toList());

        SendNotificationToMultiDevicesReq sendNotificationToMultiDevicesReq = SendNotificationToMultiDevicesReq
                .builder()
                .title(senderProfile.get().getDisplayName())
                .message(sendChatNotificationReq.getMessage())
                .tokens(tokens)
                .data(data)
                .build();

        return notificationTokenService.sendNotificationToMultipleDevices(sendNotificationToMultiDevicesReq);
    }

    /**
     *
     * @param notificationType
     * @return Notification was converted to string
     */
    public String convertNotificationTypeToString(NotificationType notificationType) {
        String notificationTypeString = null;
        switch (notificationType) {
            case ACCEPT_SWAP:
                notificationTypeString = "Accept swap";
                break;
            case REQUEST_SWAP:
                notificationTypeString = "Request swap";
                break;
            case CANCEL_SWAP:
                notificationTypeString = "Cancel swap";
                break;
            case CLOSE_CONVERSATION:
                notificationTypeString = "Close conversation";
                break;
            case START_CONVERSATION:
                notificationTypeString = "Start conversation";
                break;
            default:
                return notificationTypeString;
        }

        return notificationTypeString;
    }

    @Override
    public BatchResponse sendNotificationToMultipleDevices(SendNotificationToMultiDevicesReq req) {
        log.debug("Service send notification to multiple devices " + req);
        MulticastMessage message = MulticastMessage
            .builder()
            .addAllTokens(req.getTokens())
            .setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
            .setNotification(com.google.firebase.messaging.Notification.builder()
                    .setTitle(req.getTitle())
                    .setBody(req.getMessage()).build())
            .putAllData(req.getData())
            .build();

        BatchResponse response = null;
        try {
            response = FirebaseMessaging.getInstance().sendMulticast(message);
            List<SendResponse> responses = response.getResponses();
            log.debug("Notification responses: ", responses);
            List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    failedTokens.add(req.getTokens().get(i));
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }

        return response;
    }

    @Override
    public Integer countNotificationHaveNotSeen() {
        Profile profile = profileService.getCurrentProfile();
        Integer count = notificationRepository.countByIsSeenAndReceiverId(false, profile.getId());
        return count;
    }

    @Override
    public boolean markAllAsRead() {
        Profile profile = profileService.getCurrentProfile();
        List<Notification> notifications = notificationRepository.findByIsSeenAndReceiverId(false, profile.getId());
        for(Notification notification: notifications) {
            notification.setIsSeen(true);
            notificationRepository.save(notification);
        }
        return true;
    }
}
