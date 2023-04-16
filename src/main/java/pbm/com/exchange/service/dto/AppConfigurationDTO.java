package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pbm.com.exchange.domain.AppConfiguration} entity.
 */
public class AppConfigurationDTO implements Serializable {

    private Long id;

    private String key;

    private String value;

    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppConfigurationDTO)) {
            return false;
        }

        AppConfigurationDTO appConfigurationDTO = (AppConfigurationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appConfigurationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppConfigurationDTO{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
