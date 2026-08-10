package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycSubmissionScreen(
    viewModel: MainViewModel
) {
    val authSession by viewModel.authSession.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val currentKycStatus = authSession.activeUser?.kycStatus ?: userProfile?.kycStatus ?: "UNVERIFIED"
    val currentNik = authSession.activeUser?.ktpNumber ?: userProfile?.ktpNumber ?: ""
    val currentTier = authSession.activeUser?.requestedAccountTier ?: userProfile?.requestedAccountTier ?: "INDIVIDUAL"
    val currentLimit = authSession.activeUser?.kycTransactionLimit ?: userProfile?.kycTransactionLimit ?: 10_000_000.0

    var fullNameInput by remember { mutableStateOf(authSession.activeUser?.fullName ?: userProfile?.name ?: "") }
    var nikInput by remember { mutableStateOf(currentNik) }
    var selectedTier by remember { mutableStateOf(currentTier) }
    var isKtpUploaded by remember { mutableStateOf(false) }
    var isFaceScanned by remember { mutableStateOf(false) }

    // Animations for camera scan effect
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_anim")
    val scanScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Verifikasi Identitas (KYC)",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) },
                        modifier = Modifier.testTag("btn_kyc_back")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkSurface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // KYC STATUS HERO BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = when (currentKycStatus) {
                                "VERIFIED" -> PrimaryEmerald
                                "PENDING" -> AccentGold
                                "REJECTED" -> ErrorRed
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (currentKycStatus) {
                                "VERIFIED" -> "AKUN TERVERIFIKASI"
                                "PENDING" -> "MENUNGGU PERSETUJUAN"
                                "REJECTED" -> "VERIFIKASI DITOLAK"
                                else -> "BELUM TERVERIFIKASI"
                            },
                            fontWeight = FontWeight.Bold,
                            color = when (currentKycStatus) {
                                "VERIFIED" -> PrimaryEmerald
                                "PENDING" -> AccentGold
                                "REJECTED" -> ErrorRed
                                else -> TextSecondary
                            },
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (currentKycStatus) {
                                "VERIFIED" -> "Selamat! Identitas Anda telah dikonfirmasi oleh Super Admin. Anda dapat menikmati batas transaksi maksimal."
                                "PENDING" -> "Pengajuan KYC Anda sedang ditinjau oleh tim kepatuhan kami. Hubungi dukungan jika memiliki pertanyaan."
                                "REJECTED" -> "Pengajuan KYC ditolak karena dokumen tidak lengkap atau tidak terbaca. Harap ajukan ulang."
                                else -> "Lengkapi data identitas (KTP) dan verifikasi wajah untuk mengaktifkan akun serta meningkatkan limit nominal transaksi harian."
                            },
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Limit Transaksi Saat Ini: ",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    "Rp ${String.format("%,.0f", currentLimit)}",
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            if (currentKycStatus != "VERIFIED" && currentKycStatus != "PENDING") {
                // FORM INPUTS
                item {
                    Text(
                        "Pilih Tingkatan Limit & Jenis Akun",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // INDIVIDUAL TIER CARD
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTier = "INDIVIDUAL" }
                                .testTag("tier_individual_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTier == "INDIVIDUAL") PrimaryEmerald.copy(alpha = 0.15f) else DarkCardSurface
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (selectedTier == "INDIVIDUAL") PrimaryEmerald else DarkCardBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Individual",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTier == "INDIVIDUAL") PrimaryEmerald else TextPrimary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Limit Harian:",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    "Rp 10.000.000",
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "• Instan Verifikasi",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // ENTERPRISE TIER CARD
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTier = "ENTERPRISE" }
                                .testTag("tier_enterprise_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTier == "ENTERPRISE") PrimaryEmerald.copy(alpha = 0.15f) else DarkCardSurface
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (selectedTier == "ENTERPRISE") PrimaryEmerald else DarkCardBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Enterprise",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTier == "ENTERPRISE") PrimaryEmerald else TextPrimary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Limit Harian:",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    "Rp 5.000.000.000+",
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "• Review Manual Admin",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // DETAILS ENTRY
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Detail Identitas Resmi",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )

                        OutlinedTextField(
                            value = fullNameInput,
                            onValueChange = { fullNameInput = it },
                            label = { Text("Nama Lengkap Sesuai KTP") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_kyc_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedLabelColor = PrimaryEmerald,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = nikInput,
                            onValueChange = { if (it.length <= 16) nikInput = it },
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
                                focusedLabelColor = PrimaryEmerald,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }

                // CAMERA SIMULATOR BOXES
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            "Unggah / Ambil Foto Dokumen",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )

                        // 1. KTP Scanner Box
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.5.dp,
                                    if (isKtpUploaded) PrimaryEmerald else DarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { isKtpUploaded = !isKtpUploaded }
                                .testTag("btn_upload_ktp"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1017))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isKtpUploaded) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("KTP Berhasil Dipindai!", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Ketuk untuk membatalkan atau ambil ulang", color = TextSecondary, fontSize = 10.sp)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                "📷 PREVIEW SCANNER: KTP_READER",
                                                color = Color.LightGray,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Reticle overlay
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.8f)
                                                .height(75.dp)
                                                .align(Alignment.Center)
                                                .border(1.dp, AccentGold.copy(alpha = 0.5f * scanScale), RoundedCornerShape(6.dp))
                                                .background(AccentGold.copy(alpha = 0.03f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.CameraAlt,
                                                    contentDescription = null,
                                                    tint = AccentGold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Posisikan KTP di Sini",
                                                    color = AccentGold,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Selfie / Face Scan Box
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.5.dp,
                                    if (isFaceScanned) PrimaryEmerald else DarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { isFaceScanned = !isFaceScanned }
                                .testTag("btn_face_scan"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1017))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isFaceScanned) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Face, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Pindai Wajah Berhasil!", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Ketuk untuk membatalkan atau ambil ulang", color = TextSecondary, fontSize = 10.sp)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                "🤳 PREVIEW SCANNER: FACE_RECOGNITION_AI",
                                                color = Color.LightGray,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Reticle overlay
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .align(Alignment.Center)
                                                .border(1.5.dp, PrimaryEmerald.copy(alpha = 0.5f * scanScale), CircleShape)
                                                .background(PrimaryEmerald.copy(alpha = 0.03f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    Icons.Default.Face,
                                                    contentDescription = null,
                                                    tint = PrimaryEmerald,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    "SELFIE",
                                                    color = PrimaryEmerald,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // WARNING INFO BOX
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Platform menggunakan AI Enkripsi 256-bit tingkat perbankan untuk mengamankan data dan foto KTP Anda. Informasi tidak akan pernah dibagikan tanpa izin tertulis Anda.",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // SUBMIT BUTTON
                item {
                    val isFormValid = fullNameInput.isNotBlank() && nikInput.length == 16 && isKtpUploaded && isFaceScanned

                    Button(
                        onClick = {
                            if (isFormValid) {
                                viewModel.submitKycRequest(
                                    nik = nikInput,
                                    requestedTier = selectedTier
                                )
                            }
                        },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.Black,
                            disabledContainerColor = DarkCardBorder,
                            disabledContentColor = TextSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_kyc_submit"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (selectedTier == "ENTERPRISE") "Kirim Pengajuan KYC Enterprise" else "Verifikasi Instan Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // ALREADY SUBMITTED / VERIFIED - DISPLAY DOKUMEN & LIMIT INFO
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        border = BorderStroke(1.dp, DarkCardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Detail Dokumen Terdaftar",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = DarkCardBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Nama Lengkap", color = TextSecondary, fontSize = 12.sp)
                                Text(fullNameInput, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Nomor NIK KTP", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    if (currentNik.isBlank()) "Belum Terdeteksi" else currentNik.take(6) + "**********",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Kelas / Tingkatan Akun", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    currentTier.uppercase(),
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Batas Limit Transaksi", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    "Rp ${String.format("%,.0f", currentLimit)}",
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
