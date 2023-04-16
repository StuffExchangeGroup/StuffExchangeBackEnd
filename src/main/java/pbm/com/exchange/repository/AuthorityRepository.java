package pbm.com.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pbm.com.exchange.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
