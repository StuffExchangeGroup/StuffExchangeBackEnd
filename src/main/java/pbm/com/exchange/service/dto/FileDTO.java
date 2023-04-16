package pbm.com.exchange.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link pbm.com.exchange.domain.File} entity.
 */
public class FileDTO implements Serializable {

    private Long id;

    @NotNull
    private String fileName;

    private String fileOnServer;

    private String relativePath;

    private String amazonS3Url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileOnServer() {
        return fileOnServer;
    }

    public void setFileOnServer(String fileOnServer) {
        this.fileOnServer = fileOnServer;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getAmazonS3Url() {
        return amazonS3Url;
    }

    public void setAmazonS3Url(String amazonS3Url) {
        this.amazonS3Url = amazonS3Url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileDTO)) {
            return false;
        }

        FileDTO fileDTO = (FileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileOnServer='" + getFileOnServer() + "'" +
            ", relativePath='" + getRelativePath() + "'" +
            ", amazonS3Url='" + getAmazonS3Url() + "'" +
            "}";
    }
}
