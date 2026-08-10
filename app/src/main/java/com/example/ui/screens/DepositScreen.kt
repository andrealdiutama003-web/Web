package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GlobalDataRegistry
import com.example.data.entity.UserProfile
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute

data class PaymentMethodItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String = "OTOMATIS",
    val isAuto: Boolean = true,
    val isCrypto: Boolean = false,
    val accountNo: String = ""
)

@Composable
fun DepositScreen(
    viewModel: MainViewModel,
    profile: UserProfile?
) {
    val cryptoConfig by viewModel.cryptoWalletConfig.collectAsState()
    val currentCurrency by viewModel.currentCurrency.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val currentCountry = remember(profile?.countryCode, currentCurrency) {
        GlobalDataRegistry.WORLD_COUNTRIES.find {
            it.code.equals(profile?.countryCode, ignoreCase = true) || it.currencyCode.equals(currentCurrency, ignoreCase = true)
        } ?: GlobalDataRegistry.WORLD_COUNTRIES.first()
    }

    var depositAmountInput by remember { mutableStateOf("1000") }
    var selectedMethod by remember { mutableStateOf("CRYPTO_USDT") }
    var selectedCategoryTab by remember { mutableStateOf("CRYPTO") } // "ALL", "AUTO", "MANUAL", "CRYPTO"
    
    // Crypto state
    var selectedCryptoSymbol by remember { mutableStateOf("USDT") }
    var selectedNetwork by remember { mutableStateOf("TRC20") }
    var txHashInput by remember { mutableStateOf("") }
    var manualProofInput by remember { mutableStateOf("") }
    var showBiometricAuthDialog by remember { mutableStateOf(false) }

    val isCompany = profile?.accountType == "COMPANY"
    val accountTypeLabel = if (isCompany) "Akun Perusahaan (Corporate)" else "Akun Pengguna (Individual)"

    val activeCryptoAsset = remember(selectedCryptoSymbol) {
        GlobalDataRegistry.getCryptoBySymbol(selectedCryptoSymbol)
    }

    // Resolve company wallet address for selected crypto asset & network
    val companyWalletAddress = remember(selectedCryptoSymbol, selectedNetwork, cryptoConfig) {
        val cfg = cryptoConfig
        when (selectedCryptoSymbol) {
            "USDT" -> when (selectedNetwork) {
                "BEP20" -> cfg?.usdtBep20Address ?: "0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
                "ERC20" -> cfg?.usdtErc20Address ?: "0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
                "Solana" -> cfg?.usdtSolAddress ?: "7xKXtg2CW87d97TXJSDp3A4G008xXm1qRtS9Y"
                else -> cfg?.usdtTrc20Address ?: "TYD7sK9mL2xP8qW5zR1vA3cE4fG6hJ8kL0"
            }
            "BTC" -> cfg?.btcAddress ?: "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh"
            "ETH" -> cfg?.ethAddress ?: "0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
            "SOL" -> cfg?.solAddress ?: "7xKXtg2CW87d97TXJSDp3A4G008xXm1qRtS9Y"
            "BNB" -> cfg?.bnbAddress ?: "0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
            "TRX" -> cfg?.trxAddress ?: "TYD7sK9mL2xP8qW5zR1vA3cE4fG6hJ8kL0"
            "XRP" -> cfg?.xrpAddress ?: "rEb8TK3gG22AuRBy759pP325p7A1hE8231"
            else -> cfg?.usdtTrc20Address ?: "TYD7sK9mL2xP8qW5zR1vA3cE4fG6hJ8kL0"
        }
    }

    val cryptoPriceMap = remember(cryptoConfig) {
        mapOf(
            "USDT" to (cryptoConfig?.usdtUsdPrice ?: 1.0),
            "BTC" to (cryptoConfig?.btcUsdPrice ?: 65000.0),
            "ETH" to (cryptoConfig?.ethUsdPrice ?: 3500.0),
            "SOL" to (cryptoConfig?.solUsdPrice ?: 140.0),
            "BNB" to (cryptoConfig?.bnbUsdPrice ?: 580.0),
            "TRX" to (cryptoConfig?.trxUsdPrice ?: 0.12),
            "XRP" to (cryptoConfig?.xrpUsdPrice ?: 0.55)
        )
    }

    val inputAmountVal = depositAmountInput.toDoubleOrNull() ?: 0.0
    val isDepositAmountValid = inputAmountVal > 0.0

    // Calculate equivalent crypto amount
    val equivalentCrypto = remember(inputAmountVal, selectedCryptoSymbol, currentCountry, cryptoPriceMap) {
        val usdVal = if (currentCountry.currencyCode == "USD") inputAmountVal else inputAmountVal / currentCountry.usdExchangeRate
        val cryptoPrice = cryptoPriceMap[selectedCryptoSymbol] ?: 1.0
        if (cryptoPrice > 0) usdVal / cryptoPrice else 0.0
    }

    val allMethods = listOf(
        // Crypto Manual Global
        PaymentMethodItem("CRYPTO_USDT", "USDT (Tether USD) Manual Global", "Deposit via TRC20, BEP20, ERC20, Solana Network", Icons.Default.CurrencyExchange, "CRYPTO GLOBAL", false, true, companyWalletAddress),
        PaymentMethodItem("CRYPTO_BTC", "Bitcoin (BTC) Manual Global", "Direct Blockchain Bitcoin Wallet Transfer", Icons.Default.CurrencyExchange, "CRYPTO GLOBAL", false, true, cryptoConfig?.btcAddress ?: ""),
        PaymentMethodItem("CRYPTO_ETH", "Ethereum (ETH) Manual Global", "Direct Ethereum ERC20 Wallet Deposit", Icons.Default.CurrencyExchange, "CRYPTO GLOBAL", false, true, cryptoConfig?.ethAddress ?: ""),
        PaymentMethodItem("CRYPTO_SOL", "Solana (SOL) Manual Global", "Instant Low Fee Solana Network Wallet", Icons.Default.CurrencyExchange, "CRYPTO GLOBAL", false, true, cryptoConfig?.solAddress ?: ""),
        
        // Payment Gateway Otomatis
        PaymentMethodItem("QRIS_PG", "QRIS Global Payment Gateway", "Instant Auto Webhook Callback All Bank & E-Wallet", Icons.Default.QrCode, "OTOMATIS 24/7", true, false),
        PaymentMethodItem("GLOBAL_CARD", "Credit / Debit Card (Visa / Mastercard)", "Global Instant Card Gateway Processing", Icons.Default.AccountBalance, "OTOMATIS", true, false),
        PaymentMethodItem("BCA_VA", "BCA / Mandiri Virtual Account API", "Transfer Otomatis tanpa upload bukti transfer", Icons.Default.AccountBalance, "OTOMATIS", true, false),
        
        // Deposit Manual Direct
        PaymentMethodItem("MANUAL_BANK", "Transfer Bank Local Manual (${currentCountry.name})", "Transfer ke Rekening Perusahaan & Verifikasi Admin", Icons.Default.AccountBalance, "MANUAL LOCAL", false, false, "Bank Rekening Perusahaan - ${currentCountry.currencyCode}"),
        PaymentMethodItem("MANUAL_CASH", "Setoran Tunai / Kasir Global", "Injeksi Saldo Langsung di Loket Kantor Admin", Icons.Default.Payments, "KASIR MANUAL", false, false, "Setoran Langsung Loket Super Admin")
    )

    val displayedMethods = when (selectedCategoryTab) {
        "CRYPTO" -> allMethods.filter { it.isCrypto }
        "AUTO" -> allMethods.filter { it.isAuto }
        "MANUAL" -> allMethods.filter { !it.isAuto && !it.isCrypto }
        else -> allMethods
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TOP BAR WITH COUNTRY FLAG & CURRENCY ---
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Deposit Saldo Global (${currentCountry.flag} ${currentCountry.name})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCompany) AccentGold.copy(alpha = 0.2f) else PrimaryEmerald.copy(alpha = 0.2f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "$accountTypeLabel • ${currentCountry.currencyCode} (${currentCountry.currencySymbol})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompany) AccentGold else PrimaryEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- AMOUNT INPUT & CURRENCY PRESETS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompany) AccentGold else PrimaryEmerald)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Saldo Tersedia: ${currentCountry.currencySymbol} ${String.format("%,.2f", profile?.balance ?: 0.0)} (${currentCountry.currencyCode})",
                        fontSize = 12.sp,
                        color = if (isCompany) AccentGold else PrimaryEmerald,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Nominal Deposit (${currentCountry.currencyCode}):", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = depositAmountInput,
                        onValueChange = { depositAmountInput = it.filter { char -> char.isDigit() || char == '.' } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deposit_amount_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isCompany) AccentGold else PrimaryEmerald,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkCardSurface,
                            unfocusedContainerColor = DarkCardSurface
                        ),
                        leadingIcon = {
                            Text(text = currentCountry.currencySymbol, fontWeight = FontWeight.Bold, color = if (isCompany) AccentGold else PrimaryEmerald)
                        }
                    )

                    if (depositAmountInput.isNotEmpty() && !isDepositAmountValid) {
                        Text(
                            text = "❌ Nominal deposit harus lebih besar dari 0",
                            fontSize = 11.sp,
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset nominal buttons
                    val presets = if (currentCountry.currencyCode == "IDR") {
                        listOf("100000", "500000", "2500000", "10000000", "50000000")
                    } else {
                        listOf("100", "500", "1000", "5000", "10000")
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presets) { preset ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (depositAmountInput == preset) PrimaryEmerald else DarkCardSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable { depositAmountInput = preset }
                            ) {
                                Text(
                                    text = "${currentCountry.currencySymbol} ${String.format("%,.0f", preset.toDouble())}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (depositAmountInput == preset) Color.Black else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- CATEGORY TABS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Kategori Metode Deposit Global:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CategoryTabButton(
                            label = "🌐 CRYPTO MANUAL",
                            isSelected = selectedCategoryTab == "CRYPTO",
                            activeColor = AccentGold,
                            onClick = {
                                selectedCategoryTab = "CRYPTO"
                                selectedMethod = "CRYPTO_USDT"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryTabButton(
                            label = "⚡ GATEWAY",
                            isSelected = selectedCategoryTab == "AUTO",
                            activeColor = PrimaryEmerald,
                            onClick = {
                                selectedCategoryTab = "AUTO"
                                selectedMethod = "QRIS_PG"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryTabButton(
                            label = "🏦 BANK LOCAL",
                            isSelected = selectedCategoryTab == "MANUAL",
                            activeColor = TextPrimary,
                            onClick = {
                                selectedCategoryTab = "MANUAL"
                                selectedMethod = "MANUAL_BANK"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- CRYPTO MANUAL SPECIFIC CARD (IF CRYPTO CATEGORY SELECTED) ---
        if (selectedCategoryTab == "CRYPTO") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentGold)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CurrencyExchange, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Layanan Deposit Crypto Manual Global",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "1. Pilih Aset Crypto:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Select Crypto Asset Pills
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(GlobalDataRegistry.CRYPTO_ASSETS) { asset ->
                                val isSelected = selectedCryptoSymbol == asset.symbol
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AccentGold else DarkCardSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentGold else DarkCardBorder),
                                    modifier = Modifier.clickable {
                                        selectedCryptoSymbol = asset.symbol
                                        selectedNetwork = asset.defaultNetwork
                                        selectedMethod = "CRYPTO_${asset.symbol}"
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = asset.iconEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = asset.symbol,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(text = "2. Pilih Jaringan (Network):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Select Network Pills
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeCryptoAsset.availableNetworks.forEach { net ->
                                val isNetSelected = selectedNetwork == net
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isNetSelected) PrimaryEmerald else DarkCardSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier.clickable { selectedNetwork = net }
                                ) {
                                    Text(
                                        text = net,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNetSelected) Color.Black else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Wallet Address Card & Copy
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Alamat Deposit Wallet Perusahaan ($selectedCryptoSymbol - $selectedNetwork):",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(companyWalletAddress))
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AccentGold, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = companyWalletAddress,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Estimasi Payout Crypto:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${String.format("%,.6f", equivalentCrypto)} $selectedCryptoSymbol",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(text = "3. Masukkan Hash Transaksi Blockchain (TXID):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = txHashInput,
                            onValueChange = { txHashInput = it },
                            placeholder = { Text("Contoh: 0x891a... atau TXID TRC20...", fontSize = 11.sp, color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("crypto_txhash_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedContainerColor = DarkCardSurface,
                                unfocusedContainerColor = DarkCardSurface
                            )
                        )
                    }
                }
            }
        }

        // --- LIST OF DISPLAYED METHODS ---
        item {
            Text(text = "Pilih Saluran Pembayaran:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        }

        items(displayedMethods) { method ->
            val isSelected = selectedMethod == method.id
            val cardBorderColor = if (isSelected) (if (method.isCrypto) AccentGold else PrimaryEmerald) else DarkCardBorder

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMethod = method.id },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) DarkCardSurface else DarkSurface
                ),
                border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, cardBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(cardBorderColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = method.icon,
                                contentDescription = null,
                                tint = cardBorderColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = method.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = method.subtitle,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(cardBorderColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = method.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = cardBorderColor
                        )
                    }
                }
            }
        }

        // --- SUBMIT DEPOSIT BUTTON ---
        item {
            val isCryptoSelected = selectedCategoryTab == "CRYPTO" || selectedMethod.startsWith("CRYPTO_")

            Button(
                onClick = {
                    if (isDepositAmountValid) {
                        showBiometricAuthDialog = true
                    }
                },
                enabled = isDepositAmountValid,
                colors = ButtonDefaults.buttonColors(containerColor = if (isCryptoSelected) AccentGold else PrimaryEmerald),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_deposit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCryptoSelected) {
                        "Kirim Deposit Crypto ${String.format("%,.4f %s", equivalentCrypto, selectedCryptoSymbol)}"
                    } else {
                        "Konfirmasi Deposit ${currentCountry.currencySymbol} ${String.format("%,.2f", inputAmountVal)}"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }

    if (showBiometricAuthDialog) {
        val isCryptoSelected = selectedCategoryTab == "CRYPTO" || selectedMethod.startsWith("CRYPTO_")
        com.example.ui.components.HighSecurityBiometricVerificationDialog(
            transactionTitle = "Otorisasi Biometrik Deposit Saldo Global",
            transactionDetails = if (isCryptoSelected) {
                "Deposit Crypto: $selectedCryptoSymbol ($selectedNetwork) senilai ${currentCountry.currencySymbol} ${String.format("%,.2f", inputAmountVal)} (${String.format("%,.6f", equivalentCrypto)} $selectedCryptoSymbol) dengan TX Hash: $txHashInput"
            } else {
                "Deposit Saldo: ${currentCountry.currencySymbol} ${String.format("%,.2f", inputAmountVal)} (${currentCountry.currencyCode})"
            },
            userPin = profile?.transactionPin ?: "123456",
            onVerificationSuccess = {
                showBiometricAuthDialog = false
                if (isCryptoSelected) {
                    viewModel.submitCryptoDeposit(
                        amount = inputAmountVal,
                        cryptoSymbol = selectedCryptoSymbol,
                        network = selectedNetwork,
                        txHash = txHashInput,
                        note = manualProofInput
                    )
                } else {
                    viewModel.depositSaldo(
                        amount = inputAmountVal,
                        paymentMethod = selectedMethod,
                        isAutoGateway = true,
                        noteProof = manualProofInput
                    )
                }
            },
            onDismiss = { showBiometricAuthDialog = false }
        )
    }
}

@Composable
fun CategoryTabButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) activeColor else DarkCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) activeColor else DarkCardBorder),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else TextSecondary
            )
        }
    }
}
