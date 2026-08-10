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
    val isSmtpActive: Boolean = true
)
