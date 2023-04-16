package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pbm.com.exchange.domain.Category} entity.
 */
public class CategoryDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private String thumbnail;

    private Boolean active;

    private FileDTO categoryFile;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public FileDTO getCategoryFile() {
        return categoryFile;
    }

    public void setCategoryFile(FileDTO categoryFile) {
        this.categoryFile = categoryFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryDTO)) {
            return false;
        }

        CategoryDTO categoryDTO = (CategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, categoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbnail='" + getThumbnail() + "'" +
            ", active='" + getActive() + "'" +
            ", categoryFile=" + getCategoryFile() +
            "}";
    }
}
