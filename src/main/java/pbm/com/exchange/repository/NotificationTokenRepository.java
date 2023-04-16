package pbm.com.exchange.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.NotificationToken;
import pbm.com.exchange.domain.Profile;

/**
 * Spring Data SQL repository for the NotificationToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {
    List<NotificationToken> findByProfile(Profile profile);

    @Modifying
    @Query(value = "DELETE FROM notification_token WHERE token = ?1 AND profile_id = ?2", nativeQuery = true)
    void deleteByTokenAndProfileId(String token, Long profileId);

    boolean existsByTokenAndProfile(String token, Profile profile);
}
