package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import pbm.com.exchange.domain.enumeration.ConfirmStatus;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;

/**
 * A Exchange.
 */
@Entity
@Table(name = "exchange")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exchange extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_confirm")
    private ConfirmStatus ownerConfirm;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchanger_confirm")
    private ConfirmStatus exchangerConfirm;

    @Column(name = "confirm_phone")
    private String confirmPhone;

    @Column(name = "chatting")
    private Boolean chatting;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExchangeStatus status;

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
    private Product sendProduct;

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
    private Product receiveProduct;

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
    private Profile owner;

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
    private Profile exchanger;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exchange id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Exchange active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ConfirmStatus getOwnerConfirm() {
        return this.ownerConfirm;
    }

    public Exchange ownerConfirm(ConfirmStatus ownerConfirm) {
        this.setOwnerConfirm(ownerConfirm);
        return this;
    }

    public void setOwnerConfirm(ConfirmStatus ownerConfirm) {
        this.ownerConfirm = ownerConfirm;
    }

    public ConfirmStatus getExchangerConfirm() {
        return this.exchangerConfirm;
    }

    public Exchange exchangerConfirm(ConfirmStatus exchangerConfirm) {
        this.setExchangerConfirm(exchangerConfirm);
        return this;
    }

    public void setExchangerConfirm(ConfirmStatus exchangerConfirm) {
        this.exchangerConfirm = exchangerConfirm;
    }

    public String getConfirmPhone() {
        return this.confirmPhone;
    }

    public Exchange confirmPhone(String confirmPhone) {
        this.setConfirmPhone(confirmPhone);
        return this;
    }

    public void setConfirmPhone(String confirmPhone) {
        this.confirmPhone = confirmPhone;
    }

    public Boolean getChatting() {
        return this.chatting;
    }

    public Exchange chatting(Boolean chatting) {
        this.setChatting(chatting);
        return this;
    }

    public void setChatting(Boolean chatting) {
        this.chatting = chatting;
    }

    public ExchangeStatus getStatus() {
        return this.status;
    }

    public Exchange status(ExchangeStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }

    public Product getSendProduct() {
        return this.sendProduct;
    }

    public void setSendProduct(Product product) {
        this.sendProduct = product;
    }

    public Exchange sendProduct(Product product) {
        this.setSendProduct(product);
        return this;
    }

    public Product getReceiveProduct() {
        return this.receiveProduct;
    }

    public void setReceiveProduct(Product product) {
        this.receiveProduct = product;
    }

    public Exchange receiveProduct(Product product) {
        this.setReceiveProduct(product);
        return this;
    }

    public Profile getOwner() {
        return this.owner;
    }

    public void setOwner(Profile profile) {
        this.owner = profile;
    }

    public Exchange owner(Profile profile) {
        this.setOwner(profile);
        return this;
    }

    public Profile getExchanger() {
        return this.exchanger;
    }

    public void setExchanger(Profile profile) {
        this.exchanger = profile;
    }

    public Exchange exchanger(Profile profile) {
        this.setExchanger(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exchange)) {
            return false;
        }
        return id != null && id.equals(((Exchange) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exchange{" +
            "id=" + getId() +
            ", active='" + getActive() + "'" +
            ", ownerConfirm='" + getOwnerConfirm() + "'" +
            ", exchangerConfirm='" + getExchangerConfirm() + "'" +
            ", confirmPhone='" + getConfirmPhone() + "'" +
            ", chatting='" + getChatting() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
