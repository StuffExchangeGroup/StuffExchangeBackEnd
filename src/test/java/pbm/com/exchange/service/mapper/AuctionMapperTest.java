package pbm.com.exchange.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuctionMapperTest {

    private AuctionMapper auctionMapper;

    @BeforeEach
    public void setUp() {
        auctionMapper = new AuctionMapperImpl();
    }
}
