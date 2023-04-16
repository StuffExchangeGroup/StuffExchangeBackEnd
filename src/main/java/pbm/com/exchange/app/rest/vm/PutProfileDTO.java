package pbm.com.exchange.app.rest.vm;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PutProfileDTO {

    private String firstName;
    private String lastName;
    private String displayName;
    private String avatar;
    private String phone;
    private String email;
    private String username;
    private String location;
    private ZonedDateTime dob;
    private Long cityId;
}
