package com.railsetu.repository;

import com.railsetu.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop30ByOrderByCreatedAtDesc();
    List<Notification> findByRecipientOrRecipientOrderByCreatedAtDesc(String recipient1, String recipient2);
    long countByIsReadFalse();
}
