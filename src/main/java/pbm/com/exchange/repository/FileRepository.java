package pbm.com.exchange.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import pbm.com.exchange.domain.File;

/**
 * Spring Data SQL repository for the File entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileRepository extends JpaRepository<File, Long> {}
