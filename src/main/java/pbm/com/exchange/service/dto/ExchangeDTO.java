package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;
import pbm.com.exchange.domain.enumeration.ConfirmStatus;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Exchange} entity.
 */
public class ExchangeDTO implements Serializable {

    private Long id;

    private Boolean active;

    private ConfirmStatus ownerConfirm;

    private ConfirmStatus exchangerConfirm;

    private String confirmPhone;

    private Boolean chatting;

    private ExchangeStatus status;

    private ProductDTO sendProduct;

    private ProductDTO receiveProduct;

    private ProfileDTO owner;

    private ProfileDTO exchanger;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ConfirmStatus getOwnerConfirm() {
        return ownerConfirm;
    }

    public void setOwnerConfirm(ConfirmStatus ownerConfirm) {
        this.ownerConfirm = ownerConfirm;
    }

    public ConfirmStatus getExchangerConfirm() {
        return exchangerConfirm;
    }

    public void setExchangerConfirm(ConfirmStatus exchangerConfirm) {
        this.exchangerConfirm = exchangerConfirm;
    }

    public String getConfirmPhone() {
        return confirmPhone;
    }

    public void setConfirmPhone(String confirmPhone) {
        this.confirmPhone = confirmPhone;
    }

    public Boolean getChatting() {
        return chatting;
    }

    public void setChatting(Boolean chatting) {
        this.chatting = chatting;
    }

    public ExchangeStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }

    public ProductDTO getSendProduct() {
        return sendProduct;
    }

    public void setSendProduct(ProductDTO sendProduct) {
        this.sendProduct = sendProduct;
    }

    public ProductDTO getReceiveProduct() {
        return receiveProduct;
    }

    public void setReceiveProduct(ProductDTO receiveProduct) {
        this.receiveProduct = receiveProduct;
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }

    public ProfileDTO getExchanger() {
        return exchanger;
    }

    public void setExchanger(ProfileDTO exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExchangeDTO)) {
            return false;
        }

        ExchangeDTO exchangeDTO = (ExchangeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, exchangeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExchangeDTO{" +
            "id=" + getId() +
            ", active='" + getActive() + "'" +
            ", ownerConfirm='" + getOwnerConfirm() + "'" +
            ", exchangerConfirm='" + getExchangerConfirm() + "'" +
            ", confirmPhone='" + getConfirmPhone() + "'" +
            ", chatting='" + getChatting() + "'" +
            ", status='" + getStatus() + "'" +
            ", sendProduct=" + getSendProduct() +
            ", receiveProduct=" + getReceiveProduct() +
            ", owner=" + getOwner() +
            ", exchanger=" + getExchanger() +
            "}";
    }
}
