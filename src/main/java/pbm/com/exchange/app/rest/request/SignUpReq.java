package pbm.com.exchange.app.rest.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpReq {

    @NotNull
    @NotBlank
    @JsonProperty("userName")
    private String login;

    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @Size(min = 5, max = 100)
    @NotNull
    private String password;

    @Email
    private String email;

    private String phone;

    private Long countryId;
}