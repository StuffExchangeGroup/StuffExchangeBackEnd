package pbm.com.exchange.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pbm.com.exchange.web.rest.TestUtil;

class FileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileDTO.class);
        FileDTO fileDTO1 = new FileDTO();
        fileDTO1.setId(1L);
        FileDTO fileDTO2 = new FileDTO();
        assertThat(fileDTO1).isNotEqualTo(fileDTO2);
        fileDTO2.setId(fileDTO1.getId());
        assertThat(fileDTO1).isEqualTo(fileDTO2);
        fileDTO2.setId(2L);
        assertThat(fileDTO1).isNotEqualTo(fileDTO2);
        fileDTO1.setId(null);
        assertThat(fileDTO1).isNotEqualTo(fileDTO2);
    }
}
