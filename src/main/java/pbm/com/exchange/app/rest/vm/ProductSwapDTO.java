package pbm.com.exchange.app.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSwapDTO {

    private Long productId;

    private String productName;

    private String thumbnail;

    private String username;

    private String uid;

    private String phone;

    private Long reciverProfileId;

    private String avatarUrl;
}
