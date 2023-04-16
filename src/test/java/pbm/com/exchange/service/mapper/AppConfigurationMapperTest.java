package pbm.com.exchange.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppConfigurationMapperTest {

    private AppConfigurationMapper appConfigurationMapper;

    @BeforeEach
    public void setUp() {
        appConfigurationMapper = new AppConfigurationMapperImpl();
    }
}
