package pbm.com.exchange.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.Profile;
import pbm.com.exchange.domain.User;

/**
 * Spring Data SQL repository for the Profile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findOneByPhone(String phone);

    Optional<Profile> findOneByUser(User user);

    Optional<Profile> findOneByUid(String uid);

    boolean existsByUserAndPhone(User user, String phone);

    boolean existsByPhone(String phone);
}
