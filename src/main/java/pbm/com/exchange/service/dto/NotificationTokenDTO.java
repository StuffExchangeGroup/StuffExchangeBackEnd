package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pbm.com.exchange.domain.NotificationToken} entity.
 */
public class NotificationTokenDTO implements Serializable {

    private Long id;

    private String token;

    private ProfileDTO profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        if (!(o instanceof NotificationTokenDTO)) {
            return false;
        }

        NotificationTokenDTO notificationTokenDTO = (NotificationTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationTokenDTO{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", profile=" + getProfile() +
            "}";
    }
}
