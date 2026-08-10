package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AdminFeeConfig
import com.example.data.entity.GlobalDataRegistry
import com.example.data.entity.UserProfile
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun WithdrawalScreen(
    viewModel: MainViewModel,
    profile: UserProfile?
) {
    val feeConfig by viewModel.adminFeeConfig.collectAsState()
    val cryptoConfig by viewModel.cryptoWalletConfig.collectAsState()
    val currentCurrency by viewModel.currentCurrency.collectAsState()

    val currentCountry = remember(profile?.countryCode, currentCurrency) {
        GlobalDataRegistry.WORLD_COUNTRIES.find {
            it.code.equals(profile?.countryCode, ignoreCase = true) || it.currencyCode.equals(currentCurrency, ignoreCase = true)
        } ?: GlobalDataRegistry.WORLD_COUNTRIES.first()
    }

    var withdrawalTypeTab by remember { mutableStateOf("CRYPTO") } // "CRYPTO" or "BANK"
    var withdrawAmountInput by remember { mutableStateOf("500") }

    // Bank withdrawal fields
    var bankNameInput by remember { mutableStateOf(profile?.bankName ?: "Bank BCA") }
    var accountNumberInput by remember { mutableStateOf(profile?.bankAccountNumber ?: "8830192837") }
    var accountNameInput by remember { mutableStateOf(profile?.bankAccountName ?: "Ahmad Pratama") }

    // Crypto withdrawal fields
    var selectedCryptoSymbol by remember { mutableStateOf("USDT") }
    var selectedNetwork by remember { mutableStateOf("TRC20") }
    var destinationWalletInput by remember { mutableStateOf(profile?.cryptoWalletAddress ?: "") }
    var showBiometricAuthDialog by remember { mutableStateOf(false) }

    val isCompany = profile?.accountType == "COMPANY"
    val accountTypeLabel = if (isCompany) "Akun Perusahaan (Corporate)" else "Akun Pengguna (Individual)"

    val activeCryptoAsset = remember(selectedCryptoSymbol) {
        GlobalDataRegistry.getCryptoBySymbol(selectedCryptoSymbol)
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

    val inputAmountVal = withdrawAmountInput.toDoubleOrNull() ?: 0.0

    // Calculate equivalent crypto amount
    val equivalentCrypto = remember(inputAmountVal, selectedCryptoSymbol, currentCountry, cryptoPriceMap) {
        val usdVal = if (currentCountry.currencyCode == "USD") inputAmountVal else inputAmountVal / currentCountry.usdExchangeRate
        val cryptoPrice = cryptoPriceMap[selectedCryptoSymbol] ?: 1.0
        if (cryptoPrice > 0) usdVal / cryptoPrice else 0.0
    }

    val userBalance = profile?.balance ?: 0.0
    val isAmountValid = inputAmountVal > 0 && inputAmountVal <= userBalance

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TOP BAR ---
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Penarikan Saldo Global (${currentCountry.flag} ${currentCountry.name})",
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

        // --- SALDO & NOMINAL INPUT CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompany) AccentGold else PrimaryEmerald)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Saldo Dompet Tersedia Penarikan: ${currentCountry.currencySymbol} ${String.format("%,.2f", userBalance)} (${currentCountry.currencyCode})",
                        fontSize = 12.sp,
                        color = if (isCompany) AccentGold else PrimaryEmerald,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Nominal Penarikan (${currentCountry.currencyCode}):", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = { withdrawAmountInput = it.filter { char -> char.isDigit() || char == '.' } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
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

                    if (withdrawAmountInput.isNotEmpty() && inputAmountVal <= 0.0) {
                        Text(
                            text = "❌ Nominal penarikan harus lebih besar dari 0",
                            fontSize = 11.sp,
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (inputAmountVal > userBalance) {
                        Text(
                            text = "❌ Nominal penarikan melebihi saldo dompet tersedia!",
                            fontSize = 11.sp,
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // --- WITHDRAWAL METHOD TYPE TAB ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Pilih Metode Tujuan Penarikan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryTabButton(
                            label = "🌐 CRYPTO WALLET (GLOBAL)",
                            isSelected = withdrawalTypeTab == "CRYPTO",
                            activeColor = AccentGold,
                            onClick = { withdrawalTypeTab = "CRYPTO" },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryTabButton(
                            label = "🏦 REKENING BANK LOCAL",
                            isSelected = withdrawalTypeTab == "BANK",
                            activeColor = PrimaryEmerald,
                            onClick = { withdrawalTypeTab = "BANK" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- CRYPTO WITHDRAWAL DETAILS CARD ---
        if (withdrawalTypeTab == "CRYPTO") {
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
                                text = "Penarikan ke Wallet Crypto Global",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "1. Pilih Crypto Asset:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "2. Pilih Jaringan (Network):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

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

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(text = "3. Alamat Wallet Crypto Tujuan ($selectedCryptoSymbol - $selectedNetwork):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = destinationWalletInput,
                            onValueChange = { destinationWalletInput = it },
                            placeholder = { Text("Paste Alamat Wallet Crypto Tujuan...", fontSize = 11.sp, color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("crypto_destination_wallet_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedContainerColor = DarkCardSurface,
                                unfocusedContainerColor = DarkCardSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCardSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Jumlah Payout Crypto:", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        text = "${String.format("%,.6f", equivalentCrypto)} $selectedCryptoSymbol",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryEmerald
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Biaya Gas Blockchain:", fontSize = 11.sp, color = TextSecondary)
                                    Text("GRATIS / DICOVER PLATFORM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- LOCAL BANK WITHDRAWAL CARD ---
        if (withdrawalTypeTab == "BANK") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Detail Rekening Bank Tujuan (${currentCountry.name}):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Nama Bank / Lembaga Keuangan:", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = bankNameInput,
                            onValueChange = { bankNameInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Nomor Rekening / Account Number:", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = accountNumberInput,
                            onValueChange = { accountNumberInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Nama Pemilik Rekening / Account Holder:", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = accountNameInput,
                            onValueChange = { accountNameInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )
                    }
                }
            }
        }

        // --- SUBMIT WITHDRAWAL BUTTON ---
        item {
            Button(
                onClick = {
                    if (isAmountValid) {
                        showBiometricAuthDialog = true
                    }
                },
                enabled = isAmountValid,
                colors = ButtonDefaults.buttonColors(containerColor = if (withdrawalTypeTab == "CRYPTO") AccentGold else PrimaryEmerald),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_withdrawal_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (withdrawalTypeTab == "CRYPTO") {
                        "Otorisasi Penarikan ${String.format("%,.4f %s", equivalentCrypto, selectedCryptoSymbol)}"
                    } else {
                        "Konfirmasi Penarikan ${currentCountry.currencySymbol} ${String.format("%,.2f", inputAmountVal)}"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }

    if (showBiometricAuthDialog) {
        com.example.ui.components.HighSecurityBiometricVerificationDialog(
            transactionTitle = "Otentikasi Otorisasi Penarikan Saldo",
            transactionDetails = if (withdrawalTypeTab == "CRYPTO") {
                "Penarikan Crypto: $selectedCryptoSymbol ($selectedNetwork) sebesar ${String.format("%,.6f", equivalentCrypto)} ke Wallet: $destinationWalletInput"
            } else {
                "Penarikan Bank: ${currentCountry.currencySymbol} ${String.format("%,.2f", inputAmountVal)} ke $bankNameInput ($accountNumberInput a.n $accountNameInput)"
            },
            userPin = profile?.transactionPin ?: "123456",
            onVerificationSuccess = {
                showBiometricAuthDialog = false
                if (withdrawalTypeTab == "CRYPTO") {
                    viewModel.submitCryptoWithdrawal(
                        amount = inputAmountVal,
                        cryptoSymbol = selectedCryptoSymbol,
                        network = selectedNetwork,
                        destinationWallet = destinationWalletInput,
                        note = "Crypto Withdrawal Request"
                    )
                } else {
                    viewModel.withdrawSaldo(
                        amount = inputAmountVal,
                        bankName = bankNameInput,
                        bankNum = accountNumberInput,
                        bankHolder = accountNameInput,
                        isManual = true
                    )
                }
            },
            onDismiss = { showBiometricAuthDialog = false }
        )
    }
}
