package com.example.data.entity

data class CountryInfo(
    val code: String,        // e.g. "ID", "US", "GB", "JP", "DE", "SG", "AE", "BR", "IN", "CN", "CA", "FR", "SA", "KR", "AU", "RU", "MX", "TR", "ZA", "VN", "MY", "NG", "EG", "CH"
    val name: String,        // e.g. "Indonesia", "United States", "United Kingdom"
    val flag: String,        // e.g. "🇮🇩", "🇺🇸", "🇬🇧"
    val currencyCode: String, // e.g. "IDR", "USD", "GBP", "JPY"
    val currencySymbol: String, // e.g. "Rp", "$", "£", "¥"
    val usdExchangeRate: Double, // Amount of country currency per 1 USD
    val defaultLang: String = "EN"
)

data class CryptoAsset(
    val symbol: String,      // e.g. "USDT", "BTC", "ETH", "SOL", "BNB", "TRX", "XRP"
    val name: String,        // e.g. "Tether USD", "Bitcoin", "Ethereum"
    val iconEmoji: String,   // e.g. "💵", "₿", "Ξ", "◎", "🟡", "🔴", "✕"
    val defaultNetwork: String, // e.g. "TRC20", "Bitcoin", "ERC20"
    val availableNetworks: List<String>,
    val defaultUsdPrice: Double
)

object GlobalDataRegistry {

    val WORLD_COUNTRIES = listOf(
        CountryInfo("ID", "Indonesia", "🇮🇩", "IDR", "Rp", 16000.0, "ID"),
        CountryInfo("US", "United States", "🇺🇸", "USD", "$", 1.0, "EN"),
        CountryInfo("GB", "United Kingdom", "🇬🇧", "GBP", "£", 0.78, "EN"),
        CountryInfo("EU", "European Union / Germany", "🇩🇪", "EUR", "€", 0.92, "DE"),
        CountryInfo("JP", "Japan", "🇯🇵", "JPY", "¥", 155.0, "JA"),
        CountryInfo("SG", "Singapore", "🇸🇬", "SGD", "S$", 1.35, "EN"),
        CountryInfo("AE", "United Arab Emirates", "🇦🇪", "AED", "AED", 3.67, "AR"),
        CountryInfo("BR", "Brazil", "🇧🇷", "BRL", "R$", 5.50, "PT"),
        CountryInfo("IN", "India", "🇮🇳", "INR", "₹", 83.5, "HI"),
        CountryInfo("CN", "China", "🇨🇳", "CNY", "¥", 7.25, "ZH"),
        CountryInfo("CA", "Canada", "🇨🇦", "CAD", "C$", 1.37, "EN"),
        CountryInfo("FR", "France", "🇫🇷", "EUR", "€", 0.92, "FR"),
        CountryInfo("SA", "Saudi Arabia", "🇸🇦", "SAR", "SR", 3.75, "AR"),
        CountryInfo("KR", "South Korea", "🇰🇷", "KRW", "₩", 1380.0, "KO"),
        CountryInfo("AU", "Australia", "🇦🇺", "AUD", "A$", 1.50, "EN"),
        CountryInfo("RU", "Russia", "🇷🇺", "RUB", "₽", 90.0, "RU"),
        CountryInfo("MX", "Mexico", "🇲🇽", "MXN", "$", 18.2, "ES"),
        CountryInfo("TR", "Turkey", "🇹🇷", "TRY", "₺", 32.5, "TR"),
        CountryInfo("ZA", "South Africa", "🇿🇦", "ZAR", "R", 18.5, "EN"),
        CountryInfo("VN", "Vietnam", "🇻🇳", "VND", "₫", 25400.0, "VI"),
        CountryInfo("MY", "Malaysia", "🇲🇾", "MYR", "RM", 4.70, "EN"),
        CountryInfo("NG", "Nigeria", "🇳🇬", "NGN", "₦", 1500.0, "EN"),
        CountryInfo("EG", "Egypt", "🇪🇬", "EGP", "E£", 48.0, "AR"),
        CountryInfo("CH", "Switzerland", "🇨🇭", "CHF", "CHF", 0.89, "DE")
    )

    val CRYPTO_ASSETS = listOf(
        CryptoAsset("USDT", "Tether USD", "💵", "TRC20", listOf("TRC20", "BEP20", "ERC20", "Solana"), 1.0),
        CryptoAsset("BTC", "Bitcoin", "₿", "Bitcoin", listOf("Bitcoin (Native SegWit)", "BEP20", "Lightning"), 65000.0),
        CryptoAsset("ETH", "Ethereum", "Ξ", "ERC20", listOf("ERC20", "BEP20", "Arbitrum", "Optimism"), 3500.0),
        CryptoAsset("SOL", "Solana", "◎", "Solana", listOf("Solana Native"), 140.0),
        CryptoAsset("BNB", "Binance Coin", "🟡", "BEP20", listOf("BEP20 (BSC)", "BEP2"), 580.0),
        CryptoAsset("TRX", "TRON", "🔴", "TRC20", listOf("TRC20 Native"), 0.12),
        CryptoAsset("XRP", "Ripple", "✕", "Ripple", listOf("Ripple XRP Ledger"), 0.55)
    )

    val GLOBAL_LANGUAGES = listOf(
        Pair("ID", "Bahasa Indonesia 🇮🇩"),
        Pair("EN", "English (Global) 🇺🇸"),
        Pair("ES", "Español 🇪🇸"),
        Pair("ZH", "中文 (Mandarin) 🇨🇳"),
        Pair("AR", "العربية (Arabic) 🇸🇦"),
        Pair("JA", "日本語 (Japanese) 🇯🇵"),
        Pair("DE", "Deutsch (German) 🇩🇪"),
        Pair("FR", "Français 🇫🇷"),
        Pair("KO", "한국어 (Korean) 🇰🇷"),
        Pair("PT", "Português 🇧🇷"),
        Pair("HI", "हिन्दी (Hindi) 🇮🇳"),
        Pair("RU", "Русский 🇷🇺"),
        Pair("VI", "Tiếng Việt 🇻🇳"),
        Pair("TR", "Türkçe 🇹🇷")
    )

    fun getCountryByCode(code: String): CountryInfo {
        return WORLD_COUNTRIES.find { it.code.equals(code, ignoreCase = true) || it.currencyCode.equals(code, ignoreCase = true) }
            ?: WORLD_COUNTRIES.first()
    }

    fun getCryptoBySymbol(symbol: String): CryptoAsset {
        return CRYPTO_ASSETS.find { it.symbol.equals(symbol, ignoreCase = true) }
            ?: CRYPTO_ASSETS.first()
    }

    // Convert amount from base IDR to target currency code or Crypto
    fun convertFromBaseIdr(amountInIdr: Double, targetCurrency: String, cryptoPriceMap: Map<String, Double> = emptyMap()): Double {
        val usdAmount = amountInIdr / 16000.0
        val cryptoAsset = CRYPTO_ASSETS.find { it.symbol.equals(targetCurrency, ignoreCase = true) }
        if (cryptoAsset != null) {
            val priceInUsd = cryptoPriceMap[targetCurrency] ?: cryptoAsset.defaultUsdPrice
            return if (priceInUsd > 0) usdAmount / priceInUsd else 0.0
        }

        val country = WORLD_COUNTRIES.find { it.currencyCode.equals(targetCurrency, ignoreCase = true) }
        val rate = country?.usdExchangeRate ?: 1.0
        return usdAmount * rate
    }

    fun formatCurrencyAmount(amount: Double, currencyCode: String): String {
        val crypto = CRYPTO_ASSETS.find { it.symbol.equals(currencyCode, ignoreCase = true) }
        if (crypto != null) {
            return "${String.format("%,.4f", amount)} ${crypto.symbol}"
        }
        val country = WORLD_COUNTRIES.find { it.currencyCode.equals(currencyCode, ignoreCase = true) } ?: WORLD_COUNTRIES.first()
        val formatted = String.format("%,.2f", amount)
        return "${country.currencySymbol} $formatted (${country.currencyCode})"
    }
}
