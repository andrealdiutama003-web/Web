package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "DEPOSIT", "WITHDRAWAL", "INVESTMENT", "PROFIT_CLAIM"
    val amount: Double,
    val paymentMethod: String,
    val referenceId: String,
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val countryCode: String = "ID",
    val currencyCode: String = "IDR",
    val cryptoSymbol: String = "",
    val cryptoAmount: Double = 0.0,
    val txHash: String = "",
    val network: String = "",
    val destinationWallet: String = ""
)
