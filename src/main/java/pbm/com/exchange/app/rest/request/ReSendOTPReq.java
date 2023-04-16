package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReSendOTPReq {

    @Email
    @NotNull
    private String email;
}
