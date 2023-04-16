package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordReq {

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @Size(min = 5, max = 100)
    private String password;
}
