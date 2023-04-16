package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder
public class SendChatNotificationReq {

    @NotNull
    private String message;

    private String defaultMessage;

    @NotNull
    private String userUID;

    @NotNull
    private String partnerUID;

    @NotNull
    private Long myProductId;

    @NotNull
    private Long partnerProductId;
}
