package pbm.com.exchange.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pbm.com.exchange.web.rest.TestUtil;

class NotificationTokenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationTokenDTO.class);
        NotificationTokenDTO notificationTokenDTO1 = new NotificationTokenDTO();
        notificationTokenDTO1.setId(1L);
        NotificationTokenDTO notificationTokenDTO2 = new NotificationTokenDTO();
        assertThat(notificationTokenDTO1).isNotEqualTo(notificationTokenDTO2);
        notificationTokenDTO2.setId(notificationTokenDTO1.getId());
        assertThat(notificationTokenDTO1).isEqualTo(notificationTokenDTO2);
        notificationTokenDTO2.setId(2L);
        assertThat(notificationTokenDTO1).isNotEqualTo(notificationTokenDTO2);
        notificationTokenDTO1.setId(null);
        assertThat(notificationTokenDTO1).isNotEqualTo(notificationTokenDTO2);
    }
}
