package pbm.com.exchange.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pbm.com.exchange.web.rest.TestUtil;

class AuctionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuctionDTO.class);
        AuctionDTO auctionDTO1 = new AuctionDTO();
        auctionDTO1.setId(1L);
        AuctionDTO auctionDTO2 = new AuctionDTO();
        assertThat(auctionDTO1).isNotEqualTo(auctionDTO2);
        auctionDTO2.setId(auctionDTO1.getId());
        assertThat(auctionDTO1).isEqualTo(auctionDTO2);
        auctionDTO2.setId(2L);
        assertThat(auctionDTO1).isNotEqualTo(auctionDTO2);
        auctionDTO1.setId(null);
        assertThat(auctionDTO1).isNotEqualTo(auctionDTO2);
    }
}
