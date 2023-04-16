package pbm.com.exchange.app.rest.respone;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.Status;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetProductRes {

    private Long id;

    private String title;

    private Condition productCondition;

    private Status status;

    private String description;

    private String thumbnail;

    private Integer requestCount;

    private Integer receiveCount;

    private String notice;

    private String location;

    private Long profileId;

    private String displayName;

    private String avatar;

    private ZonedDateTime createdDate;

    private Long categoryId;

    private String categoryName;

    private Integer likedCount;

    private Boolean isFavorite;

    private Long stateId;

    private String stateName;

    private Long cityId;

    private String cityName;

    private Boolean isExchange;

    private Boolean isGift;

    private List<String> imageLinks;
}
