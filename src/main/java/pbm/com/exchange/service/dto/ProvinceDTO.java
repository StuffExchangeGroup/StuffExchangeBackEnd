package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Province} entity.
 */
public class ProvinceDTO implements Serializable {

    private Long id;

    private String name;

    private NationalityDTO nationality;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NationalityDTO getNationality() {
        return nationality;
    }

    public void setNationality(NationalityDTO nationality) {
        this.nationality = nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProvinceDTO)) {
            return false;
        }

        ProvinceDTO provinceDTO = (ProvinceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, provinceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProvinceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", nationality=" + getNationality() +
            "}";
    }
}
