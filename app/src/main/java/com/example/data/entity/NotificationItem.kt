package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // "DEPOSIT_APPROVED", "DEPOSIT_REJECTED", "WITHDRAWAL_APPROVED", "WITHDRAWAL_REJECTED", "MANUAL_DEPOSIT", "MANUAL_WITHDRAWAL", "INFO"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val referenceId: String = "",
    val amount: Double = 0.0,
    val targetAccountType: String = "ALL" // "USER", "COMPANY", "ALL"
)
