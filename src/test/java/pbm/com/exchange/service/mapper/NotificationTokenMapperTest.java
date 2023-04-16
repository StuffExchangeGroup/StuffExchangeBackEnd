package pbm.com.exchange.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationTokenMapperTest {

    private NotificationTokenMapper notificationTokenMapper;

    @BeforeEach
    public void setUp() {
        notificationTokenMapper = new NotificationTokenMapperImpl();
    }
}
