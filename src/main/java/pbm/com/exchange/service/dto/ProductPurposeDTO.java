package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;
import pbm.com.exchange.domain.enumeration.PurposeType;

/**
 * A DTO for the {@link pbm.com.exchange.domain.ProductPurpose} entity.
 */
public class ProductPurposeDTO implements Serializable {

    private Long id;

    private PurposeType name;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurposeType getName() {
        return name;
    }

    public void setName(PurposeType name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductPurposeDTO)) {
            return false;
        }

        ProductPurposeDTO productPurposeDTO = (ProductPurposeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productPurposeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductPurposeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
