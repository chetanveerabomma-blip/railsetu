package com.railsetu.repository;

import com.railsetu.domain.AdminActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Long> {

    List<AdminActivityLog> findTop50ByOrderByTimestampDesc();

    @Query("SELECT a FROM AdminActivityLog a WHERE " +
           "(:admin IS NULL OR LOWER(a.adminUsername) LIKE LOWER(CONCAT('%', :admin, '%'))) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType) ORDER BY a.timestamp DESC")
    Page<AdminActivityLog> searchLogs(@Param("admin") String admin,
                                      @Param("action") String action,
                                      @Param("entityType") String entityType,
                                      Pageable pageable);
}
