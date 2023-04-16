package pbm.com.exchange.app.rest.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubcriptionReq {

    @NotNull
    @NotBlank
    String topic;

    @NotNull
    List<String> tokens;
}
