package pbm.com.exchange.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pbm.com.exchange.app.rest.request.SendNotificationToDeviceReq;
import pbm.com.exchange.app.rest.request.SendNotificationToMultiDevicesReq;
import pbm.com.exchange.app.rest.request.SendNotificationToTopicReq;
import pbm.com.exchange.app.rest.request.SubcriptionReq;
import pbm.com.exchange.app.rest.request.UnsubcriptionReq;
import pbm.com.exchange.domain.NotificationToken;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.NotificationTokenRepository;
import pbm.com.exchange.repository.ProfileRepository;
import pbm.com.exchange.service.NotificationTokenService;
import pbm.com.exchange.service.UserService;
import pbm.com.exchange.service.dto.NotificationTokenDTO;
import pbm.com.exchange.service.mapper.NotificationTokenMapper;

/**
 * Service Implementation for managing {@link NotificationToken}.
 */
@Service
@Transactional
public class NotificationTokenServiceImpl implements NotificationTokenService {

    private final Logger log = LoggerFactory.getLogger(NotificationTokenServiceImpl.class);

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationTokenMapper notificationTokenMapper;

    private Logger logger = LoggerFactory.getLogger(NotificationTokenServiceImpl.class);

    private FirebaseApp firebaseApp;

    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream()))
                .build();
            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public NotificationTokenDTO save(NotificationTokenDTO notificationTokenDTO) {
        log.debug("Request to save NotificationToken : {}", notificationTokenDTO);
        NotificationToken notificationToken = notificationTokenMapper.toEntity(notificationTokenDTO);
        notificationToken = notificationTokenRepository.save(notificationToken);
        return notificationTokenMapper.toDto(notificationToken);
    }

    @Override
    public Optional<NotificationTokenDTO> partialUpdate(NotificationTokenDTO notificationTokenDTO) {
        log.debug("Request to partially update NotificationToken : {}", notificationTokenDTO);

        return notificationTokenRepository
            .findById(notificationTokenDTO.getId())
            .map(existingNotificationToken -> {
                notificationTokenMapper.partialUpdate(existingNotificationToken, notificationTokenDTO);

                return existingNotificationToken;
            })
            .map(notificationTokenRepository::save)
            .map(notificationTokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTokenDTO> findAll(Pageable pageable) {
        log.debug("Request to get all NotificationTokens");
        return notificationTokenRepository.findAll(pageable).map(notificationTokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationTokenDTO> findOne(Long id) {
        log.debug("Request to get NotificationToken : {}", id);
        return notificationTokenRepository.findById(id).map(notificationTokenMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete NotificationToken : {}", id);
        notificationTokenRepository.deleteById(id);
    }

    @Override
    public boolean addNotificationToken(NotificationTokenDTO notificationTokenDTO) {
        logger.debug("Service update notification token");
        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        if (notificationTokenRepository.existsByTokenAndProfile(notificationTokenDTO.getToken(), currentProfile)) {
            throw new BadRequestException(MessageHelper.getMessage(pbm.com.exchange.message.Message.Keys.E0089), new Throwable());
        }

        NotificationToken notificationToken = new NotificationToken();
        notificationToken.setToken(notificationTokenDTO.getToken());
        notificationToken.setProfile(currentProfile);

        notificationTokenRepository.save(notificationToken);
        return true;
    }

    @Override
    public boolean deleteNotificationToken(String token) {
        logger.debug("Service to delete token :" + token);
        User currentUser = userService.getCurrentUser().get();
        Profile currentProfile = profileRepository.findOneByUser(currentUser).get();

        notificationTokenRepository.deleteByTokenAndProfileId(token, currentProfile.getId());
        return true;
    }

    @Override
    public void subscribeToTopic(SubcriptionReq subcriptionReq) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).subscribeToTopic(subcriptionReq.getTokens(), subcriptionReq.getTopic());
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase subscribe to topic fail", e);
        }
    }

    @Override
    public void unsubscribeFromTopic(UnsubcriptionReq unsubcriptionReq) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).unsubscribeFromTopic(unsubcriptionReq.getTokens(), unsubcriptionReq.getTopic());
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase unsubscribe from topic fail", e);
        }
    }

    @Override
    public String sendNotificationToDevice(SendNotificationToDeviceReq req) {
        logger.debug("Service to send notification to device " + req);
        Message message = Message
            .builder()
            .setToken(req.getToken())
            .setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
            .setNotification(Notification.builder().setTitle(req.getTitle()).setBody(req.getMessage()).build())
            .putAllData(req.getData())
            .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            logger.error("Fail to send firebase notification", e);
        }

        return response;
    }

    @Override
    public String sendNotificationToTopic(SendNotificationToTopicReq req) {
        Message message = Message
            .builder()
            .setTopic(req.getTopic())
            .setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
            .setNotification(Notification.builder().setTitle(req.getTitle()).setBody(req.getMessage()).build())
            .putAllData(req.getData())
            .build();

        String response = null;
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            logger.error("Fail to send firebase notification", e);
        }

        return response;
    }

    @Override
    public BatchResponse sendNotificationToMultipleDevices(SendNotificationToMultiDevicesReq req) {
        logger.debug("Service send notification to multiple devices " + req);
        MulticastMessage message = MulticastMessage
            .builder()
            .addAllTokens(req.getTokens())
            .setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH).build())
            .setNotification(Notification.builder().setTitle(req.getTitle()).setBody(req.getMessage()).build())
            .putAllData(req.getData())
            .build();

        BatchResponse response = null;
        try {
            response = FirebaseMessaging.getInstance().sendMulticast(message);
            List<SendResponse> responses = response.getResponses();
            List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    failedTokens.add(req.getTokens().get(i));
                }
            }
        } catch (FirebaseMessagingException e) {
            logger.error("Fail to send firebase notification", e);
        }

        return response;
    }
}
