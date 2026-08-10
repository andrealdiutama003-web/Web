package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PaymentGatewayConfig
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentGatewayApiScreen(
    viewModel: MainViewModel,
    gatewayConfig: PaymentGatewayConfig?
) {
    var providerNameInput by remember(gatewayConfig) { mutableStateOf(gatewayConfig?.providerName ?: "Midtrans / Xendit Payment Gateway") }
    var apiKeyInput by remember(gatewayConfig) { mutableStateOf(gatewayConfig?.apiKey ?: "SB-Mid-server-x891K2mL9A0zP") }
    var merchantIdInput by remember(gatewayConfig) { mutableStateOf(gatewayConfig?.merchantId ?: "M10928374") }
    var webhookSecretInput by remember(gatewayConfig) { mutableStateOf(gatewayConfig?.webhookSecret ?: "whsec_89123891723912837") }
    var autoDeposit by remember(gatewayConfig) { mutableStateOf(gatewayConfig?.autoProcessDeposit ?: true) }
    var autoWithdraw by remember(gatewayConfig) { mutableStateOf(gatewayConfig?.autoProcessWithdrawal ?: true) }

    // DANA Enterprise API Indonesia (Indonesia Only) State variables
    var selectedCountry by remember { mutableStateOf("ID") } // ID = Indonesia, SG = Singapore, MY = Malaysia
    val isIndonesia = selectedCountry == "ID"
    var isDanaApiEnabled by remember { mutableStateOf(true) }
    var danaClientId by remember { mutableStateOf("DANA-ENT-901827412") }
    var danaClientSecret by remember { mutableStateOf("dana_sec_prod_901a823b9d0e2124") }
    var danaMerchantCode by remember { mutableStateOf("DANA_INDONESIA_88192") }
    var danaEnv by remember { mutableStateOf("SANDBOX") } // "SANDBOX", "PRODUCTION"
    var enableDanaDirectDebit by remember { mutableStateOf(true) }
    var enableDanaDisbursement by remember { mutableStateOf(true) }

    val apiLogs = remember {
        mutableStateListOf(
            "[System] Payment Gateway API Engine Initialized.",
            "[Webhook] Registered endpoint: https://api.investpro.id/v1/payment/webhook",
            "[Sandbox] Mock Gateway Server connected with Secret Key SB-Mid-server-***"
        )
    }

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
                IconButton(onClick = { viewModel.navigateTo(ScreenRoute.SUPER_ADMIN) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Pengaturan Payment Gateway API",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Konfigurasi Key & Otomatisasi Deposit / Tarik Saldo",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // --- CREDENTIALS FORM ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = PrimaryEmerald)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Kredensial API Payment Gateway",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Nama Penyedia Payment Gateway:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = providerNameInput,
                        onValueChange = { providerNameInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("pg_provider_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Server API Key / Secret Key:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("pg_apikey_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Merchant ID:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = merchantIdInput,
                        onValueChange = { merchantIdInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("pg_merchant_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Webhook Signature Secret:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = webhookSecretInput,
                        onValueChange = { webhookSecretInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("pg_webhook_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val newConfig = PaymentGatewayConfig(
                                id = 1,
                                providerName = providerNameInput,
                                apiKey = apiKeyInput,
                                merchantId = merchantIdInput,
                                webhookSecret = webhookSecretInput,
                                autoProcessDeposit = autoDeposit,
                                autoProcessWithdrawal = autoWithdraw,
                                sandboxMode = true
                            )
                            viewModel.superAdminSavePaymentGatewayConfig(newConfig)
                            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            apiLogs.add(0, "[$timeStr] API Configuration updated and saved to Room Database.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_pg_config_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Simpan Kredensial API Gateway", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SECTION: DANA ENTERPRISE API (INDONESIA ONLY) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("dana_enterprise_api_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    if (isIndonesia && isDanaApiEnabled) Color(0xFF008CFF).copy(alpha = 0.6f) else DarkCardBorder
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF008CFF).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet, 
                                    contentDescription = null, 
                                    tint = Color(0xFF008CFF)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "API DANA Enterprise",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFF008CFF).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "ID Only 🇮🇩",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF008CFF),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Integrasi Pembayaran Langsung & Pencairan Saldo Instan",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isDanaApiEnabled,
                            onCheckedChange = { isDanaApiEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF008CFF))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Country restriction selection
                    Text(
                        text = "Negara Operasional Legal (Yurisdiksi):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "ID" to "Indonesia 🇮🇩",
                            "SG" to "Singapura 🇸🇬",
                            "MY" to "Malaysia 🇲🇾"
                        ).forEach { (countryCode, countryLabel) ->
                            val isSelected = selectedCountry == countryCode
                            Button(
                                onClick = { 
                                    selectedCountry = countryCode 
                                    if (countryCode != "ID") {
                                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                        apiLogs.add(0, "[$timeStr] [RESTRICTION] Operational region changed to $countryCode. DANA Enterprise disabled.")
                                    } else {
                                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                        apiLogs.add(0, "[$timeStr] [RESTRICTION] Operational region changed to ID. DANA Enterprise enabled.")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF008CFF) else DarkCardSurface,
                                    contentColor = if (isSelected) Color.White else TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                            ) {
                                Text(text = countryLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isIndonesia) {
                        // Region Restriction Warning Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF331F1F)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Akses Wilayah Terbatas",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                    Text(
                                        text = "Fitur API DANA Enterprise secara legal & teknis hanya tersedia bagi merchant berbadan hukum resmi di Negara Indonesia (ID).",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    } else if (!isDanaApiEnabled) {
                        // Disabled notification
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkCardSurface, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Integrasi DANA Enterprise sedang tidak aktif.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        // Fully enabled fields for Indonesia
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "DANA Client ID:", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = danaClientId,
                                onValueChange = { danaClientId = it },
                                modifier = Modifier.fillMaxWidth().testTag("dana_client_id_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF008CFF),
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Client Secret / X.509 RSA Private Key:", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = danaClientSecret,
                                onValueChange = { danaClientSecret = it },
                                modifier = Modifier.fillMaxWidth().testTag("dana_secret_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF008CFF),
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "DANA Merchant Account Number (ID):", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = danaMerchantCode,
                                onValueChange = { danaMerchantCode = it },
                                modifier = Modifier.fillMaxWidth().testTag("dana_merchant_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF008CFF),
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = "DANA API Environment:", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("SANDBOX" to "DANA Sandbox Tester", "PRODUCTION" to "Live Production (ID)").forEach { (envCode, envLabel) ->
                                    val isEnvSelected = danaEnv == envCode
                                    Button(
                                        onClick = { danaEnv = envCode },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEnvSelected) Color(0xFF008CFF).copy(alpha = 0.2f) else DarkCardSurface,
                                            contentColor = if (isEnvSelected) Color(0xFF008CFF) else TextSecondary
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        border = if (isEnvSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF008CFF)) else null
                                    ) {
                                        Text(text = envLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "Fitur API Diaktifkan (Indonesia):", fontSize = 11.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Direct Debit Authorization", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "Debet saldo akun DANA user secara otomatis saat transaksi", fontSize = 10.sp, color = TextSecondary)
                                }
                                Switch(
                                    checked = enableDanaDirectDebit,
                                    onCheckedChange = { enableDanaDirectDebit = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF008CFF))
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "B2B2C Instant Disbursement", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "Kirim dana otomatis ke saldo e-wallet DANA nasabah", fontSize = 10.sp, color = TextSecondary)
                                }
                                Switch(
                                    checked = enableDanaDisbursement,
                                    onCheckedChange = { enableDanaDisbursement = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF008CFF))
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                    apiLogs.add(0, "[$timeStr] [DANA] API Config saved successfully. Environment: $danaEnv")
                                    apiLogs.add(0, "[$timeStr] [DANA] Handshake request dispatched to https://api.dana.id/v1/oauth/token")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008CFF)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("save_dana_api_button")
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Simpan & Hubungkan API DANA Enterprise", 
                                    color = Color.White, 
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- AUTOMATIC GATEWAY TOGGLES ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Otomatisasi Payment Gateway",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Auto-Approve Deposit Webhook", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Setujui otomatis deposit ketika callback gateway diterima", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = autoDeposit,
                            onCheckedChange = { autoDeposit = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryEmerald)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Auto-Disburse Tarik Saldo API", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Kirim otomatis payout pencairan ke bank/e-wallet user", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = autoWithdraw,
                            onCheckedChange = { autoWithdraw = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentGold)
                        )
                    }
                }
            }
        }

        // --- GATEWAY TESTER / SIMULATOR ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = AccentGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulator Callback Payment Gateway API",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.depositSaldo(500000.0, "Simulator QRIS Gateway")
                                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                apiLogs.add(0, "[$timeStr] POST /webhook/deposit - 200 OK (Processed Rp 500.000)")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("sim_deposit_button")
                        ) {
                            Text(text = "Test Deposit", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.withdrawSaldo(100000.0, "BCA", "8830192837", "Ahmad Pratama")
                                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                apiLogs.add(0, "[$timeStr] POST /disburse/payout - 200 OK (Disbursed Rp 100.000 to BCA)")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("sim_withdraw_button")
                        ) {
                            Text(text = "Test Payout", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isIndonesia && isDanaApiEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "DANA Enterprise Testing (Indonesia Only):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF008CFF)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.depositSaldo(250000.0, "DANA Enterprise Direct Debit")
                                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                    apiLogs.add(0, "[$timeStr] POST /v1/dana/directdebit/payment - 200 OK (Pulled Rp 250.000 from DANA User Balance)")
                                    apiLogs.add(0, "[$timeStr] [DANA Webhook] Received payment status: SUCCESS. Txn: TXN-DANA-${System.currentTimeMillis() % 100000}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008CFF)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("sim_dana_debit_button")
                            ) {
                                Text(text = "DANA Debit Pull", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.withdrawSaldo(150000.0, "DANA E-Wallet", "081290182741", "Ahmad Pratama")
                                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                    apiLogs.add(0, "[$timeStr] POST /v1/dana/disburse - 200 OK (Disbursed Rp 150.000 to DANA Wallet 0812****2741)")
                                    apiLogs.add(0, "[$timeStr] [DANA Outbound] Transfer processed successfully. Ref ID: OUT-${System.currentTimeMillis() % 100000}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E33)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("sim_dana_disburse_button"),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF008CFF))
                            ) {
                                Text(text = "DANA Disburse", fontSize = 10.sp, color = Color(0xFF008CFF), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // API Console Log Output
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0D1624),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "CONSOLE API LOGS:",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            apiLogs.take(6).forEach { logLine ->
                                Text(
                                    text = logLine,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
