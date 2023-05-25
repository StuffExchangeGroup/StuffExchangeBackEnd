package pbm.com.exchange.service.dto;

import java.io.Serializable;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import lombok.Data;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductStatus;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Product} entity.
 */
@Data
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull(message = "Product name must not be null")
    private String name;

    private String description;

    private String notice;

    private String location;

    private String verifyPhone;

    private ProductStatus status;

    private Boolean active;

    private Boolean isFavorite;

    private String thumbnail;

    private Integer point;

    private Integer favoriteCount;

    private Double latitude;

    private Double longitude;

    private Condition condition;

    private Integer requestCount;

    private Integer receiveCount;

    private Boolean isSwapAvailable;

    private RatingDTO rating;

    private String categoryName;

    private CityDTO city;

    private ProfileDTO profile;
    
    private Boolean isExchange;

    private Boolean isGift;
    
    private Boolean isBlock;

    private ZonedDateTime createdDate;
}
