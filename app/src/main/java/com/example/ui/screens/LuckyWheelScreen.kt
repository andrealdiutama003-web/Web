package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.LuckyWheelConfig
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyWheelScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) }
) {
    val authSession by viewModel.authSession.collectAsState()
    val allUserAccounts by viewModel.allUserAccounts.collectAsState()
    val luckyWheelConfigState by viewModel.luckyWheelConfig.collectAsState()

    val config = luckyWheelConfigState ?: LuckyWheelConfig()

    // Find active user record
    val currentUserEmail = authSession.activeUser?.email ?: ""
    val userAccount = allUserAccounts.find { it.email.equals(currentUserEmail, ignoreCase = true) }
        ?: authSession.activeUser

    val isCompany = userAccount?.accountType == "COMPANY"
    val mainBalance = userAccount?.balance ?: 0.0
    val wheelDeposit = userAccount?.wheelDepositBalance ?: 0.0

    // Check daily claims today
    val sdf = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) }
    val todayStr = remember { sdf.format(java.util.Date()) }
    val claimsToday = if (userAccount?.lastWheelClaimDate == todayStr) userAccount.wheelClaimsToday else 0
    val claimsRemaining = (config.dailyClaimQuota - claimsToday).coerceAtLeast(0)

    // Deposit top-up state
    var depositInput by remember { mutableStateOf("") }
    var showConfirmDepositDialog by remember { mutableStateOf(false) }

    // Spin Wheel Animation State
    var isSpinning by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    var showResultDialog by remember { mutableStateOf(false) }
    var lastWonProfit by remember { mutableDoubleStateOf(0.0) }
    var lastMultiplier by remember { mutableDoubleStateOf(1.0) }

    // Share Summary Report state
    var showShareReportDialog by remember { mutableStateOf(false) }

    // Multipliers on wheel segments
    val segments = remember { listOf(1.0, 1.2, 1.5, 2.0, 1.1, 2.5, 1.3, 3.0) }
    val segmentColors = remember {
        listOf(
            Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF3B82F6), Color(0xFFEC4899),
            Color(0xFF8B5CF6), Color(0xFFF97316), Color(0xFF06B6D4), Color(0xFFEF4444)
        )
    }

    // Determine current user tier profit pct
    val currentProfitPct = remember(wheelDeposit, isCompany, config) {
        if (isCompany) {
            when {
                wheelDeposit >= config.companyTier3Min -> config.companyTier3ProfitPct
                wheelDeposit >= config.companyTier2Min -> config.companyTier2ProfitPct
                else -> config.companyTier1ProfitPct
            }
        } else {
            when {
                wheelDeposit >= config.individualTier3Min -> config.individualTier3ProfitPct
                wheelDeposit >= config.individualTier2Min -> config.individualTier2ProfitPct
                else -> config.individualTier1ProfitPct
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🎡 Roda Keberuntungan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isCompany) "Akun Perusahaan (Corporate)" else "Akun Personal (Individual)",
                            fontSize = 11.sp,
                            color = PrimaryEmerald
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_lucky_wheel")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER SUMMARY CARD ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryEmerald.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Casino,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Saldo Deposito Roda", fontSize = 12.sp, color = TextSecondary)
                                    Text(
                                        text = "Rp ${String.format("%,.0f", wheelDeposit)}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                }
                            }

                            // Quota Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (claimsRemaining > 0) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, if (claimsRemaining > 0) SuccessGreen else ErrorRed)
                            ) {
                                Text(
                                    text = "$claimsRemaining/$config.dailyClaimQuota Klaim Harian",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (claimsRemaining > 0) SuccessGreen else ErrorRed,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Locked Balance Notice
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ErrorRed.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Saldo Deposito Roda Terkunci PERMANEN & Tidak Dapat Ditarik Kembali. Menghasilkan profit harian ${String.format("%.1f", currentProfitPct)}%/klaim.",
                                    fontSize = 11.sp,
                                    color = ErrorRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        HorizontalDivider(color = DarkCardBorder)

                        // Info row: Tax rate & Main Wallet Balance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Pajak Claim Harian", fontSize = 11.sp, color = TextSecondary)
                                Text("${config.taxPct}% (PPh Admin)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Saldo Dompet Utama", fontSize = 11.sp, color = TextSecondary)
                                Text("Rp ${String.format("%,.0f", mainBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                    }
                }
            }

            // --- INTERACTIVE WHEEL CANVA & SPIN SECTION ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🎯 Putar Roda & Dapatkan Multiplier Profit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Wheel Canvas Container
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Canvas for wheel
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(rotationAngle)
                            ) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val radius = canvasWidth / 2f
                                val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
                                val sweepAngle = 360f / segments.size

                                // Draw wheel segments
                                segments.forEachIndexed { index, mult ->
                                    val startAngle = index * sweepAngle
                                    drawArc(
                                        color = segmentColors[index % segmentColors.size],
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = true,
                                        topLeft = Offset(0f, 0f),
                                        size = Size(canvasWidth, canvasHeight)
                                    )
                                }

                                // Outer border
                                drawCircle(
                                    color = Color(0xFFFFD700),
                                    radius = radius,
                                    center = center,
                                    style = Stroke(width = 8.dp.toPx())
                                )

                                // Inner center hub
                                drawCircle(
                                    color = Color(0xFF1E293B),
                                    radius = 36.dp.toPx(),
                                    center = center
                                )
                            }

                            // Center Hub Icon / Text
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text("LUCKY", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AccentGold)
                            }

                            // Pointer Arrow at top
                            Canvas(
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.TopCenter)
                            ) {
                                val path = Path().apply {
                                    moveTo(size.width / 2f, size.height)
                                    lineTo(0f, 0f)
                                    lineTo(size.width, 0f)
                                    close()
                                }
                                drawPath(path, color = Color.Yellow)
                            }
                        }

                        // Spin / Claim Button
                        Button(
                            onClick = {
                                if (wheelDeposit <= 0) {
                                    viewModel.depositToLuckyWheel(0.0) // triggers prompt
                                    return@Button
                                }
                                if (claimsRemaining <= 0) {
                                    return@Button
                                }
                                if (isSpinning) return@Button

                                isSpinning = true
                                coroutineScope.launch {
                                    val randomSegmentIndex = (segments.indices).random()
                                    val chosenMultiplier = segments[randomSegmentIndex]
                                    val extraRounds = (5..8).random() * 360f
                                    val segmentAngle = 360f / segments.size
                                    // Pointer is at top (270 deg), align angle
                                    val targetAngle = extraRounds + (360f - (randomSegmentIndex * segmentAngle)) - (segmentAngle / 2f)

                                    val animatable = Animatable(rotationAngle)
                                    animatable.animateTo(
                                        targetValue = rotationAngle + targetAngle,
                                        animationSpec = tween(
                                            durationMillis = 3500,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    rotationAngle = animatable.value % 360f
                                    isSpinning = false

                                    // Trigger claim in VM
                                    viewModel.claimLuckyWheelDailyProfit(chosenMultiplier) { profitAmount ->
                                        lastWonProfit = profitAmount
                                        lastMultiplier = chosenMultiplier
                                        showResultDialog = true
                                    }
                                }
                            },
                            enabled = !isSpinning && (wheelDeposit > 0) && (claimsRemaining > 0),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_spin_lucky_wheel"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                disabledContainerColor = DarkCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSpinning) {
                                CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Memutar Roda...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            } else if (wheelDeposit <= 0) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Wajib Deposito Untuk Memutar", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            } else if (claimsRemaining <= 0) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kuota Klaim Harian Habis", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PUTAR RODA & KLAIM PROFIT", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- FORM: TIMPA DEPOSITO RODA KEBERUNTUNGAN ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Timpa Deposito Roda Keberuntungan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "Masukkan saldo bebas dari dompet utama Anda untuk meningkatkan persentase keuntungan harian Roda Keberuntungan Anda. (Saldo akan TERKUNCI PERMANEN).",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = depositInput,
                            onValueChange = { depositInput = it.filter { char -> char.isDigit() } },
                            label = { Text("Nominal Deposito Baru (Rp)", color = TextSecondary) },
                            placeholder = { Text("Contoh: 1000000", color = DarkCardBorder) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_wheel_deposit_amount")
                        )

                        // Quick Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val quickAmounts = listOf(100_000.0, 500_000.0, 1_000_000.0, 5_000_000.0)
                            quickAmounts.forEach { amt ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkBackground,
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { depositInput = amt.toLong().toString() }
                                ) {
                                    Text(
                                        text = if (amt >= 1_000_000) "${(amt / 1_000_000).toInt()}Jt" else "${(amt / 1000).toInt()}rb",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val amount = depositInput.toDoubleOrNull() ?: 0.0
                                if (amount <= 0) {
                                    return@Button
                                }
                                showConfirmDepositDialog = true
                            },
                            enabled = (depositInput.toDoubleOrNull() ?: 0.0) > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_wheel_deposit"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PROSES DEPOSITO TERKUNCI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            // --- TIER PROFIT MATRIX TABLE CARD ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "📊 Tabel Keuntungan Harian Berdasarkan Deposito",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Semakin besar deposito Roda Keberuntungan Anda, semakin besar persentase keuntungan harian yang diperoleh per klaim.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        HorizontalDivider(color = DarkCardBorder)

                        if (isCompany) {
                            // Corporate Tier Matrix
                            TierRow(
                                title = "Tier 1 Korporat",
                                range = "Rp ${String.format("%,.0f", config.companyTier1Min)} - Rp ${String.format("%,.0f", config.companyTier1Max)}",
                                rate = "${config.companyTier1ProfitPct}% / Klaim",
                                isActive = wheelDeposit >= config.companyTier1Min && wheelDeposit < config.companyTier2Min
                            )
                            TierRow(
                                title = "Tier 2 Korporat",
                                range = "Rp ${String.format("%,.0f", config.companyTier2Min)} - Rp ${String.format("%,.0f", config.companyTier2Max)}",
                                rate = "${config.companyTier2ProfitPct}% / Klaim",
                                isActive = wheelDeposit >= config.companyTier2Min && wheelDeposit < config.companyTier3Min
                            )
                            TierRow(
                                title = "Tier 3 Korporat Platinum",
                                range = "> Rp ${String.format("%,.0f", config.companyTier3Min)}",
                                rate = "${config.companyTier3ProfitPct}% / Klaim",
                                isActive = wheelDeposit >= config.companyTier3Min
                            )
                        } else {
                            // Individual Tier Matrix
                            TierRow(
                                title = "Tier 1 Individual",
                                range = "Rp ${String.format("%,.0f", config.individualTier1Min)} - Rp ${String.format("%,.0f", config.individualTier1Max)}",
                                rate = "${config.individualTier1ProfitPct}% / Klaim",
                                isActive = wheelDeposit >= config.individualTier1Min && wheelDeposit < config.individualTier2Min
                            )
                            TierRow(
                                title = "Tier 2 Individual Growth",
                                range = "Rp ${String.format("%,.0f", config.individualTier2Min)} - Rp ${String.format("%,.0f", config.individualTier2Max)}",
                                rate = "${config.individualTier2ProfitPct}% / Klaim",
                                isActive = wheelDeposit >= config.individualTier2Min && wheelDeposit < config.individualTier3Min
                            )
                            TierRow(
                                title = "Tier 3 Individual VIP",
                                range = "> Rp ${String.format("%,.0f", config.individualTier3Min)}",
                                rate = "${config.individualTier3ProfitPct}% / Klaim",
                                isActive = wheelDeposit >= config.individualTier3Min
                            )
                        }
                    }
                }
            }

            // --- SUMMARY REPORT CARD ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📄 Laporan Ringkasan Roda Keberuntungan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total Akumulasi Profit Klaim: Rp ${String.format("%,.0f", userAccount?.totalWheelProfitClaimed ?: 0.0)}",
                                fontSize = 11.sp,
                                color = AccentGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showShareReportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_share_wheel_report")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BAGIKAN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // --- CONFIRM DEPOSIT DIALOG ---
    if (showConfirmDepositDialog) {
        val amountToDeposit = depositInput.toDoubleOrNull() ?: 0.0
        AlertDialog(
            onDismissRequest = { showConfirmDepositDialog = false },
            title = {
                Text(
                    text = "🔒 Konfirmasi Deposito Roda Keberuntungan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "PERHATIAN PENTING:",
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Nominal sebesar Rp ${String.format("%,.0f", amountToDeposit)} akan dipindahkan dari saldo dompet utama ke Deposito Roda Keberuntungan.",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "⚠️ Saldo Deposito Roda Keberuntungan bersifat PERMANEN & TIDAK DAPAT DITARIK KEMBALI. Namun saldo ini akan terus memberikan keuntungan harian berulang.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDepositDialog = false
                        viewModel.depositToLuckyWheel(amountToDeposit) {
                            depositInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("SETUJU & DEPOSITO TERKUNCI", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDepositDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    // --- RESULT DIALOG ---
    if (showResultDialog) {
        val taxFee = (lastWonProfit / (1.0 - (config.taxPct / 100.0))) * (config.taxPct / 100.0)
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎉 KEUNTUNGAN DIKLAIM!", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AccentGold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selamat! Anda mendapatkan Multiplier", fontSize = 12.sp, color = TextSecondary)
                    Text("${String.format("%.1f", lastMultiplier)}x Yield Multiplier", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)

                    HorizontalDivider(color = DarkCardBorder)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pajak Claim (${config.taxPct}%):", fontSize = 11.sp, color = TextSecondary)
                        Text("-Rp ${String.format("%,.0f", taxFee)}", fontSize = 11.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bersih Masuk Saldo:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("+Rp ${String.format("%,.0f", lastWonProfit)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showResultDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("MANTAP, TERIMA KASIH", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    // --- SHAREABLE SUMMARY REPORT DIALOG ---
    if (showShareReportDialog) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val shareText = """
            🏆 LAPORAN HASIL RODA KEBERUNTUNGAN 🏆
            ------------------------------------
            Nama: ${userAccount?.fullName ?: "Pengguna"}
            Tipe Akun: ${if (isCompany) "Perusahaan / Corporate" else "Personal / Individual"}
            
            💰 Saldo Deposito Roda (Terkunci): Rp ${String.format("%,.0f", wheelDeposit)}
            📈 Rate Profit Harian: ${currentProfitPct}% per Klaim
            🎉 Total Akumulasi Profit Diklaim: Rp ${String.format("%,.0f", userAccount?.totalWheelProfitClaimed ?: 0.0)}
            ------------------------------------
            Raih profit harian berulang dari Deposito Bebas & Terkunci di Roda Keberuntungan!
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { showShareReportDialog = false },
            title = {
                Text(
                    text = "📄 Ringkasan Kinerja Roda Keberuntungan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald
                )
            },
            text = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    border = BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Pengguna: ${userAccount?.fullName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Kategori: ${if (isCompany) "Corporate" else "Individual"}", fontSize = 11.sp, color = TextSecondary)

                        HorizontalDivider(color = DarkCardBorder)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Deposito Terkunci:", fontSize = 11.sp, color = TextSecondary)
                            Text("Rp ${String.format("%,.0f", wheelDeposit)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rate Yield Harian:", fontSize = 11.sp, color = TextSecondary)
                            Text("${currentProfitPct}% / Klaim", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Profit Klaim:", fontSize = 11.sp, color = TextSecondary)
                            Text("Rp ${String.format("%,.0f", userAccount?.totalWheelProfitClaimed ?: 0.0)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Bagikan Laporan Profit")
                        context.startActivity(shareIntent)
                        showShareReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.testTag("btn_confirm_share_intent")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BAGIKAN LAPORAN", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareReportDialog = false }) {
                    Text("Tutup", color = TextSecondary)
                }
            },
            containerColor = DarkCardSurface
        )
    }
}

@Composable
private fun TierRow(
    title: String,
    range: String,
    rate: String,
    isActive: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) PrimaryEmerald.copy(alpha = 0.15f) else DarkBackground,
        border = BorderStroke(1.dp, if (isActive) PrimaryEmerald else DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = PrimaryEmerald
                        ) {
                            Text("AKTIF", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(range, fontSize = 11.sp, color = TextSecondary)
            }

            Text(
                text = rate,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) PrimaryEmerald else AccentGold
            )
        }
    }
}
