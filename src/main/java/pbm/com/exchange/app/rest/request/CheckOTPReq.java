package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckOTPReq {

    @NotNull
    @NotBlank
    private String codeOTP;

    @NotNull
    @NotBlank
    private String email;
}
