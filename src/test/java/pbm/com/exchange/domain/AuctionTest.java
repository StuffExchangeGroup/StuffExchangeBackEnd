package pbm.com.exchange.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pbm.com.exchange.web.rest.TestUtil;

class AuctionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Auction.class);
        Auction auction1 = new Auction();
        auction1.setId(1L);
        Auction auction2 = new Auction();
        auction2.setId(auction1.getId());
        assertThat(auction1).isEqualTo(auction2);
        auction2.setId(2L);
        assertThat(auction1).isNotEqualTo(auction2);
        auction1.setId(null);
        assertThat(auction1).isNotEqualTo(auction2);
    }
}
