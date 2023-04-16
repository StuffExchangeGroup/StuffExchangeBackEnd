package pbm.com.exchange.app.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.Status;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostProductReq {

    private Long id;

    @NotNull
    private Long categoryId;

    @NotNull
    private String title;

    private String description;

    @NotNull
    private Status status;

    @NotNull
    @JsonProperty("condition")
    private Condition condition;

    @NotNull
    private List<Long> purposeIds = new ArrayList<>();

    private String location;

    private String notice;

    private ZonedDateTime startAuctionTime;

    private ZonedDateTime endAuctionTime;

    private Double sellPoint;

    private Double auctionPoint;

    private String[] imageIds;

    @NotNull
    private Long cityId;
}
