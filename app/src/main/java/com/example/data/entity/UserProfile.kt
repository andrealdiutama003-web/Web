package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Ahmad Pratama",
    val email: String = "investor@investpro.id",
    val accountType: String = "USER", // "USER" (Pengguna) or "COMPANY" (Perusahaan)
    val companyName: String = "PT Investasi Jaya Mandiri",
    val balance: Double = 25_000_000.0,
    val cryptoBalanceUsdt: Double = 1500.0,
    val bankName: String = "Bank BCA",
    val bankAccountNumber: String = "8830192837",
    val bankAccountName: String = "Ahmad Pratama",
    val cryptoWalletAddress: String = "TYD7sK9mL2xP8qW5zR1vA3cE4fG6hJ8kL0",
    val cryptoNetwork: String = "TRC20",
    val countryCode: String = "ID",
    val kycStatus: String = "UNVERIFIED",
    val ktpNumber: String = "",
    val isFingerprintEnabled: Boolean = false,
    val isFaceAuthEnabled: Boolean = false,
    val transactionPin: String = "123456",
    val preferredCurrency: String = "IDR",
    val preferredLanguage: String = "ID",
    val referralCode: String = "",
    val referredByCode: String = "",
    val referralBonusesEarned: Double = 0.0,
    val referredUsersCount: Int = 0,
    val requestedAccountTier: String = "INDIVIDUAL", // "INDIVIDUAL", "ENTERPRISE"
    val kycTransactionLimit: Double = 10_000_000.0,
    val isEmailVerified: Boolean = false
)
