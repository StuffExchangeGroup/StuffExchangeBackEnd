package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Auction} entity.
 */
public class AuctionDTO implements Serializable {

    private Long id;

    private Integer point;

    private ZonedDateTime createdDate;

    private ProductDTO product;

    private ProfileDTO profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuctionDTO)) {
            return false;
        }

        AuctionDTO auctionDTO = (AuctionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auctionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuctionDTO{" +
            "id=" + getId() +
            ", point=" + getPoint() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", product=" + getProduct() +
            ", profile=" + getProfile() +
            "}";
    }
}
