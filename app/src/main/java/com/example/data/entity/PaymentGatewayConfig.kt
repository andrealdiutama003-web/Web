package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_gateway_config")
data class PaymentGatewayConfig(
    @PrimaryKey val id: Int = 1,
    val providerName: String = "Midtrans / Xendit Payment Gateway",
    val apiKey: String = "SB-Mid-server-x891K2mL9A0zP",
    val merchantId: String = "M10928374",
    val webhookSecret: String = "whsec_89123891723912837",
    val autoProcessDeposit: Boolean = true,
    val autoProcessWithdrawal: Boolean = true,
    val sandboxMode: Boolean = true
)
