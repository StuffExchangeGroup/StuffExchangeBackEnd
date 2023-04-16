package pbm.com.exchange.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductPurposeMapperTest {

    private ProductPurposeMapper productPurposeMapper;

    @BeforeEach
    public void setUp() {
        productPurposeMapper = new ProductPurposeMapperImpl();
    }
}
