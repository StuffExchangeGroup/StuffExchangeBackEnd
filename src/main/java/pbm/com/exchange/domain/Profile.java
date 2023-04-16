package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Profile.
 */
@Entity
@Table(name = "profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "phone")
    private String phone;

    @Column(name = "dob")
    private ZonedDateTime dob;

    @Column(name = "location")
    private String location;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "point")
    private Integer point;

    @Column(name = "uid")
    private String uid;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "profile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "profile" }, allowSetters = true)
    private Set<Favorite> favorites = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sendProduct", "receiveProduct", "owner", "exchanger" }, allowSetters = true)
    private Set<Exchange> ownerExchanges = new HashSet<>();

    @OneToMany(mappedBy = "exchanger")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sendProduct", "receiveProduct", "owner", "exchanger" }, allowSetters = true)
    private Set<Exchange> exchangerExchanges = new HashSet<>();

    @OneToMany(mappedBy = "profile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "profile" }, allowSetters = true)
    private Set<Purchase> purchases = new HashSet<>();

    @OneToMany(mappedBy = "profile")
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

    @OneToMany(mappedBy = "profile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "profile" }, allowSetters = true)
    private Set<NotificationToken> notificationTokens = new HashSet<>();

    @OneToMany(mappedBy = "profile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "profile" }, allowSetters = true)
    private Set<Auction> auctions = new HashSet<>();

    @OneToMany(mappedBy = "profile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "profile" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "profiles", "products", "province" }, allowSetters = true)
    private City city;

    @ManyToOne
    @JsonIgnoreProperties(value = { "profiles" }, allowSetters = true)
    private Level level;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Profile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Profile displayName(String displayName) {
        this.setDisplayName(displayName);
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getBalance() {
        return this.balance;
    }

    public Profile balance(Double balance) {
        this.setBalance(balance);
        return this;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Profile latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Profile longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public Profile avatar(String avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return this.phone;
    }

    public Profile phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ZonedDateTime getDob() {
        return this.dob;
    }

    public Profile dob(ZonedDateTime dob) {
        this.setDob(dob);
        return this;
    }

    public void setDob(ZonedDateTime dob) {
        this.dob = dob;
    }

    public String getLocation() {
        return this.location;
    }

    public Profile location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Profile countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getPoint() {
        return this.point;
    }

    public Profile point(Integer point) {
        this.setPoint(point);
        return this;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Profile user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Favorite> getFavorites() {
        return this.favorites;
    }

    public void setFavorites(Set<Favorite> favorites) {
        if (this.favorites != null) {
            this.favorites.forEach(i -> i.setProfile(null));
        }
        if (favorites != null) {
            favorites.forEach(i -> i.setProfile(this));
        }
        this.favorites = favorites;
    }

    public Profile favorites(Set<Favorite> favorites) {
        this.setFavorites(favorites);
        return this;
    }

    public Profile addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
        favorite.setProfile(this);
        return this;
    }

    public Profile removeFavorite(Favorite favorite) {
        this.favorites.remove(favorite);
        favorite.setProfile(null);
        return this;
    }

    public Set<Exchange> getOwnerExchanges() {
        return this.ownerExchanges;
    }

    public void setOwnerExchanges(Set<Exchange> exchanges) {
        if (this.ownerExchanges != null) {
            this.ownerExchanges.forEach(i -> i.setOwner(null));
        }
        if (exchanges != null) {
            exchanges.forEach(i -> i.setOwner(this));
        }
        this.ownerExchanges = exchanges;
    }

    public Profile ownerExchanges(Set<Exchange> exchanges) {
        this.setOwnerExchanges(exchanges);
        return this;
    }

    public Profile addOwnerExchanges(Exchange exchange) {
        this.ownerExchanges.add(exchange);
        exchange.setOwner(this);
        return this;
    }

    public Profile removeOwnerExchanges(Exchange exchange) {
        this.ownerExchanges.remove(exchange);
        exchange.setOwner(null);
        return this;
    }

    public Set<Exchange> getExchangerExchanges() {
        return this.exchangerExchanges;
    }

    public void setExchangerExchanges(Set<Exchange> exchanges) {
        if (this.exchangerExchanges != null) {
            this.exchangerExchanges.forEach(i -> i.setExchanger(null));
        }
        if (exchanges != null) {
            exchanges.forEach(i -> i.setExchanger(this));
        }
        this.exchangerExchanges = exchanges;
    }

    public Profile exchangerExchanges(Set<Exchange> exchanges) {
        this.setExchangerExchanges(exchanges);
        return this;
    }

    public Profile addExchangerExchanges(Exchange exchange) {
        this.exchangerExchanges.add(exchange);
        exchange.setExchanger(this);
        return this;
    }

    public Profile removeExchangerExchanges(Exchange exchange) {
        this.exchangerExchanges.remove(exchange);
        exchange.setExchanger(null);
        return this;
    }

    public Set<Purchase> getPurchases() {
        return this.purchases;
    }

    public void setPurchases(Set<Purchase> purchases) {
        if (this.purchases != null) {
            this.purchases.forEach(i -> i.setProfile(null));
        }
        if (purchases != null) {
            purchases.forEach(i -> i.setProfile(this));
        }
        this.purchases = purchases;
    }

    public Profile purchases(Set<Purchase> purchases) {
        this.setPurchases(purchases);
        return this;
    }

    public Profile addPurchase(Purchase purchase) {
        this.purchases.add(purchase);
        purchase.setProfile(this);
        return this;
    }

    public Profile removePurchase(Purchase purchase) {
        this.purchases.remove(purchase);
        purchase.setProfile(null);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setProfile(null));
        }
        if (products != null) {
            products.forEach(i -> i.setProfile(this));
        }
        this.products = products;
    }

    public Profile products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Profile addProduct(Product product) {
        this.products.add(product);
        product.setProfile(this);
        return this;
    }

    public Profile removeProduct(Product product) {
        this.products.remove(product);
        product.setProfile(null);
        return this;
    }

    public Set<NotificationToken> getNotificationTokens() {
        return this.notificationTokens;
    }

    public void setNotificationTokens(Set<NotificationToken> notificationTokens) {
        if (this.notificationTokens != null) {
            this.notificationTokens.forEach(i -> i.setProfile(null));
        }
        if (notificationTokens != null) {
            notificationTokens.forEach(i -> i.setProfile(this));
        }
        this.notificationTokens = notificationTokens;
    }

    public Profile notificationTokens(Set<NotificationToken> notificationTokens) {
        this.setNotificationTokens(notificationTokens);
        return this;
    }

    public Profile addNotificationToken(NotificationToken notificationToken) {
        this.notificationTokens.add(notificationToken);
        notificationToken.setProfile(this);
        return this;
    }

    public Profile removeNotificationToken(NotificationToken notificationToken) {
        this.notificationTokens.remove(notificationToken);
        notificationToken.setProfile(null);
        return this;
    }

    public Set<Auction> getAuctions() {
        return this.auctions;
    }

    public void setAuctions(Set<Auction> auctions) {
        if (this.auctions != null) {
            this.auctions.forEach(i -> i.setProfile(null));
        }
        if (auctions != null) {
            auctions.forEach(i -> i.setProfile(this));
        }
        this.auctions = auctions;
    }

    public Profile auctions(Set<Auction> auctions) {
        this.setAuctions(auctions);
        return this;
    }

    public Profile addAuction(Auction auction) {
        this.auctions.add(auction);
        auction.setProfile(this);
        return this;
    }

    public Profile removeAuction(Auction auction) {
        this.auctions.remove(auction);
        auction.setProfile(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setProfile(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setProfile(this));
        }
        this.comments = comments;
    }

    public Profile comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Profile addComment(Comment comment) {
        this.comments.add(comment);
        comment.setProfile(this);
        return this;
    }

    public Profile removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setProfile(null);
        return this;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Profile city(City city) {
        this.setCity(city);
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Profile level(Level level) {
        this.setLevel(level);
        return this;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        return id != null && id.equals(((Profile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profile{" +
            "id=" + getId() +
            ", displayName='" + getDisplayName() + "'" +
            ", balance=" + getBalance() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", avatar='" + getAvatar() + "'" +
            ", phone='" + getPhone() + "'" +
            ", dob='" + getDob() + "'" +
            ", location='" + getLocation() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", point=" + getPoint() +
            "}";
    }
}
