package com.railsetu.controller;

import com.railsetu.domain.AdminActivityLog;
import com.railsetu.domain.Notification;
import com.railsetu.service.AuditLogService;
import com.railsetu.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuditNotificationController {

    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AdminAuditNotificationController(AuditLogService auditLogService, NotificationService notificationService) {
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    /**
     * Section 35: Immutable Admin Activity Audit Logs
     */
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<AdminActivityLog>> searchAuditLogs(
            @RequestParam(required = false) String admin,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(auditLogService.searchLogs(admin, action, entityType, pageable));
    }

    /**
     * Section 27: Admin Notification Center
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATIONS_ADMIN', 'FARE_ADMIN', 'BOOKING_ADMIN', 'VERIFIER')")
    public ResponseEntity<List<Notification>> getNotifications() {
        return ResponseEntity.ok(notificationService.getRecentNotifications());
    }

    @PutMapping("/notifications/{id}/read")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATIONS_ADMIN', 'FARE_ADMIN', 'BOOKING_ADMIN', 'VERIFIER')")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    @PutMapping("/notifications/read-all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATIONS_ADMIN', 'FARE_ADMIN', 'BOOKING_ADMIN', 'VERIFIER')")
    public ResponseEntity<Map<String, String>> markAllRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
