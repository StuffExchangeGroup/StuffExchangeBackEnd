package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class AuctionReq {

    @NotNull
    private Long productId;

    @NotNull
    private Double auctionPoint;
}
