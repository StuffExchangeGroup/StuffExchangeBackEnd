package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Level} entity.
 */
public class LevelDTO implements Serializable {

    private Long id;

    private String name;

    private Integer swapLimit;

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

    public Integer getSwapLimit() {
        return swapLimit;
    }

    public void setSwapLimit(Integer swapLimit) {
        this.swapLimit = swapLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LevelDTO)) {
            return false;
        }

        LevelDTO levelDTO = (LevelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, levelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LevelDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", swapLimit=" + getSwapLimit() +
            "}";
    }
}
