package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_versions")
public class ScheduleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long trainId;

    @Column(nullable = false)
    private Integer versionNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String scheduleSnapshotJson;

    @Column(length = 80)
    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 255)
    private String changeReason;

    public ScheduleVersion() {}

    public ScheduleVersion(Long trainId, Integer versionNumber, String scheduleSnapshotJson, String createdBy, String changeReason) {
        this.trainId = trainId;
        this.versionNumber = versionNumber;
        this.scheduleSnapshotJson = scheduleSnapshotJson;
        this.createdBy = createdBy;
        this.changeReason = changeReason;
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

    public String getScheduleSnapshotJson() {
        return scheduleSnapshotJson;
    }

    public void setScheduleSnapshotJson(String scheduleSnapshotJson) {
        this.scheduleSnapshotJson = scheduleSnapshotJson;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
}
