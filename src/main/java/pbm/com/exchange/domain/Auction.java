package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Auction.
 */
@Entity
@Table(name = "auction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Auction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "point")
    private Double point;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "rating",
            "images",
            "sendExchanges",
            "receiveExchanges",
            "productCategories",
            "favorites",
            "auctions",
            "comments",
            "purposes",
            "city",
            "profile",
        },
        allowSetters = true
    )
    private Product product;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "user",
            "favorites",
            "ownerExchanges",
            "exchangerExchanges",
            "purchases",
            "products",
            "notificationTokens",
            "auctions",
            "comments",
            "city",
            "level",
        },
        allowSetters = true
    )
    private Profile profile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Auction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPoint() {
        return this.point;
    }

    public Auction point(Double point) {
        this.setPoint(point);
        return this;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    public Auction createdDate(ZonedDateTime createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Auction product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Auction profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Auction)) {
            return false;
        }
        return id != null && id.equals(((Auction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Auction{" +
            "id=" + getId() +
            ", point=" + getPoint() +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
