package pbm.com.exchange.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingMapperTest {

    private RatingMapper ratingMapper;

    @BeforeEach
    public void setUp() {
        ratingMapper = new RatingMapperImpl();
    }
}
