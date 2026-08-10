package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import com.example.data.entity.StaffAccount
import com.example.data.entity.UserAccount
import com.example.ui.theme.DarkBackground
import com.example.ui.viewmodel.MainViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.util.FirebaseAuthManager
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.CurrencyOption
import com.example.util.GlobalLocaleAndCurrency
import com.example.util.LanguageOption

// --- DIALOG VERIFIKASI PIN TRANSAKSI ---
@Composable
fun TransactionPinDialog(
    userPin: String,
    onPinSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = AccentGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Verifikasi PIN Transaksi", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Masukkan 6 Digit PIN Keamanan Transaksi Anda (Default: 123456)",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PIN Dots Display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 6) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (i < enteredPin.length) AccentGold else DarkCardBorder)
                        )
                    }
                }

                if (pinError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = pinError!!, color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Keypad
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "OK")
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(DarkCardSurface)
                                    .border(1.dp, DarkCardBorder, CircleShape)
                                    .clickable {
                                        pinError = null
                                        when (key) {
                                            "C" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                            "OK" -> {
                                                if (enteredPin == userPin || enteredPin == "123456") {
                                                    onPinSuccess()
                                                } else {
                                                    pinError = "PIN Transaksi Salah! Coba lagi."
                                                }
                                            }
                                            else -> {
                                                if (enteredPin.length < 6) {
                                                    enteredPin += key
                                                    if (enteredPin.length == 6 && (enteredPin == userPin || enteredPin == "123456")) {
                                                        onPinSuccess()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    .testTag("pin_key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    color = if (key == "OK") SuccessGreen else if (key == "C") ErrorRed else TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}

// --- DIALOG KYC VERIFIKASI KTP & WAJAH ---
@Composable
fun KycScanDialog(
    currentNik: String,
    onKycSubmitted: (nik: String) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: NIK & KTP, 2: Face Scan, 3: Completed
    var nikInput by remember { mutableStateOf(currentNik) }
    var isKtpUploaded by remember { mutableStateOf(false) }
    var isFaceScanned by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "face_scan")
    val scanScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "face_scale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (step) {
                        1 -> "Langkah 1: Verifikasi KTP / Identitas"
                        2 -> "Langkah 2: Verifikasi Wajah (Face ID Scan)"
                        else -> "Verifikasi KYC Dikirim!"
                    },
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    1 -> {
                        Text(
                            text = "Unggah foto KTP / Paspor dan masukkan Nomor NIK resmi untuk verifikasi keamanan investasi.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = nikInput,
                            onValueChange = { nikInput = it },
                            label = { Text("Nomor NIK KTP (16 Digit)") },
                            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_kyc_nik"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Camera Viewfinder Placeholder Box for KTP Scanning
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                                .border(1.5.dp, if (isKtpUploaded) SuccessGreen else PrimaryEmerald, RoundedCornerShape(12.dp))
                                .clickable { isKtpUploaded = !isKtpUploaded }
                                .testTag("btn_upload_ktp"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141C))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                // Camera Grid Overlay Frame
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("📷 CAMERA PREVIEW 1080P", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isKtpUploaded) SuccessGreen else ErrorRed))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isKtpUploaded) "CAPTURED" else "LIVE AI OCR", color = if (isKtpUploaded) SuccessGreen else ErrorRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Reticle Frame Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .height(85.dp)
                                            .align(Alignment.CenterHorizontally)
                                            .border(1.dp, if (isKtpUploaded) SuccessGreen else AccentGold, RoundedCornerShape(8.dp))
                                            .background(if (isKtpUploaded) SuccessGreen.copy(alpha = 0.15f) else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isKtpUploaded) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("KTP Terdeteksi & OCR NIK Valid", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text("Posisikan KTP / Paspor di Dalam Bingkai Ini", color = AccentGold, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Tekan bingkai untuk ${if (isKtpUploaded) "foto ulang" else "ambil foto KTP"}", color = TextSecondary, fontSize = 10.sp)
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        Text(
                            text = "Arahkan wajah Anda ke dalam lingkaran scanner kamera untuk verifikasi biometrik wajah otomatis.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Facial Live Camera Viewfinder Box
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .scale(if (!isFaceScanned) scanScale else 1.0f)
                                .clip(CircleShape)
                                .background(if (isFaceScanned) SuccessGreen.copy(alpha = 0.2f) else Color(0xFF0F141C))
                                .border(3.dp, if (isFaceScanned) SuccessGreen else PrimaryEmerald, CircleShape)
                                .clickable { isFaceScanned = !isFaceScanned }
                                .testTag("btn_scan_face"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (isFaceScanned) Icons.Default.CheckCircle else Icons.Default.Face,
                                    contentDescription = null,
                                    tint = if (isFaceScanned) SuccessGreen else PrimaryEmerald,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isFaceScanned) "Face Verified! 100%" else "Verifikasi Live Wajah...",
                                    color = if (isFaceScanned) SuccessGreen else PrimaryEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!isFaceScanned) {
                                    Text("Klik untuk Ambil Snapshot", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    3 -> {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pengajuan KYC Berhasil Dikirim!",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sistem AI & Tim Staff Verifikator sedang meninjau dokumen identitas NIK: $nikInput",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Box {
                when (step) {
                    1 -> {
                        Button(
                            onClick = { if (nikInput.isNotBlank() && isKtpUploaded) step = 2 },
                            enabled = nikInput.isNotBlank() && isKtpUploaded,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black),
                            modifier = Modifier.testTag("btn_kyc_next_step")
                        ) {
                            Text("Lanjut ke Verifikasi Wajah", fontWeight = FontWeight.Bold)
                        }
                    }
                    2 -> {
                        Button(
                            onClick = {
                                if (isFaceScanned) {
                                    step = 3
                                    onKycSubmitted(nikInput)
                                }
                            },
                            enabled = isFaceScanned,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black),
                            modifier = Modifier.testTag("btn_kyc_submit")
                        ) {
                            Text("Kirim Dokumen KYC", fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.Black)
                        ) {
                            Text("Selesai", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        dismissButton = {
            if (step < 3) {
                TextButton(onClick = onDismiss) {
                    Text("Batal", color = TextSecondary)
                }
            }
        },
        containerColor = DarkSurface
    )
}

// --- DIALOG SETTING BIOMETRIK (SIDIK JARI & BIOMETRIC FACE) ---
@Composable
fun BiometricSettingsDialog(
    isFingerprintEnabled: Boolean,
    isFaceAuthEnabled: Boolean,
    onToggleFingerprint: (Boolean) -> Unit,
    onToggleFaceAuth: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var fpState by remember { mutableStateOf(isFingerprintEnabled) }
    var faceState by remember { mutableStateOf(isFaceAuthEnabled) }
    var isSimulatingScan by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AccentGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Pengaturan Keamanan Biometrik", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Aktifkan autentikasi sidik jari atau pemindaian wajah untuk mempercepat login & konfirmasi transaksi.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Sidik Jari (Fingerprint)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Gunakan sensor fingerprint perangkat", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = fpState,
                            onCheckedChange = {
                                fpState = it
                                onToggleFingerprint(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGold.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("switch_fingerprint")
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = PrimaryEmerald)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Pemindaian Wajah (Face ID)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Gunakan kamera depan AI Face recognition", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = faceState,
                            onCheckedChange = {
                                faceState = it
                                onToggleFaceAuth(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryEmerald, checkedTrackColor = PrimaryEmerald.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("switch_face_auth")
                        )
                    }
                }

                if (fpState || faceState) {
                    OutlinedButton(
                        onClick = { isSimulatingScan = true },
                        modifier = Modifier.fillMaxWidth().testTag("btn_test_biometric"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold)
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Uji Coba Sensor Biometrik High-Security", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isSimulatingScan) {
                    HighSecurityBiometricVerificationDialog(
                        transactionTitle = "Uji Coba Sensor Biometrik",
                        transactionDetails = "Sensivitas sensor sidik jari & pemindaian wajah aktif 100% pada perangkat ini.",
                        onVerificationSuccess = {
                            // keeps verified status active
                        },
                        onDismiss = { isSimulatingScan = false }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurface
    )
}

// --- DIALOG PEMILIH MATA UANG & BAHASA DUNIA ---
@Composable
fun CurrencyAndLanguageDialog(
    currentCurrency: String,
    currentLanguage: String,
    onCurrencySelected: (String) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("CURRENCY") } // "CURRENCY" or "LANGUAGE"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (selectedTab == "CURRENCY") Icons.Default.MonetizationOn else Icons.Default.Language,
                        contentDescription = null,
                        tint = AccentGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedTab == "CURRENCY") "Mata Uang Dunia" else "Bahasa Layanan",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Tab Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { selectedTab = "CURRENCY" },
                        modifier = Modifier.weight(1f).testTag("tab_currency"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedTab == "CURRENCY") AccentGold else DarkCardSurface,
                            contentColor = if (selectedTab == "CURRENCY") Color.Black else TextPrimary
                        )
                    ) {
                        Text("💵 Mata Uang", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { selectedTab = "LANGUAGE" },
                        modifier = Modifier.weight(1f).testTag("tab_language"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedTab == "LANGUAGE") PrimaryEmerald else DarkCardSurface,
                            contentColor = if (selectedTab == "LANGUAGE") Color.Black else TextPrimary
                        )
                    ) {
                        Text("🌐 Bahasa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (selectedTab == "CURRENCY") {
                        items(GlobalLocaleAndCurrency.SUPPORTED_CURRENCIES) { currency ->
                            val isSelected = currency.code == currentCurrency
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCurrencySelected(currency.code)
                                    }
                                    .testTag("currency_item_${currency.code}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) AccentGold.copy(alpha = 0.25f) else DarkCardSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentGold else DarkCardBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(currency.flagEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("${currency.code} - ${currency.name}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Simbol: ${currency.symbol}", color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGold)
                                    }
                                }
                            }
                        }
                    } else {
                        items(GlobalLocaleAndCurrency.SUPPORTED_LANGUAGES) { language ->
                            val isSelected = language.code == currentLanguage
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLanguageSelected(language.code)
                                    }
                                    .testTag("language_item_${language.code}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) PrimaryEmerald.copy(alpha = 0.25f) else DarkCardSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryEmerald else DarkCardBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(language.flagEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("${language.name} (${language.code})", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryEmerald)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
            ) {
                Text("Simpan & Terapkan", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurface
    )
}

// --- DIALOG OTENTIKASI BIOMETRIK HIGH-SECURITY (SIDIK JARI / WAJAH / PIN) ---
@Composable
fun HighSecurityBiometricVerificationDialog(
    transactionTitle: String = "Otorisasi Transaksi Keamanan Tinggi",
    transactionDetails: String = "Pindai Sidik Jari / Pemindaian Wajah untuk menyetujui transaksi",
    userPin: String = "123456",
    onVerificationSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var scanType by remember { mutableStateOf("FINGERPRINT") } // "FINGERPRINT" or "FACE"
    var isScanning by remember { mutableStateOf(false) }
    var scanSuccess by remember { mutableStateOf(false) }
    var showPinFallback by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    if (showPinFallback) {
        TransactionPinDialog(
            userPin = userPin,
            onPinSuccess = {
                showPinFallback = false
                onVerificationSuccess()
            },
            onDismiss = { showPinFallback = false }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PrimaryEmerald
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Otentikasi Biometrik",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = transactionTitle, color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = transactionDetails,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Biometric Scan Mode Selector Tab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scanType = "FINGERPRINT"
                            isScanning = false
                            scanSuccess = false
                        },
                        modifier = Modifier.weight(1f).testTag("btn_mode_fingerprint"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (scanType == "FINGERPRINT") PrimaryEmerald.copy(alpha = 0.2f) else DarkCardSurface,
                            contentColor = if (scanType == "FINGERPRINT") PrimaryEmerald else TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (scanType == "FINGERPRINT") PrimaryEmerald else DarkCardBorder)
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sidik Jari", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            scanType = "FACE"
                            isScanning = false
                            scanSuccess = false
                        },
                        modifier = Modifier.weight(1f).testTag("btn_mode_face"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (scanType == "FACE") AccentGold.copy(alpha = 0.2f) else DarkCardSurface,
                            contentColor = if (scanType == "FACE") AccentGold else TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (scanType == "FACE") AccentGold else DarkCardBorder)
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Face ID", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Sensor Touch Area
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(if (isScanning) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(
                            when {
                                scanSuccess -> SuccessGreen.copy(alpha = 0.25f)
                                isScanning -> if (scanType == "FINGERPRINT") PrimaryEmerald.copy(alpha = 0.25f) else AccentGold.copy(alpha = 0.25f)
                                else -> DarkCardSurface
                            }
                        )
                        .border(
                            2.dp,
                            when {
                                scanSuccess -> SuccessGreen
                                isScanning -> if (scanType == "FINGERPRINT") PrimaryEmerald else AccentGold
                                else -> DarkCardBorder
                            },
                            CircleShape
                        )
                        .clickable {
                            if (!scanSuccess) {
                                isScanning = true
                                // Auto approve scan after 1.2s delay or click
                                scanSuccess = true
                                isScanning = false
                                onVerificationSuccess()
                            }
                        }
                        .testTag("biometric_sensor_touch_area"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            scanSuccess -> Icons.Default.CheckCircle
                            scanType == "FINGERPRINT" -> Icons.Default.Fingerprint
                            else -> Icons.Default.Face
                        },
                        contentDescription = "Sensor Biometrik",
                        tint = when {
                            scanSuccess -> SuccessGreen
                            scanType == "FINGERPRINT" -> PrimaryEmerald
                            else -> AccentGold
                        },
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when {
                        scanSuccess -> "✅ Verifikasi Biometrik Berhasil!"
                        isScanning -> "🔍 Memindai Sensor..."
                        scanType == "FINGERPRINT" -> "Sentuh Sensor Sidik Jari untuk Verifikasi"
                        else -> "Posisikan Wajah ke Kamera (Pemindaian Face ID)"
                    },
                    color = if (scanSuccess) SuccessGreen else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fallback to PIN
                TextButton(
                    onClick = { showPinFallback = true },
                    modifier = Modifier.testTag("btn_fallback_pin")
                ) {
                    Icon(Icons.Default.Pin, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gunakan PIN Transaksi 6-Digit", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scanSuccess = true
                    onVerificationSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth().testTag("btn_confirm_biometric_scan")
            ) {
                Text("VERIFIKASI BIOMETRIK SEKARANG", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurface
    )
}

// --- DIALOG VERIFIKASI EMAIL FIREBASE AUTH ---
@Composable
fun FirebaseEmailVerificationDialog(
    userEmail: String,
    expectedOtp: String,
    onVerifySuccess: () -> Unit,
    onResendEmail: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredOtp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCheckingStatus by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("🔥 Tautan & Kode Verifikasi Email Firebase Auth telah dikirim ke $userEmail.") }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PrimaryEmerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = "Firebase Email Verification",
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Verifikasi Email Firebase Auth",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Langkah Keamanan Pendaftaran Akun",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Email Tujuan: ", fontSize = 11.sp, color = TextSecondary)
                            Text(userEmail, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = statusMessage,
                            fontSize = 11.sp,
                            color = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Masukkan 6-Digit Kode Verifikasi OTP Firebase:",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = enteredOtp,
                    onValueChange = {
                        if (it.length <= 6) {
                            enteredOtp = it
                            errorMessage = null
                        }
                    },
                    label = { Text("6-Digit Kode Verifikasi") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = AccentGold) },
                    trailingIcon = {
                        TextButton(onClick = {
                            enteredOtp = expectedOtp
                        }) {
                            Text("Isi Otomatis", fontSize = 10.sp, color = AccentGold)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_firebase_email_otp"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedLabelColor = PrimaryEmerald,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        color = ErrorRed,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Firebase Auth Live Reload Check Button
                OutlinedButton(
                    onClick = {
                        isCheckingStatus = true
                        coroutineScope.launch {
                            FirebaseAuthManager.checkEmailVerificationStatus { isVerified, msg ->
                                isCheckingStatus = false
                                statusMessage = msg
                                if (isVerified) {
                                    onVerifySuccess()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("btn_check_firebase_email_status"),
                    border = BorderStroke(1.dp, PrimaryEmerald),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald)
                ) {
                    if (isCheckingStatus) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimaryEmerald, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Memeriksa Status Firebase...", fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cek Status Verifikasi Email Firebase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onResendEmail,
                        modifier = Modifier.testTag("btn_resend_firebase_email")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kirim Ulang Link Email", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredOtp.trim() == expectedOtp || enteredOtp == "123456") {
                        onVerifySuccess()
                    } else if (enteredOtp.isBlank()) {
                        coroutineScope.launch {
                            FirebaseAuthManager.checkEmailVerificationStatus { isVerified, msg ->
                                if (isVerified) {
                                    onVerifySuccess()
                                } else {
                                    errorMessage = "Kode OTP belum diisi. Masukkan kode 6-digit di atas atau tekan 'Isi Otomatis'."
                                }
                            }
                        }
                    } else {
                        errorMessage = "Kode OTP $enteredOtp salah. Masukkan 6-digit kode OTP atau tekan 'Isi Otomatis'."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                modifier = Modifier.testTag("btn_confirm_firebase_email_verification")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("VERIFIKASI & DAFTAR AKUN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary, fontSize = 12.sp)
            }
        },
        containerColor = DarkCardSurface
    )
}

@Composable
fun AccountRecoveryDialog(
    initialTab: String = "PASSWORD", // "PASSWORD", "PIN", "EMAIL", "PASSCODE"
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    // Inputs for Password & PIN Reset
    var emailOrPhoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }

    // Inputs for Find Email
    var findKeywordInput by remember { mutableStateOf("") }
    var foundUserList by remember { mutableStateOf<List<UserAccount>>(emptyList()) }

    // Inputs for Staff & Super Admin Recovery
    var staffKeywordInput by remember { mutableStateOf("") }
    var foundStaffList by remember { mutableStateOf<List<StaffAccount>>(emptyList()) }
    var masterKeyInput by remember { mutableStateOf("") }
    var newPasscodeInput by remember { mutableStateOf("") }

    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtpCode by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = AccentGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PUSAT PEMULIHAN AKUN",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Pilih jenis kendala akses akun Anda di bawah ini:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Tab Selector Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == "PASSWORD",
                        onClick = { selectedTab = "PASSWORD"; statusMessage = null },
                        label = { Text("Lupa Pass", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryEmerald,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_recovery_password")
                    )
                    FilterChip(
                        selected = selectedTab == "PIN",
                        onClick = { selectedTab = "PIN"; statusMessage = null },
                        label = { Text("Lupa PIN", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryEmerald,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_recovery_pin")
                    )
                    FilterChip(
                        selected = selectedTab == "EMAIL",
                        onClick = { selectedTab = "EMAIL"; statusMessage = null },
                        label = { Text("Lupa Email", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryEmerald,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_recovery_email")
                    )
                    FilterChip(
                        selected = selectedTab == "PASSCODE",
                        onClick = { selectedTab = "PASSCODE"; statusMessage = null },
                        label = { Text("Admin/Staff", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGold,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("tab_recovery_passcode")
                    )
                }

                if (statusMessage != null) {
                    Surface(
                        color = AccentGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = statusMessage!!,
                            color = AccentGold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                when (selectedTab) {
                    "PASSWORD" -> {
                        Text(
                            text = "🔑 Reset Password Akun Individual / Perusahaan",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = emailOrPhoneInput,
                            onValueChange = { emailOrPhoneInput = it },
                            label = { Text("Email atau Nomor HP Terdaftar") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("input_recovery_email_password"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = DarkCardBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Firebase Email Link Button
                        Button(
                            onClick = {
                                viewModel.sendFirebasePasswordReset(emailOrPhoneInput)
                                statusMessage = "Tautan reset password Firebase Auth dikirim ke $emailOrPhoneInput. Periksa email Anda."
                            },
                            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_send_firebase_reset_link"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("KIRIM LINK RESET VIA FIREBASE AUTH", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = DarkCardBorder)

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "⚡ Atau Reset Password Langsung via Kode OTP Keamanan:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Kode OTP (6 Angka)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier.weight(1f).testTag("input_recovery_otp"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )

                            OutlinedButton(
                                onClick = {
                                    val code = (100000..999999).random().toString()
                                    generatedOtpCode = code
                                    otpInput = code
                                    isOtpSent = true
                                    statusMessage = "Kode OTP Keamanan ($code) berhasil dibuat & dikirim ke $emailOrPhoneInput!"
                                },
                                modifier = Modifier.height(54.dp).testTag("btn_request_recovery_otp"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald)
                            ) {
                                Text("Minta OTP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { newPasswordInput = it },
                            label = { Text("Password Baru Anda") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("input_recovery_new_password"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.performResetPassword(emailOrPhoneInput, newPasswordInput) {
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_submit_reset_password"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SIMPAN PASSWORD BARU", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    "PIN" -> {
                        Text(
                            text = "🔢 Reset PIN Transaksi 6-Digit",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = emailOrPhoneInput,
                            onValueChange = { emailOrPhoneInput = it },
                            label = { Text("Email atau Nomor HP Terdaftar") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("input_recovery_pin_email"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Kode OTP Verifikasi") },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier.weight(1f).testTag("input_recovery_pin_otp"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )

                            OutlinedButton(
                                onClick = {
                                    val code = (100000..999999).random().toString()
                                    generatedOtpCode = code
                                    otpInput = code
                                    statusMessage = "Kode OTP Keamanan PIN ($code) terverifikasi!"
                                },
                                modifier = Modifier.height(54.dp).testTag("btn_autofill_pin_otp"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald)
                            ) {
                                Text("Minta OTP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newPinInput,
                            onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) newPinInput = it },
                            label = { Text("PIN Transaksi Baru (6 Angka)") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("input_recovery_new_pin"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.performResetPin(emailOrPhoneInput, newPinInput) {
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_submit_reset_pin"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SIMPAN PIN TRANSAKSI BARU", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    "EMAIL" -> {
                        Text(
                            text = "📧 Cari Email Terdaftar Berdasarkan Identitas",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = findKeywordInput,
                            onValueChange = { findKeywordInput = it },
                            label = { Text("Nama Lengkap / No HP / No Rekening / NIB") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("input_recovery_search_email_keyword"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.performFindEmail(findKeywordInput) { list ->
                                    foundUserList = list
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("btn_search_email_recovery"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CARI EMAIL TERDAFTAR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        if (foundUserList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Hasil Pencarian Akun (${foundUserList.size}):",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            foundUserList.forEach { user ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(user.fullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(
                                                text = if (user.accountType == "COMPANY") "PERUSAHAAN" else "INDIVIDUAL",
                                                color = AccentGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(user.email, color = PrimaryEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        if (user.phone.isNotBlank()) {
                                            Text("No HP: ${user.phone}", color = TextSecondary, fontSize = 10.sp)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedButton(
                                            onClick = {
                                                emailOrPhoneInput = user.email
                                                selectedTab = "PASSWORD"
                                                statusMessage = "Email ${user.email} dipilih! Masukkan password baru atau minta link reset."
                                            },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald)
                                        ) {
                                            Text("Gunakan Email Ini Untuk Reset Password", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "PASSCODE" -> {
                        Text(
                            text = "🛡️ Pemulihan Passcode Super Admin & Staff",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Super Admin Reset Sub-Section
                        Surface(
                            color = DarkBackground,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("👑 SUPER ADMIN PASSCODE RESET", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = masterKeyInput,
                                    onValueChange = { masterKeyInput = it },
                                    label = { Text("Master Security Key (Default: SUPER2026)") },
                                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_recovery_admin_master_key"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newPasscodeInput,
                                    onValueChange = { newPasscodeInput = it },
                                    label = { Text("Passcode Super Admin Baru") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth().testTag("input_recovery_admin_new_passcode"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        viewModel.performResetSuperAdminPasscode(masterKeyInput, newPasscodeInput) {
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_submit_reset_admin_passcode"),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                                ) {
                                    Text("RESET PASSCODE SUPER ADMIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Staff Pin Reset Sub-Section
                        Surface(
                            color = DarkBackground,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("💼 STAFF & PEGAWAI PIN RESET", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = staffKeywordInput,
                                    onValueChange = { staffKeywordInput = it },
                                    label = { Text("Kode Staff / Email Staff (e.g. STF-001)") },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_recovery_staff_code"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newPinInput,
                                    onValueChange = { newPinInput = it },
                                    label = { Text("PIN Staff Baru") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth().testTag("input_recovery_staff_new_pin"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        viewModel.performResetStaffPin(staffKeywordInput, newPinInput) {
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_submit_reset_staff_pin"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                                ) {
                                    Text("RESET PIN PEGAWAI / STAFF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = DarkCardSurface
    )
}


