package com.stackoverflow.repository;

import com.stackoverflow.entity.Notification;
import com.stackoverflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    long countByIsReadFalse();
    
    // For user deletion
    void deleteByUser(User user);
}
