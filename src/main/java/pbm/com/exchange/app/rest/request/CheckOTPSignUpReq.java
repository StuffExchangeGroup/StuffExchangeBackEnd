package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOTPSignUpReq {

    @NotNull
    @NotBlank
    private String codeOTP;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String password;
}
