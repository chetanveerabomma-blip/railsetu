package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_activity_logs", indexes = {
        @Index(name = "idx_admin_audit_timestamp", columnList = "timestamp"),
        @Index(name = "idx_admin_audit_entity", columnList = "entity_type, entity_id")
})
public class AdminActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String adminUsername;

    @Column(nullable = false, length = 60)
    private String action; // CREATE_TRAIN, UPDATE_FARE, SUSPEND_TRAIN, APPROVE_FARE, CANCEL_BOOKING

    @Column(name = "entity_type", nullable = false, length = 60)
    private String entityType; // TRAIN, FARE, ROUTE, SCHEDULE, BOOKING

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String previousValue;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(length = 255)
    private String changeReason;

    @Column(length = 60)
    private String ipAddress = "127.0.0.1";

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AdminActivityLog() {}

    public AdminActivityLog(String adminUsername, String action, String entityType, Long entityId, String previousValue, String newValue, String changeReason) {
        this.adminUsername = adminUsername;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.changeReason = changeReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
