package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import pbm.com.exchange.domain.enumeration.MoneyUnit;
import pbm.com.exchange.domain.enumeration.PurchaseType;

/**
 * A Purchase.
 */
@Entity
@Table(name = "purchase")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_type")
    private PurchaseType purchaseType;

    @Column(name = "confirmed_date")
    private ZonedDateTime confirmedDate;

    @Column(name = "money")
    private Double money;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private MoneyUnit unit;

    @Column(name = "is_confirm")
    private Boolean isConfirm;

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

    public Purchase id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseType getPurchaseType() {
        return this.purchaseType;
    }

    public Purchase purchaseType(PurchaseType purchaseType) {
        this.setPurchaseType(purchaseType);
        return this;
    }

    public void setPurchaseType(PurchaseType purchaseType) {
        this.purchaseType = purchaseType;
    }

    public ZonedDateTime getConfirmedDate() {
        return this.confirmedDate;
    }

    public Purchase confirmedDate(ZonedDateTime confirmedDate) {
        this.setConfirmedDate(confirmedDate);
        return this;
    }

    public void setConfirmedDate(ZonedDateTime confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public Double getMoney() {
        return this.money;
    }

    public Purchase money(Double money) {
        this.setMoney(money);
        return this;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public MoneyUnit getUnit() {
        return this.unit;
    }

    public Purchase unit(MoneyUnit unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(MoneyUnit unit) {
        this.unit = unit;
    }

    public Boolean getIsConfirm() {
        return this.isConfirm;
    }

    public Purchase isConfirm(Boolean isConfirm) {
        this.setIsConfirm(isConfirm);
        return this;
    }

    public void setIsConfirm(Boolean isConfirm) {
        this.isConfirm = isConfirm;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Purchase profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Purchase)) {
            return false;
        }
        return id != null && id.equals(((Purchase) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Purchase{" +
            "id=" + getId() +
            ", purchaseType='" + getPurchaseType() + "'" +
            ", confirmedDate='" + getConfirmedDate() + "'" +
            ", money=" + getMoney() +
            ", unit='" + getUnit() + "'" +
            ", isConfirm='" + getIsConfirm() + "'" +
            "}";
    }
}
