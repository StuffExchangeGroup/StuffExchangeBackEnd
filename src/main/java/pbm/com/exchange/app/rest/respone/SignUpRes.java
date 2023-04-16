package pbm.com.exchange.app.rest.respone;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpRes {

    private boolean useOTP;

    @JsonProperty("otpStatus")
    private boolean status;

    private UserRes user;
}