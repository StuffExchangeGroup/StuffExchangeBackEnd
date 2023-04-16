package pbm.com.exchange.app.rest.respone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import pbm.com.exchange.domain.enumeration.TokenType;

@Data
@Builder
public class SignInRes {

    private String authToken;

    @Builder.Default
    private TokenType tokenType = TokenType.Bearer;

    private UserRes user;

    private boolean useOTP;

    @JsonProperty("otpStatus")
    private boolean status;
}
