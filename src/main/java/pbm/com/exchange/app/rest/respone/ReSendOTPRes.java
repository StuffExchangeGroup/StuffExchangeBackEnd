package pbm.com.exchange.app.rest.respone;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vonage.client.verify.VerifyStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReSendOTPRes {

    @JsonProperty("otpRequestId")
    private String requestId;

    @JsonProperty("otpStatus")
    private VerifyStatus status;
}
