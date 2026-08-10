package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crypto_wallet_config")
data class CryptoWalletConfig(
    @PrimaryKey val id: Int = 1,
    val usdtTrc20Address: String = "TYD7sK9mL2xP8qW5zR1vA3cE4fG6hJ8kL0",
    val usdtBep20Address: String = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
    val usdtErc20Address: String = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
    val usdtSolAddress: String = "7xKXtg2CW87d97TXJSDp3A4G008xXm1qRtS9Y",
    val btcAddress: String = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
    val ethAddress: String = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
    val solAddress: String = "7xKXtg2CW87d97TXJSDp3A4G008xXm1qRtS9Y",
    val bnbAddress: String = "0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
    val trxAddress: String = "TYD7sK9mL2xP8qW5zR1vA3cE4fG6hJ8kL0",
    val xrpAddress: String = "rEb8TK3gG22AuRBy759pP325p7A1hE8231",
    val xrpDestinationTag: String = "1092837",
    val isCryptoDepositEnabled: Boolean = true,
    val isCryptoWithdrawalEnabled: Boolean = true,
    val usdtUsdPrice: Double = 1.0,
    val btcUsdPrice: Double = 65000.0,
    val ethUsdPrice: Double = 3500.0,
    val solUsdPrice: Double = 140.0,
    val bnbUsdPrice: Double = 580.0,
    val trxUsdPrice: Double = 0.12,
    val xrpUsdPrice: Double = 0.55
)
