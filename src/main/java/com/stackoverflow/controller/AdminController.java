package com.stackoverflow.controller;

import com.stackoverflow.model.User;
import com.stackoverflow.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Admin Controller - Quản lý người dùng
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Trang tổng hợp tất cả tính năng admin
     */
    @GetMapping("/all-features")
    public String allFeatures(Model model) {
        return "admin/all-features";
    }

    /**
     * Trang dashboard admin
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard - Stack Overflow Clone");
        model.addAttribute("totalUsers", adminService.getAllUsers().size());
        model.addAttribute("activeUsers", adminService.countActiveUsers());
        model.addAttribute("bannedUsers", adminService.countBannedUsers());
        model.addAttribute("adminCount", adminService.countUsersByRole("ADMIN"));
        model.addAttribute("managerCount", adminService.countUsersByRole("MANAGER"));
        model.addAttribute("userCount", adminService.countUsersByRole("USER"));
        
        return "admin/dashboard";
    }

    /**
     * Danh sách người dùng
     */
    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users;
        
        if (search != null && !search.isEmpty()) {
            users = adminService.searchUsers(search, pageable);
            model.addAttribute("search", search);
        } else {
            users = adminService.getAllUsers(pageable);
        }
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalItems", users.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("pageTitle", "User Management - Admin");
        
        return "admin/users/list";
    }

    /**
     * Xem chi tiết người dùng
     */
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "User Details - " + user.getUsername());
        
        return "admin/users/view";
    }

    /**
     * Form chỉnh sửa người dùng
     */
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit User - " + user.getUsername());
        
        return "admin/users/edit";
    }

    /**
     * Xử lý cập nhật thông tin người dùng
     */
    @PostMapping("/users/{id}/edit")
    public String editUser(
            @PathVariable Long id,
            @RequestParam String location,
            @RequestParam String about,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = adminService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setLocation(location);
            user.setAbout(about);
            adminService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "User information updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Khóa tài khoản tạm thời
     */
    @PostMapping("/users/{id}/ban")
    public String banUser(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam(required = false) Integer days,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (days != null && days > 0) {
                LocalDateTime bannedUntil = LocalDateTime.now().plusDays(days);
                adminService.banUser(id, reason, bannedUntil);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "User banned for " + days + " days successfully!");
            } else {
                adminService.banUserPermanently(id, reason);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "User banned permanently!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Mở khóa tài khoản
     */
    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.unbanUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User unbanned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Vô hiệu hóa tài khoản
     */
    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Kích hoạt tài khoản
     */
    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.activateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Đổi mật khẩu người dùng
     */
    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            adminService.resetUserPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Password reset successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Thay đổi vai trò người dùng
     */
    @PostMapping("/users/{id}/change-role")
    public String changeRole(
            @PathVariable Long id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        
        try {
            adminService.changeUserRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User role changed to " + role + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Xóa người dùng
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User deleted successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error: " + e.getMessage());
            return "redirect:/admin/users/" + id;
        }
    }
}

