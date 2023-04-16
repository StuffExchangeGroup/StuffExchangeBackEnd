package pbm.com.exchange.app.rest.respone;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRes {

    private Long id;

    @JsonProperty("userName")
    private String login;

    private String firstName;

    private String lastName;

    private String displayName;

    private Double latitude;

    private Double longitude;

    private String email;

    private boolean activated;

    private String phone;

    private String avatar;

    private ZonedDateTime dob;

    private Long cityId;

    private String location;

    private String customTokenFirebase;

    private String uid;

    private Integer point;
}
