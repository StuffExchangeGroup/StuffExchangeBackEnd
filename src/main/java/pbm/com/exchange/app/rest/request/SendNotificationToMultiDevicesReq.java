package pbm.com.exchange.app.rest.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationToMultiDevicesReq {

    private String title;

    private String message;

    private List<String> tokens;

    private Map<String, String> data = new HashMap<>();
}
