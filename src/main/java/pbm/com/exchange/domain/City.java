package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A City.
 */
@Entity
@Table(name = "city")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "city")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Profile> profiles = new HashSet<>();

    @OneToMany(mappedBy = "city")
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

    @ManyToOne
    @JsonIgnoreProperties(value = { "cities", "nationality" }, allowSetters = true)
    private Province province;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public City id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public City name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        if (this.profiles != null) {
            this.profiles.forEach(i -> i.setCity(null));
        }
        if (profiles != null) {
            profiles.forEach(i -> i.setCity(this));
        }
        this.profiles = profiles;
    }

    public City profiles(Set<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public City addProfile(Profile profile) {
        this.profiles.add(profile);
        profile.setCity(this);
        return this;
    }

    public City removeProfile(Profile profile) {
        this.profiles.remove(profile);
        profile.setCity(null);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setCity(null));
        }
        if (products != null) {
            products.forEach(i -> i.setCity(this));
        }
        this.products = products;
    }

    public City products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public City addProduct(Product product) {
        this.products.add(product);
        product.setCity(this);
        return this;
    }

    public City removeProduct(Product product) {
        this.products.remove(product);
        product.setCity(null);
        return this;
    }

    public Province getProvince() {
        return this.province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City province(Province province) {
        this.setProvince(province);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof City)) {
            return false;
        }
        return id != null && id.equals(((City) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "City [id=" + id + ", name=" + name + ", profiles=" + profiles + ", products=" + products + ", province=" + province + "]";
    }
}
