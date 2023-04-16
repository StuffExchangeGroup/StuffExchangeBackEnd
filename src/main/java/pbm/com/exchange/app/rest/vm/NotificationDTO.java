package pbm.com.exchange.app.rest.vm;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import pbm.com.exchange.domain.enumeration.NotificationType;

@Data
@Builder
public class NotificationDTO {

    private Long id;

    private String subject;

    private String content;

    private String notificationTitle;
    
    private String thumbnail;

    private NotificationType notificationType;

    private ZonedDateTime createdDate;
    
    private Long productId;

    private Boolean isSeen;
}
