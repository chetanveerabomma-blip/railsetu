package com.railsetu.service;

import com.railsetu.domain.AdminActivityLog;
import com.railsetu.repository.AdminActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AdminActivityLogRepository auditLogRepository;

    public AuditLogService(AdminActivityLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AdminActivityLog logActivity(String adminUsername, String action, String entityType,
                                        Long entityId, String previousValue, String newValue,
                                        String changeReason) {
        AdminActivityLog log = new AdminActivityLog();
        log.setAdminUsername(adminUsername != null ? adminUsername : "SYSTEM");
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPreviousValue(previousValue);
        log.setNewValue(newValue);
        log.setChangeReason(changeReason);
        log.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AdminActivityLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public Page<AdminActivityLog> searchLogs(String admin, String action, String entityType, Pageable pageable) {
        return auditLogRepository.searchLogs(admin, action, entityType, pageable);
    }
}
