package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String recipient; // ADMIN_BROADCAST, or specific username

    @Column(nullable = false, length = 120)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 50)
    private String notificationType; // HIGH_OCCUPANCY, LOW_INVENTORY, FARE_EXPIRY, SCHEDULED_FARE_PENDING, SUSPENSION_IMPACT, AUDIT_ALERT

    @Column(length = 20)
    private String severity = "INFO"; // INFO, WARNING, CRITICAL

    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(String recipient, String title, String message, String notificationType, String severity) {
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.severity = severity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
