package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import pbm.com.exchange.domain.enumeration.MoneyUnit;
import pbm.com.exchange.domain.enumeration.PurchaseType;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Purchase} entity.
 */
public class PurchaseDTO implements Serializable {

    private Long id;

    private PurchaseType purchaseType;

    private ZonedDateTime confirmedDate;

    private Double money;

    private MoneyUnit unit;

    private Boolean isConfirm;

    private ProfileDTO profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseType getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(PurchaseType purchaseType) {
        this.purchaseType = purchaseType;
    }

    public ZonedDateTime getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(ZonedDateTime confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public MoneyUnit getUnit() {
        return unit;
    }

    public void setUnit(MoneyUnit unit) {
        this.unit = unit;
    }

    public Boolean getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(Boolean isConfirm) {
        this.isConfirm = isConfirm;
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
        if (!(o instanceof PurchaseDTO)) {
            return false;
        }

        PurchaseDTO purchaseDTO = (PurchaseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseDTO{" +
            "id=" + getId() +
            ", purchaseType='" + getPurchaseType() + "'" +
            ", confirmedDate='" + getConfirmedDate() + "'" +
            ", money=" + getMoney() +
            ", unit='" + getUnit() + "'" +
            ", isConfirm='" + getIsConfirm() + "'" +
            ", profile=" + getProfile() +
            "}";
    }
}
