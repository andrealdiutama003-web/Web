package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_investments")
data class UserInvestment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageId: Int,
    val packageName: String,
    val accountType: String, // "USER" or "COMPANY"
    val capitalAmount: Double,
    val dailyReturnPct: Double,
    val monthlyReturnPct: Double,
    val yearlyReturnPct: Double,
    val accruedProfit: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val lastAccrualDate: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE"
)
