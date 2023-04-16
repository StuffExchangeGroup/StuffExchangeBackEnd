package pbm.com.exchange.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NationalityMapperTest {

    private NationalityMapper nationalityMapper;

    @BeforeEach
    public void setUp() {
        nationalityMapper = new NationalityMapperImpl();
    }
}
