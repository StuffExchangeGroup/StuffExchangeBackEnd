package pbm.com.exchange.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pbm.com.exchange.web.rest.TestUtil;

class ProductPurposeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductPurpose.class);
        ProductPurpose productPurpose1 = new ProductPurpose();
        productPurpose1.setId(1L);
        ProductPurpose productPurpose2 = new ProductPurpose();
        productPurpose2.setId(productPurpose1.getId());
        assertThat(productPurpose1).isEqualTo(productPurpose2);
        productPurpose2.setId(2L);
        assertThat(productPurpose1).isNotEqualTo(productPurpose2);
        productPurpose1.setId(null);
        assertThat(productPurpose1).isNotEqualTo(productPurpose2);
    }
}
