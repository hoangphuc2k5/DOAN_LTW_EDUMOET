package com.stackoverflow.service.common;

import com.stackoverflow.entity.User;
import com.stackoverflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Admin Service - Quản lý người dùng
 */
@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================== QUẢN LÝ NGƯỜI DÙNG ==================

    /**
     * Lấy tất cả người dùng (có phân trang)
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Lấy tất cả người dùng (không phân trang)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Tìm người dùng theo ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Tìm người dùng theo username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Tìm kiếm người dùng theo từ khóa (username hoặc email)
     */
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword, pageable);
    }

    /**
     * Lấy người dùng theo vai trò
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * Khóa tài khoản người dùng
     */
    public void banUser(Long userId, String reason, LocalDateTime bannedUntil) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(true);
        user.setBanReason(reason);
        user.setBannedUntil(bannedUntil);
        
        userRepository.save(user);
    }

    /**
     * Khóa tài khoản vĩnh viễn
     */
    public void banUserPermanently(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(true);
        user.setBanReason(reason);
        user.setBannedUntil(null); // null = permanent ban
        
        userRepository.save(user);
    }

    /**
     * Mở khóa tài khoản
     */
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(false);
        user.setBanReason(null);
        user.setBannedUntil(null);
        
        userRepository.save(user);
    }

    /**
     * Vô hiệu hóa tài khoản (không cho đăng nhập nhưng không xóa dữ liệu)
     */
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Kích hoạt lại tài khoản
     */
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(true);
        userRepository.save(user);
    }

    /**
     * Đổi mật khẩu cho người dùng (Admin reset password)
     */
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Thay đổi vai trò của người dùng
     */
    public void changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(newRole);
        userRepository.save(user);
    }

    /**
     * Xóa người dùng (cẩn thận!)
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Lấy số lượng người dùng theo trạng thái
     */
    public long countActiveUsers() {
        return userRepository.countByIsActive(true);
    }

    public long countBannedUsers() {
        return userRepository.countByIsBanned(true);
    }

    public long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }

    /**
     * Xác minh email cho người dùng
     */
    public void verifyUserEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Cập nhật thông tin người dùng
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}

