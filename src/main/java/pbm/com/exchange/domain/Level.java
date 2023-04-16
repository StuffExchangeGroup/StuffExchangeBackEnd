package pbm.com.exchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Level.
 */
@Entity
@Table(name = "level")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Level implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "swap_limit")
    private Integer swapLimit;

    @OneToMany(mappedBy = "level")
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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Level id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Level name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSwapLimit() {
        return this.swapLimit;
    }

    public Level swapLimit(Integer swapLimit) {
        this.setSwapLimit(swapLimit);
        return this;
    }

    public void setSwapLimit(Integer swapLimit) {
        this.swapLimit = swapLimit;
    }

    public Set<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        if (this.profiles != null) {
            this.profiles.forEach(i -> i.setLevel(null));
        }
        if (profiles != null) {
            profiles.forEach(i -> i.setLevel(this));
        }
        this.profiles = profiles;
    }

    public Level profiles(Set<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Level addProfile(Profile profile) {
        this.profiles.add(profile);
        profile.setLevel(this);
        return this;
    }

    public Level removeProfile(Profile profile) {
        this.profiles.remove(profile);
        profile.setLevel(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Level)) {
            return false;
        }
        return id != null && id.equals(((Level) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Level{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", swapLimit=" + getSwapLimit() +
            "}";
    }
}
