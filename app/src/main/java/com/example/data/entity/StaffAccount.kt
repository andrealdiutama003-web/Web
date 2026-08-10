package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff_accounts")
data class StaffAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val staffCode: String, // e.g. "STF-001"
    val fullName: String,
    val email: String,
    val phone: String = "",
    val passwordPin: String, // PIN/Password for staff login
    val rolePosition: String, // e.g. "Customer Service Admin", "Finance Verifier", "Operations Manager", "Global Crypto Specialist"
    val assignedCountryCode: String = "ID", // "ID", "US", "GB", "ALL"
    val assignedBranchRegion: String = "Jakarta Global HQ",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
