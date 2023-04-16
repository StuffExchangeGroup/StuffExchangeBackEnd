package pbm.com.exchange.app.rest.request;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationToTopicReq {

    private String title;

    private String message;

    private String topic;

    private Map<String, String> data;
}
