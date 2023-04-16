package pbm.com.exchange.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.AppConfiguration;

/**
 * Spring Data SQL repository for the AppConfiguration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppConfigurationRepository extends JpaRepository<AppConfiguration, Long> {
    AppConfiguration findByKey(String key);
}
