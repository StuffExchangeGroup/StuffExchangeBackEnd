package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordReq {

    @Size(min = 5, max = 100)
    @NotNull
    private String oldPassword;

    @Size(min = 5, max = 100)
    @NotNull
    private String newPassword;
}
