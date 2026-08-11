package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_config")
data class BrandConfig(
    @PrimaryKey val id: Int = 1,
    val appName: String = "InvestPro",
    val companyName: String = "PT Investasi Jaya Mandiri",
    val tagline: String = "Platform Investasi & Manajemen Modal Digital Terpercaya",
    val whatsappNumber: String = "6281234567890",
    val whatsappGreeting: String = "Halo CS Admin, saya ingin berkonsultasi mengenai investasi.",
    val supportEmail: String = "support@investpro.id",
    val supportAddress: String = "Equity Tower Lt. 18, SCBD Jakarta",
    val isLiveChatEnabled: Boolean = true,
    val isDarkMode: Boolean = true,
    val logoUrl: String = "",
    val faviconUrl: String = "",
    val logoSymbol: String = "TRENDING", // TRENDING, SHIELD, DIAMOND, ROCKET, BUSINESS, STAR, GLOBE, EMOJI
    val customEmojiLogo: String = "📈",
    val customFaviconEmoji: String = "🛡️",
    // New Corporate Domain Settings for PT Perusahaan
    val companyDomain: String = "investpro.id",
    val isDomainSslActive: Boolean = true,
    val dnsARecordIp: String = "104.21.90.18",
    val dnsCnameTarget: String = "cname.investpro.id",
    val domainVerificationToken: String = "investpro-verification-hash-8821",
    // Corporate SMTP Email Server Settings
    val smtpHost: String = "smtp.mailgun.org",
    val smtpPort: Int = 587,
    val smtpUsername: String = "postmaster@mg.investpro.id",
    val smtpPassword: String = "secure-smtp-pass-8821",
    val smtpEncryption: String = "TLS", // TLS, SSL, NONE
    val smtpSenderEmail: String = "noreply@investpro.id",
    val smtpSenderName: String = "InvestPro Official",
    val isSmtpActive: Boolean = true,
    // Cloudflare Deployment & Edge Online Hub Configuration
    val cloudflareAccountId: String = "cf-acc-88219472910",
    val cloudflareApiToken: String = "v1.0-cf-token-secure-key-99381",
    val cloudflarePagesDomain: String = "investpro.pages.dev",
    val cloudflareWorkerEndpoint: String = "https://api.investpro.workers.dev",
    val isCloudflareProxyActive: Boolean = true,
    val isCloudflareTunnelActive: Boolean = true,
    val cloudflareTunnelUrl: String = "https://investpro-portal.trycloudflare.com",
    // Cloudflare D1 SQLite Database Integration
    val cloudflareD1DatabaseId: String = "d1-db-88219472-investpro",
    val cloudflareD1DatabaseName: String = "investpro-d1-db",
    val cloudflareD1BindingName: String = "DB",
    val isD1AutoSyncEnabled: Boolean = true,
    val cloudflareD1SecretToken: String = "d1-secret-token-key-investpro",
    // GitHub Repository & Cloudflare Pages CI/CD Auto-Online Integration
    val githubRepoUrl: String = "https://github.com/investpro/investpro-web-portal",
    val githubBranch: String = "main",
    val isGithubCloudflareAutoDeploy: Boolean = true,
    val isAutoOnlineOnDeployEnabled: Boolean = true,
    val lastCloudflareDeployTimestamp: String = "2026-08-11 05:00 UTC (Auto-Deployed via GitHub main)",
    val cloudflareDeployStatus: String = "ONLINE"
)
