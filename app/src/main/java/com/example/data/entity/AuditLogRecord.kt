package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actionType: String,
    val adminEmail: String = "Super Admin",
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
