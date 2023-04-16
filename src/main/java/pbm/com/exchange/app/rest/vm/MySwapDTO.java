package pbm.com.exchange.app.rest.vm;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySwapDTO {

    private Long exchangeId;

    private Long productId;

    private String productName;

    private String avatarProduct;

    private ZonedDateTime updatedDate;

    private ExchangeStatus exchangeStatus;
}
