package pbm.com.exchange.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pbm.com.exchange.web.rest.TestUtil;

class ProductPurposeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductPurposeDTO.class);
        ProductPurposeDTO productPurposeDTO1 = new ProductPurposeDTO();
        productPurposeDTO1.setId(1L);
        ProductPurposeDTO productPurposeDTO2 = new ProductPurposeDTO();
        assertThat(productPurposeDTO1).isNotEqualTo(productPurposeDTO2);
        productPurposeDTO2.setId(productPurposeDTO1.getId());
        assertThat(productPurposeDTO1).isEqualTo(productPurposeDTO2);
        productPurposeDTO2.setId(2L);
        assertThat(productPurposeDTO1).isNotEqualTo(productPurposeDTO2);
        productPurposeDTO1.setId(null);
        assertThat(productPurposeDTO1).isNotEqualTo(productPurposeDTO2);
    }
}
