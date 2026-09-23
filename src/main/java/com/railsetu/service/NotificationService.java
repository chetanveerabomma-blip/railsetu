package com.railsetu.service;

import com.railsetu.domain.Notification;
import com.railsetu.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification sendNotification(String recipient, String title, String message, String type, String severity) {
        Notification notification = new Notification(recipient, title, message, type, severity);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void notifyAdmins(String title, String message, String type, String severity) {
        sendNotification("ADMIN_BROADCAST", title, message, type, severity);
    }

    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications() {
        return notificationRepository.findTop30ByOrderByCreatedAtDesc();
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findTop30ByOrderByCreatedAtDesc();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
