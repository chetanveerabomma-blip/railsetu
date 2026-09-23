package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_versions")
public class RouteVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long trainId;

    @Column(nullable = false)
    private Integer versionNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String routeSnapshotJson;

    @Column(length = 80)
    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 255)
    private String changeReason;

    public RouteVersion() {}

    public RouteVersion(Long trainId, Integer versionNumber, String routeSnapshotJson, String createdBy, String changeReason) {
        this.trainId = trainId;
        this.versionNumber = versionNumber;
        this.routeSnapshotJson = routeSnapshotJson;
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

    public String getRouteSnapshotJson() {
        return routeSnapshotJson;
    }

    public void setRouteSnapshotJson(String routeSnapshotJson) {
        this.routeSnapshotJson = routeSnapshotJson;
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
