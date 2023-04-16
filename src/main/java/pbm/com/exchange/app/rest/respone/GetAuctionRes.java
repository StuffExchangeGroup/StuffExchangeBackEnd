package pbm.com.exchange.app.rest.respone;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAuctionRes {

    private Long auctionId;

    private Double auctionPoint;

    private ZonedDateTime auctionTime;

    private String displayName;

    private String avatar;
}
