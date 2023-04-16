package pbm.com.exchange.app.rest.respone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProfileUidRes {

    private Long id;

    private String userName;

    private String avatar;
}
