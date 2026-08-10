package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investment_packages")
data class InvestmentPackage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val accountType: String, // "USER", "COMPANY", "ALL"
    val minCapital: Double,  // Modal Minimal
    val maxCapital: Double,  // Modal Maksimal
    val dailyReturnPct: Double,   // Persen Harian
    val monthlyReturnPct: Double, // Persen Bulanan
    val yearlyReturnPct: Double,  // Persen Tahunan
    val durationDays: Int = 30,
    val isActive: Boolean = true
)
