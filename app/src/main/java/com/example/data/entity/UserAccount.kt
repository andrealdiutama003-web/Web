package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String = "",
    val password: String,
    val accountType: String, // "INDIVIDUAL", "COMPANY", "SUPER_ADMIN"
    val companyName: String = "",
    val companyTaxId: String = "", // NIB / NPWP / Tax ID
    val balance: Double = 0.0,
    val cryptoBalanceUsdt: Double = 0.0,
    val bankName: String = "Bank BCA",
    val bankAccountNumber: String = "",
    val bankAccountName: String = "",
    val cryptoWalletAddress: String = "",
    val cryptoNetwork: String = "TRC20",
    val countryCode: String = "ID",
    val isApproved: Boolean = true,
    val kycStatus: String = "UNVERIFIED", // "UNVERIFIED", "PENDING", "VERIFIED", "REJECTED"
    val ktpNumber: String = "",
    val ktpPhotoPath: String = "",
    val facePhotoPath: String = "",
    val isFingerprintEnabled: Boolean = false,
    val isFaceAuthEnabled: Boolean = false,
    val transactionPin: String = "123456", // Default 6-digit transaction PIN
    val preferredCurrency: String = "IDR", // "IDR", "USD", "EUR", "SGD", "JPY", "GBP", "SAR", "CNY", "USDT", "BTC", "ETH", etc.
    val preferredLanguage: String = "ID", // "ID", "EN", "ES", "ZH", "JA", "AR", "DE", "FR", "KO", "RU", "PT", "HI", "VI", "TR"
    val createdAt: Long = System.currentTimeMillis(),
    val referralCode: String = "",
    val referredByCode: String = "",
    val referralBonusesEarned: Double = 0.0,
    val referredUsersCount: Int = 0,
    val requestedAccountTier: String = "INDIVIDUAL", // "INDIVIDUAL", "ENTERPRISE"
    val kycTransactionLimit: Double = 10_000_000.0,
    val wheelDepositBalance: Double = 0.0, // Saldo Deposito Roda Keberuntungan (Terkunci Permanen)
    val lastWheelClaimDate: String = "",   // Tanggal klaim harian terakhir ("yyyy-MM-dd")
    val wheelClaimsToday: Int = 0,         // Jumlah klaim/spins hari ini
    val totalWheelProfitClaimed: Double = 0.0, // Total profit Roda Keberuntungan yang sudah diklaim
    val isEmailVerified: Boolean = false  // Status verifikasi email Firebase Auth
)
