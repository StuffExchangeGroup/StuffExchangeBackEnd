package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import pbm.com.exchange.domain.enumeration.PurposeType;

/**
 * A ProductPurpose.
 */
@Entity
@Table(name = "product_purpose")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductPurpose implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private PurposeType name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "purposes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductPurpose id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurposeType getName() {
        return this.name;
    }

    public ProductPurpose name(PurposeType name) {
        this.setName(name);
        return this;
    }

    public void setName(PurposeType name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductPurpose description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removePurpose(this));
        }
        if (products != null) {
            products.forEach(i -> i.addPurpose(this));
        }
        this.products = products;
    }

    public ProductPurpose products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public ProductPurpose addProduct(Product product) {
        this.products.add(product);
        product.getPurposes().add(this);
        return this;
    }

    public ProductPurpose removeProduct(Product product) {
        this.products.remove(product);
        product.getPurposes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductPurpose)) {
            return false;
        }
        return id != null && id.equals(((ProductPurpose) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductPurpose{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
