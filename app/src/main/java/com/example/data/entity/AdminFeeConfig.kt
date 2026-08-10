package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_fee_config")
data class AdminFeeConfig(
    @PrimaryKey val id: Int = 1,
    // Fee Settings for Akun Pengguna (Personal)
    val userWithdrawalFee: Double = 2500.0,    // Flat withdrawal fee Rp 2.500
    val userAdminFeePct: Double = 1.0,         // Biaya admin % (1%)
    val userTaxFeePct: Double = 5.0,           // Potongan Pajak PPh % (5%)

    // Fee Settings for Akun Perusahaan (Company)
    val companyWithdrawalFee: Double = 10000.0,// Flat withdrawal fee Rp 10.000
    val companyAdminFeePct: Double = 0.5,      // Biaya admin % (0.5%)
    val companyTaxFeePct: Double = 2.5,         // Potongan Pajak PPh % (2.5%)

    // Referral Bonus Settings for Individual
    val individualReferralReferrerBonus: Double = 150000.0,
    val individualReferralReferredBonus: Double = 50000.0,

    // Referral Bonus Settings for Company
    val companyReferralReferrerBonus: Double = 300000.0,
    val companyReferralReferredBonus: Double = 100000.0,

    // Tax rate on Referral Bonuses (%)
    val referralTaxPct: Double = 10.0,

    // Profit Sharing Commission from active investments (%)
    val referralDailyCommissionPct: Double = 5.0,
    val referralMonthlyCommissionPct: Double = 3.0,
    val referralYearlyCommissionPct: Double = 2.0
)
