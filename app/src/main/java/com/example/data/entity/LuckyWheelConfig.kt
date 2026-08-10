package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lucky_wheel_config")
data class LuckyWheelConfig(
    @PrimaryKey val id: Int = 1,
    val isWheelEnabled: Boolean = true,
    val dailyClaimQuota: Int = 3,           // Kuota Klaim Harian per Pengguna/Perusahaan
    val taxPct: Double = 5.0,                // Pajak Keuntungan Harian (%)
    
    // Profit rates for Individual Accounts (% harian berdasarkan range/tier deposit Roda Keberuntungan)
    val individualTier1Min: Double = 1.0,
    val individualTier1Max: Double = 10_000_000.0,
    val individualTier1ProfitPct: Double = 1.0,

    val individualTier2Min: Double = 10_000_001.0,
    val individualTier2Max: Double = 50_000_000.0,
    val individualTier2ProfitPct: Double = 1.8,

    val individualTier3Min: Double = 50_000_001.0,
    val individualTier3Max: Double = 1_000_000_000.0,
    val individualTier3ProfitPct: Double = 2.5,

    // Profit rates for Corporate / Company Accounts (% harian berdasarkan range/tier deposit Roda Keberuntungan)
    val companyTier1Min: Double = 1.0,
    val companyTier1Max: Double = 50_000_000.0,
    val companyTier1ProfitPct: Double = 1.8,

    val companyTier2Min: Double = 50_000_001.0,
    val companyTier2Max: Double = 250_000_000.0,
    val companyTier2ProfitPct: Double = 3.0,

    val companyTier3Min: Double = 250_000_001.0,
    val companyTier3Max: Double = 10_000_000_000.0,
    val companyTier3ProfitPct: Double = 4.5
)
