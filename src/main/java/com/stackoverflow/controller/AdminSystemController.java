package com.stackoverflow.controller;

import com.stackoverflow.model.SystemSettings;
import com.stackoverflow.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin System Controller - Quản trị hệ thống
 */
@Controller
@RequestMapping("/admin/system")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSystemController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * Cài đặt hệ thống
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        SystemSettings settings = systemSettingsService.getSettings();
        model.addAttribute("settings", settings);
        model.addAttribute("pageTitle", "System Settings - Admin");
        return "admin/system/settings";
    }

    /**
     * Cập nhật cài đặt
     */
    @PostMapping("/settings/update")
    public String updateSettings(
            @ModelAttribute SystemSettings settings,
            RedirectAttributes redirectAttributes) {
        
        try {
            systemSettingsService.updateSettings(settings);
            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Settings updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "❌ Error: " + e.getMessage());
        }
        
        return "redirect:/admin/system/settings";
    }

    /**
     * Quản lý database - placeholder
     */
    @GetMapping("/database")
    public String database(Model model) {
        model.addAttribute("pageTitle", "Database Management - Admin");
        return "admin/system/database";
    }

    /**
     * Phân quyền - placeholder
     */
    @GetMapping("/permissions")
    public String permissions(Model model) {
        model.addAttribute("pageTitle", "Permissions Management - Admin");
        return "admin/system/permissions";
    }

    /**
     * Quản lý vai trò - placeholder
     */
    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("pageTitle", "Roles Management - Admin");
        return "admin/system/roles";
    }
}

