package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductStatus;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "notice")
    private String notice;

    @Column(name = "location")
    private String location;

    @Column(name = "verify_phone")
    private String verifyPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "sell_point")
    private Integer sellPoint;

    @Column(name = "favorite_count")
    private Integer favoriteCount;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_condition")
    private Condition condition;

    @Column(name = "request_count")
    private Integer requestCount;

    @Column(name = "receive_count")
    private Integer receiveCount;

    @Column(name = "is_swap_available")
    private Boolean isSwapAvailable;

    @Column(name = "is_exchange", columnDefinition = "boolean default true")
    private Boolean isExchange;

    @Column(name = "is_auction", columnDefinition = "boolean default false")
    private Boolean isAuction;

    @Column(name = "is_auction_now", columnDefinition = "boolean default false")
    private Boolean isAuctionNow;

    @Column(name = "is_sell", columnDefinition = "boolean default false")
    private Boolean isSell;

    @Column(name = "is_gift", columnDefinition = "boolean default false")
    private Boolean isGift;

    @Column(name = "start_auction_time")
    private ZonedDateTime startAuctionTime;

    @Column(name = "end_auction_time")
    private ZonedDateTime endAuctionTime;

    @Column(name = "sale_point")
    private Double salePoint;

    @Column(name = "auction_point")
    private Double auctionPoint;

    @Column(name = "current_point")
    private Double currentPoint;

    @Column(name = "is_block", columnDefinition = "boolean default false")
    private Boolean isBlock ;
    
    public Boolean getIsBlock() {
		return this.isBlock;
	}

	public void setIsBlock(Boolean isBlock) {
		this.isBlock = isBlock;
	}

	@JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Rating rating;

    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "imageFile", "product" }, allowSetters = true)
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "sendProduct")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sendProduct", "receiveProduct", "owner", "exchanger" }, allowSetters = true)
    private Set<Exchange> sendExchanges = new HashSet<>();

    @OneToMany(mappedBy = "receiveProduct")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sendProduct", "receiveProduct", "owner", "exchanger" }, allowSetters = true)
    private Set<Exchange> receiveExchanges = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "category" }, allowSetters = true)
    private Set<ProductCategory> productCategories = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "profile" }, allowSetters = true)
    private Set<Favorite> favorites = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "profile" }, allowSetters = true)
    private Set<Auction> auctions = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "profile" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_product__purpose",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "purpose_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<ProductPurpose> purposes = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "profiles", "products", "province" }, allowSetters = true)
    private City city;

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

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotice() {
        return this.notice;
    }

    public Product notice(String notice) {
        this.setNotice(notice);
        return this;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getLocation() {
        return this.location;
    }

    public Product location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVerifyPhone() {
        return this.verifyPhone;
    }

    public Product verifyPhone(String verifyPhone) {
        this.setVerifyPhone(verifyPhone);
        return this;
    }

    public void setVerifyPhone(String verifyPhone) {
        this.verifyPhone = verifyPhone;
    }

    public ProductStatus getStatus() {
        return this.status;
    }

    public Product status(ProductStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Product active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public Product thumbnail(String thumbnail) {
        this.setThumbnail(thumbnail);
        return this;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(Integer sellPoint) {
        this.sellPoint = sellPoint;
    }

    public Integer getFavoriteCount() {
        return this.favoriteCount;
    }

    public Product favoriteCount(Integer favoriteCount) {
        this.setFavoriteCount(favoriteCount);
        return this;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Product latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Product longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public Product condition(Condition condition) {
        this.setCondition(condition);
        return this;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Integer getRequestCount() {
        return this.requestCount;
    }

    public Product requestCount(Integer requestCount) {
        this.setRequestCount(requestCount);
        return this;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getReceiveCount() {
        return this.receiveCount;
    }

    public Product receiveCount(Integer receiveCount) {
        this.setReceiveCount(receiveCount);
        return this;
    }

    public void setReceiveCount(Integer receiveCount) {
        this.receiveCount = receiveCount;
    }

    public Boolean getIsSwapAvailable() {
        return this.isSwapAvailable;
    }

    public Product isSwapAvailable(Boolean isSwapAvailable) {
        this.setIsSwapAvailable(isSwapAvailable);
        return this;
    }

    public void setIsSwapAvailable(Boolean isSwapAvailable) {
        this.isSwapAvailable = isSwapAvailable;
    }

    public ZonedDateTime getStartAuctionTime() {
        return this.startAuctionTime;
    }

    public Product auctionTime(ZonedDateTime startAuctionTime) {
        this.setStartAuctionTime(startAuctionTime);
        return this;
    }

    public void setStartAuctionTime(ZonedDateTime startAuctionTime) {
        this.startAuctionTime = startAuctionTime;
    }

    public Boolean getIsAuctionNow() {
        return isAuctionNow;
    }

    public void setIsAuctionNow(Boolean isAuctionNow) {
        this.isAuctionNow = isAuctionNow;
    }

    public ZonedDateTime getEndAuctionTime() {
        return endAuctionTime;
    }

    public void setEndAuctionTime(ZonedDateTime endAuctionTime) {
        this.endAuctionTime = endAuctionTime;
    }

    public Rating getRating() {
        return this.rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Product rating(Rating rating) {
        this.setRating(rating);
        return this;
    }

    public Set<Image> getImages() {
        return this.images;
    }

    public void setImages(Set<Image> images) {
        if (this.images != null) {
            this.images.forEach(i -> i.setProduct(null));
        }
        if (images != null) {
            images.forEach(i -> i.setProduct(this));
        }
        this.images = images;
    }

    public Product images(Set<Image> images) {
        this.setImages(images);
        return this;
    }

    public Product addImage(Image image) {
        this.images.add(image);
        image.setProduct(this);
        return this;
    }

    public Product removeImage(Image image) {
        this.images.remove(image);
        image.setProduct(null);
        return this;
    }

    public Set<Exchange> getSendExchanges() {
        return this.sendExchanges;
    }

    public void setSendExchanges(Set<Exchange> exchanges) {
        if (this.sendExchanges != null) {
            this.sendExchanges.forEach(i -> i.setSendProduct(null));
        }
        if (exchanges != null) {
            exchanges.forEach(i -> i.setSendProduct(this));
        }
        this.sendExchanges = exchanges;
    }

    public Product sendExchanges(Set<Exchange> exchanges) {
        this.setSendExchanges(exchanges);
        return this;
    }

    public Product addSendExchanges(Exchange exchange) {
        this.sendExchanges.add(exchange);
        exchange.setSendProduct(this);
        return this;
    }

    public Product removeSendExchanges(Exchange exchange) {
        this.sendExchanges.remove(exchange);
        exchange.setSendProduct(null);
        return this;
    }

    public Set<Exchange> getReceiveExchanges() {
        return this.receiveExchanges;
    }

    public void setReceiveExchanges(Set<Exchange> exchanges) {
        if (this.receiveExchanges != null) {
            this.receiveExchanges.forEach(i -> i.setReceiveProduct(null));
        }
        if (exchanges != null) {
            exchanges.forEach(i -> i.setReceiveProduct(this));
        }
        this.receiveExchanges = exchanges;
    }

    public Product receiveExchanges(Set<Exchange> exchanges) {
        this.setReceiveExchanges(exchanges);
        return this;
    }

    public Product addReceiveExchanges(Exchange exchange) {
        this.receiveExchanges.add(exchange);
        exchange.setReceiveProduct(this);
        return this;
    }

    public Product removeReceiveExchanges(Exchange exchange) {
        this.receiveExchanges.remove(exchange);
        exchange.setReceiveProduct(null);
        return this;
    }

    public Set<ProductCategory> getProductCategories() {
        return this.productCategories;
    }

    public void setProductCategories(Set<ProductCategory> productCategories) {
        if (this.productCategories != null) {
            this.productCategories.forEach(i -> i.setProduct(null));
        }
        if (productCategories != null) {
            productCategories.forEach(i -> i.setProduct(this));
        }
        this.productCategories = productCategories;
    }

    public Product productCategories(Set<ProductCategory> productCategories) {
        this.setProductCategories(productCategories);
        return this;
    }

    public Product addProductCategory(ProductCategory productCategory) {
        this.productCategories.add(productCategory);
        productCategory.setProduct(this);
        return this;
    }

    public Product removeProductCategory(ProductCategory productCategory) {
        this.productCategories.remove(productCategory);
        productCategory.setProduct(null);
        return this;
    }

    public Set<Favorite> getFavorites() {
        return this.favorites;
    }

    public void setFavorites(Set<Favorite> favorites) {
        if (this.favorites != null) {
            this.favorites.forEach(i -> i.setProduct(null));
        }
        if (favorites != null) {
            favorites.forEach(i -> i.setProduct(this));
        }
        this.favorites = favorites;
    }

    public Product favorites(Set<Favorite> favorites) {
        this.setFavorites(favorites);
        return this;
    }

    public Product addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
        favorite.setProduct(this);
        return this;
    }

    public Product removeFavorite(Favorite favorite) {
        this.favorites.remove(favorite);
        favorite.setProduct(null);
        return this;
    }

    public Set<Auction> getAuctions() {
        return this.auctions;
    }

    public void setAuctions(Set<Auction> auctions) {
        if (this.auctions != null) {
            this.auctions.forEach(i -> i.setProduct(null));
        }
        if (auctions != null) {
            auctions.forEach(i -> i.setProduct(this));
        }
        this.auctions = auctions;
    }

    public Product auctions(Set<Auction> auctions) {
        this.setAuctions(auctions);
        return this;
    }

    public Product addAuction(Auction auction) {
        this.auctions.add(auction);
        auction.setProduct(this);
        return this;
    }

    public Product removeAuction(Auction auction) {
        this.auctions.remove(auction);
        auction.setProduct(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setProduct(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setProduct(this));
        }
        this.comments = comments;
    }

    public Product comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Product addComment(Comment comment) {
        this.comments.add(comment);
        comment.setProduct(this);
        return this;
    }

    public Product removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setProduct(null);
        return this;
    }

    public Set<ProductPurpose> getPurposes() {
        return this.purposes;
    }

    public void setPurposes(Set<ProductPurpose> productPurposes) {
        this.purposes = productPurposes;
    }

    public Product purposes(Set<ProductPurpose> productPurposes) {
        this.setPurposes(productPurposes);
        return this;
    }

    public Product addPurpose(ProductPurpose productPurpose) {
        this.purposes.add(productPurpose);
        productPurpose.getProducts().add(this);
        return this;
    }

    public Product removePurpose(ProductPurpose productPurpose) {
        this.purposes.remove(productPurpose);
        productPurpose.getProducts().remove(this);
        return this;
    }

    public Boolean getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(Boolean isExchange) {
        this.isExchange = isExchange;
    }

    public Boolean getIsAuction() {
        return isAuction;
    }

    public void setIsAuction(Boolean isAuction) {
        this.isAuction = isAuction;
    }

    public Boolean getIsSell() {
        return isSell;
    }

    public void setIsSell(Boolean isSell) {
        this.isSell = isSell;
    }

    public Boolean getIsGift() {
        return isGift;
    }

    public void setIsGift(Boolean isGift) {
        this.isGift = isGift;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Product city(City city) {
        this.setCity(city);
        return this;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Product profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public Double getSalePoint() {
        return salePoint;
    }

    public void setSalePoint(Double salePoint) {
        this.salePoint = salePoint;
    }

    public Double getAuctionPoint() {
        return auctionPoint;
    }

    public void setAuctionPoint(Double auctionPoint) {
        this.auctionPoint = auctionPoint;
    }

    public Double getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Double currentPoint) {
        this.currentPoint = currentPoint;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", notice='" + getNotice() + "'" +
            ", location='" + getLocation() + "'" +
            ", verifyPhone='" + getVerifyPhone() + "'" +
            ", status='" + getStatus() + "'" +
            ", active='" + getActive() + "'" +
            ", thumbnail='" + getThumbnail() + "'" +
            ", favoriteCount=" + getFavoriteCount() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", condition='" + getCondition() + "'" +
            ", requestCount=" + getRequestCount() +
            ", receiveCount=" + getReceiveCount() +
            ", isSwapAvailable='" + getIsSwapAvailable() + "'" +
            ", auctionTime='" + getStartAuctionTime() + "'" +
            "}";
    }
}
