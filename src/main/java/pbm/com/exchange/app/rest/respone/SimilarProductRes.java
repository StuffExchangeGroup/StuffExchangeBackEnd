package pbm.com.exchange.app.rest.respone;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import pbm.com.exchange.domain.enumeration.Condition;

@Data
@Builder
public class SimilarProductRes {

    private Long id;

    private String productName;

    private Boolean isFavorite;

    private Boolean isExchange;

    private Boolean isSell;

    private Boolean isAuction;

    private Boolean isGift;

    private Condition productCondition;

    private String thumbnail;

    private ZonedDateTime createdDate;

    private ZonedDateTime updatedDate;

    private String categoryName;

    private String displayName;

    private String avatar;

    private Long categoryId;
}
