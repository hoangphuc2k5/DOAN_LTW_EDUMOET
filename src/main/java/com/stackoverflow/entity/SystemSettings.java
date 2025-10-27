package com.stackoverflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String siteName = "StackOverflow Clone";

    @Column(columnDefinition = "TEXT")
    private String siteDescription = "A Q&A platform for developers";

    @Column(nullable = false)
    private String contactEmail = "admin@stackoverflow.com";

    @Column(nullable = false)
    private String language = "en";

    @Column(nullable = false)
    private String theme = "light";

    @Column(nullable = false)
    private Boolean enableRegistration = true;

    @Column(nullable = false)
    private Boolean enableComments = true;

    @Column(nullable = false)
    private Boolean requireEmailVerification = false;

    @Column(nullable = false)
    private Boolean requireModeration = false;

    @Column(nullable = false)
    private Long maxUploadSize = 10L; // MB

    @Column(nullable = false)
    private Integer pointsPerQuestion = 10;

    @Column(nullable = false)
    private Integer pointsPerAnswer = 15;

    @Column(nullable = false)
    private Integer pointsPerAcceptedAnswer = 25;

    // Logo and Branding
    private String logoPath;
    
    private String faviconPath;

    // Theme Colors (allow null for existing databases)
    @Column(columnDefinition = "VARCHAR(255) DEFAULT '#0066cc'")
    private String primaryColor = "#0066cc";

    @Column(columnDefinition = "VARCHAR(255) DEFAULT '#28a745'")
    private String secondaryColor = "#28a745";

    @Column(columnDefinition = "VARCHAR(255) DEFAULT '#ffc107'")
    private String accentColor = "#ffc107";

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}

