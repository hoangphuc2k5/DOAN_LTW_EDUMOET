package com.stackoverflow.service;

import com.stackoverflow.model.SystemSettings;
import com.stackoverflow.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * System Settings Service - Quản lý cài đặt hệ thống
 */
@Service
@Transactional
public class SystemSettingsService {

    @Autowired
    private SystemSettingsRepository repository;

    public SystemSettings getSettings() {
        return repository.findAll().stream().findFirst()
                .orElseGet(() -> createDefaultSettings());
    }

    public SystemSettings updateSettings(SystemSettings settings) {
        return repository.save(settings);
    }

    private SystemSettings createDefaultSettings() {
        SystemSettings settings = new SystemSettings();
        settings.setSiteName("StackOverflow Clone");
        settings.setSiteDescription("A Q&A Platform");
        settings.setContactEmail("admin@stackoverflow.com");
        settings.setLanguage("en");
        settings.setTheme("light");
        settings.setEnableRegistration(true);
        settings.setEnableComments(true);
        settings.setRequireEmailVerification(false);
        settings.setRequireModeration(false);
        settings.setMaxUploadSize(10L);
        settings.setPointsPerQuestion(10);
        settings.setPointsPerAnswer(15);
        settings.setPointsPerAcceptedAnswer(25);
        
        return repository.save(settings);
    }

    public void setSetting(String key, String value) {
        SystemSettings settings = getSettings();
        
        switch (key) {
            case "siteName" -> settings.setSiteName(value);
            case "siteDescription" -> settings.setSiteDescription(value);
            case "contactEmail" -> settings.setContactEmail(value);
            case "language" -> settings.setLanguage(value);
            case "theme" -> settings.setTheme(value);
        }
        
        repository.save(settings);
    }
}

