package io.repsy.repo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
public class PackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;
    private String author;
    private LocalDateTime uploadDate;
    private String metaFilePath;
    private String packageFilePath;
    private String storageType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public String getMetaFilePath() { return metaFilePath; }
    public void setMetaFilePath(String metaFilePath) { this.metaFilePath = metaFilePath; }

    public String getPackageFilePath() { return packageFilePath; }
    public void setPackageFilePath(String packageFilePath) { this.packageFilePath = packageFilePath; }

    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }
}