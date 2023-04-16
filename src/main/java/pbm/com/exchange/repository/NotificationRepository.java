package pbm.com.exchange.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pbm.com.exchange.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long profileId, Pageable pageable);
    
    Integer countByReceiverId(Long profileId);
    
    Integer countByIsSeenAndReceiverId(boolean isSeen, Long profileId);
    
    List<Notification> findByIsSeenAndReceiverId(boolean isSeen, Long profileId);
}
