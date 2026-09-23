package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_versions")
public class TrainVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long trainId;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(nullable = false, length = 80)
    private String createdBy;

    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(length = 255)
    private String changeReason;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String previousConfiguration;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String newConfiguration;

    public TrainVersion() {}

    public TrainVersion(Long trainId, Integer versionNumber, String createdBy, String changeReason, String previousConfiguration, String newConfiguration) {
        this.trainId = trainId;
        this.versionNumber = versionNumber;
        this.createdBy = createdBy;
        this.changeReason = changeReason;
        this.previousConfiguration = previousConfiguration;
        this.newConfiguration = newConfiguration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getPreviousConfiguration() {
        return previousConfiguration;
    }

    public void setPreviousConfiguration(String previousConfiguration) {
        this.previousConfiguration = previousConfiguration;
    }

    public String getNewConfiguration() {
        return newConfiguration;
    }

    public void setNewConfiguration(String newConfiguration) {
        this.newConfiguration = newConfiguration;
    }
}
