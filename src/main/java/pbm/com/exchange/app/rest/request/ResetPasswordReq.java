package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordReq {

    @NotNull
    @NotBlank
    private String phone;
}
