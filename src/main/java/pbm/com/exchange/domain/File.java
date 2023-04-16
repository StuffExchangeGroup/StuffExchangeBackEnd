package pbm.com.exchange.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A File.
 */
@Entity
@Table(name = "file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_on_server")
    private String fileOnServer;

    @Column(name = "relative_path")
    private String relativePath;

    @Column(name = "amazon_s_3_url")
    private String amazonS3Url;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public File id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public File fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileOnServer() {
        return this.fileOnServer;
    }

    public File fileOnServer(String fileOnServer) {
        this.setFileOnServer(fileOnServer);
        return this;
    }

    public void setFileOnServer(String fileOnServer) {
        this.fileOnServer = fileOnServer;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public File relativePath(String relativePath) {
        this.setRelativePath(relativePath);
        return this;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getAmazonS3Url() {
        return this.amazonS3Url;
    }

    public File amazonS3Url(String amazonS3Url) {
        this.setAmazonS3Url(amazonS3Url);
        return this;
    }

    public void setAmazonS3Url(String amazonS3Url) {
        this.amazonS3Url = amazonS3Url;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof File)) {
            return false;
        }
        return id != null && id.equals(((File) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "File{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileOnServer='" + getFileOnServer() + "'" +
            ", relativePath='" + getRelativePath() + "'" +
            ", amazonS3Url='" + getAmazonS3Url() + "'" +
            "}";
    }
}
