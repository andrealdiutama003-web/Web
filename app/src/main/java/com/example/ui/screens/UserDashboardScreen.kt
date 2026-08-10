package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import com.example.ui.components.PortfolioChartCard
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import com.example.data.entity.InvestmentPackage
import com.example.data.entity.TransactionRecord
import com.example.data.entity.UserInvestment
import com.example.data.entity.UserProfile
import com.example.data.entity.UserAccount
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.components.BannerSlideCarousel
import com.example.ui.components.WhatsAppLiveChatFab
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserDashboardScreen(
    viewModel: MainViewModel,
    profile: UserProfile?,
    packages: List<InvestmentPackage>,
    userInvestments: List<UserInvestment>,
    transactions: List<TransactionRecord>
) {
    val brandConfig by viewModel.brandConfig.collectAsState()
    val authSession by viewModel.authSession.collectAsState()
    val currentCurrency by viewModel.currentCurrency.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val banners by viewModel.allBanners.collectAsState(initial = emptyList())
    val allUserAccounts by viewModel.allUserAccounts.collectAsState()
    var selectedPackageToInvest by remember { mutableStateOf<InvestmentPackage?>(null) }
    var capitalInput by remember { mutableStateOf("") }
    var showPackageBiometricDialog by remember { mutableStateOf(false) }
    var pendingInvestAmount by remember { mutableStateOf(0.0) }
    val currentAccountType = profile?.accountType ?: "USER"

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }

    val totalActiveInvested = userInvestments
        .filter { it.status == "ACTIVE" }
        .sumOf { it.capitalAmount }

    val totalProfitAccrued = userInvestments.sumOf { it.accruedProfit }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // --- TOP USER PROFILE CARD WITH ACCOUNT SWITCHER ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (currentAccountType == "COMPANY") AccentGold.copy(alpha = 0.2f) else PrimaryEmerald.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (currentAccountType == "COMPANY") Icons.Default.Business else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (currentAccountType == "COMPANY") AccentGold else PrimaryEmerald
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (currentAccountType == "COMPANY") (profile?.companyName ?: "PT Perusahaan") else (profile?.name ?: "Pengguna"),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = profile?.email ?: "investor@investpro.id",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Toggle Account Type & Theme Toggle Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isDark = brandConfig?.isDarkMode ?: true
                            IconButton(
                                onClick = { viewModel.toggleThemeMode() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                                    tint = if (isDark) AccentGold else PrimaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                onClick = {
                                    val nextType = if (currentAccountType == "USER") "COMPANY" else "USER"
                                    viewModel.switchAccountType(nextType)
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = DarkCardSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.testTag("switch_account_type_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentAccountType == "USER") "Ke Perusahaan" else "Ke Personal",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account Type & KYC Status Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentAccountType == "COMPANY") AccentGold.copy(alpha = 0.15f) else PrimaryEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (currentAccountType == "COMPANY") "🏢 Perusahaan" else "👤 Individual",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentAccountType == "COMPANY") AccentGold else PrimaryEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // KYC Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (profile?.kycStatus) {
                                "VERIFIED" -> SuccessGreen.copy(alpha = 0.2f)
                                "PENDING" -> AccentGold.copy(alpha = 0.2f)
                                else -> ErrorRed.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = when (profile?.kycStatus) {
                                    "VERIFIED" -> "🛡️ KYC VERIFIED"
                                    "PENDING" -> "⏳ KYC MENUNGGU"
                                    else -> "⚠️ BELUM KYC"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (profile?.kycStatus) {
                                    "VERIFIED" -> SuccessGreen
                                    "PENDING" -> AccentGold
                                    else -> ErrorRed
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Firebase Auth Email Verified Badge
                        val isEmailVerified = authSession.activeUser?.isEmailVerified ?: profile?.isEmailVerified ?: false
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isEmailVerified) PrimaryEmerald.copy(alpha = 0.2f) else AccentGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isEmailVerified) "🔥 EMAIL VERIFIED" else "📧 EMAIL UNVERIFIED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEmailVerified) PrimaryEmerald else AccentGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Saldo Dompet Balance Display
                    Text(text = "TOTAL SALDO DOMPET ($currentCurrency)", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = com.example.util.GlobalLocaleAndCurrency.formatMoney(profile?.balance ?: 0.0, currentCurrency),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Deposit Button
                        Button(
                            onClick = { viewModel.navigateTo(ScreenRoute.DEPOSIT) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("deposit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Deposit",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Deposit", fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        // Tarik Saldo Button
                        Button(
                            onClick = { viewModel.navigateTo(ScreenRoute.WITHDRAWAL) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("withdraw_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Tarik Saldo",
                                tint = AccentGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Tarik Saldo", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // --- LUCKY WHEEL / RODA KEBERUNTUNGAN BANNER CARD ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(ScreenRoute.LUCKY_WHEEL) }
                    .testTag("card_lucky_wheel_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎡", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Roda Keberuntungan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentGold
                                ) {
                                    Text(
                                        text = "DAILY YIELD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Putar Roda Harian • Deposito Bebas & Terkunci • Individu & Perusahaan",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Buka Roda Keberuntungan",
                        tint = PrimaryEmerald
                    )
                }
            }
        }

        // --- REFERRAL SYSTEM CARD ---
        item {
            var customCodeInput by remember { mutableStateOf("") }
            val userEmail = profile?.email ?: ""
            val myRefCode = profile?.referralCode ?: ""
            val bonusesEarned = profile?.referralBonusesEarned ?: 0.0
            val invitedCount = profile?.referredUsersCount ?: 0
            val context = LocalContext.current

            Card(
                modifier = Modifier.fillMaxWidth().testTag("referral_system_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AccentGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🎁 Program Kemitraan & Referral",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Ajak teman bergabung & nikmati bagi hasil langsung!",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rewards Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardSurface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Bonus Anda (Per Undangan)", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = com.example.util.GlobalLocaleAndCurrency.formatMoney(150000.0, currentCurrency),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Bonus Teman (Selamat Datang)", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = com.example.util.GlobalLocaleAndCurrency.formatMoney(50000.0, currentCurrency),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Referral Code display and Copy button
                    Text(
                        text = "KODE REFERRAL UNIK ANDA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Referral Code Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCardSurface)
                                .border(
                                    width = 1.dp,
                                    color = AccentGold.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (myRefCode.isNotBlank()) {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Referral Code", myRefCode)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Kode referral '$myRefCode' disalin!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = myRefCode.ifBlank { "MEMBUAT KODE..." },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = AccentGold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Copy Button
                        Button(
                            onClick = {
                                if (myRefCode.isNotBlank()) {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Referral Code", myRefCode)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Kode referral '$myRefCode' disalin!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp).testTag("copy_referral_btn")
                        ) {
                            Text(text = "SALIN", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Customize/Generate Custom Code input
                    Text(
                        text = "BUAT KODE CUSTOM ANDA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customCodeInput,
                            onValueChange = { customCodeInput = it },
                            placeholder = { Text("CONTOH: SUKSES77", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(54.dp).testTag("custom_referral_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedLabelColor = AccentGold,
                                unfocusedLabelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (customCodeInput.isNotBlank()) {
                                    viewModel.updateReferralCode(userEmail, customCodeInput)
                                    customCodeInput = ""
                                } else {
                                    Toast.makeText(context, "Ketik kode kustom Anda terlebih dahulu", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(54.dp).testTag("update_referral_btn")
                        ) {
                            Text(text = "SIMPAN", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Statistics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Invited Stat
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = "Total Teman Diundang", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$invitedCount orang",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Total Bonuses Earned Stat
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = "Bonus Total Diperoleh", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = com.example.util.GlobalLocaleAndCurrency.formatMoney(bonusesEarned, currentCurrency),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }

                    // --- NEW DEDICATED REFERRAL CONVERSIONS & EARNINGS BREAKDOWN SECTION ---
                    val convertedReferrals = allUserAccounts.filter {
                        it.referredByCode.isNotBlank() && it.referredByCode.trim().equals(myRefCode.trim(), ignoreCase = true)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DarkCardBorder.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥 Riwayat Referral Terkonversi (${convertedReferrals.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                        
                        Text(
                            text = "Total Kontribusi: " + com.example.util.GlobalLocaleAndCurrency.formatMoney(
                                convertedReferrals.sumOf { refUser ->
                                    transactions.filter { tx ->
                                        (tx.type == "REFERRAL_BONUS" || tx.type == "REFERRAL_COMMISSION") &&
                                        (tx.note.contains(refUser.email, ignoreCase = true) || tx.note.contains(refUser.fullName, ignoreCase = true))
                                    }.sumOf { it.amount }
                                },
                                currentCurrency
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (convertedReferrals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface.copy(alpha = 0.5f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada referral terkonversi. Ajak teman Anda menggunakan kode di atas atau gunakan tombol simulasi di bawah!",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            convertedReferrals.forEach { refUser ->
                                val refEarnings = transactions.filter { tx ->
                                    (tx.type == "REFERRAL_BONUS" || tx.type == "REFERRAL_COMMISSION") &&
                                    (tx.note.contains(refUser.email, ignoreCase = true) || tx.note.contains(refUser.fullName, ignoreCase = true))
                                }.sumOf { it.amount }

                                val joinDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(refUser.createdAt))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkCardSurface)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Avatar with Initials
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryEmerald.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val initials = refUser.fullName.split(" ")
                                                .filter { it.isNotBlank() }
                                                .take(2)
                                                .map { it.first() }
                                                .joinToString("")
                                                .uppercase()
                                            Text(
                                                text = initials.ifBlank { "U" },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryEmerald
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = refUser.fullName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "${refUser.email} • $joinDate",
                                                    fontSize = 10.sp,
                                                    color = TextSecondary
                                                )
                                                
                                                // KYC status pill
                                                val statusColor = when (refUser.kycStatus) {
                                                    "VERIFIED" -> SuccessGreen
                                                    "PENDING" -> AccentGold
                                                    else -> TextSecondary
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(statusColor.copy(alpha = 0.15f))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = refUser.kycStatus,
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = statusColor
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "+ " + com.example.util.GlobalLocaleAndCurrency.formatMoney(refEarnings, currentCurrency),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (refEarnings > 0) SuccessGreen else TextSecondary
                                        )
                                        Text(
                                            text = "Penghasilan",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulation Button
                    Button(
                        onClick = { viewModel.simulateReferralJoin(myRefCode) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold.copy(alpha = 0.12f), contentColor = AccentGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("simulate_new_referral_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AccentGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "⚡ Simulasi Teman Baru Bergabung", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                    }
                }
            }
        }

        // --- INVESTMENT SUMMARY CARDS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Investasi Active
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Aktif Investasi", fontSize = 11.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Rp ${String.format("%,.0f", totalActiveInvested)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // Total Keuntungan Terkumpul
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Total Keuntungan", fontSize = 11.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+Rp ${String.format("%,.0f", totalProfitAccrued)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                }
            }
        }

        // --- SLIDE BANNER CAROUSEL ---
        item {
            BannerSlideCarousel(banners = banners)
        }

        // --- PORTFOLIO GROWTH CHART ---
        item {
            PortfolioChartCard(
                totalValue = (profile?.balance ?: 0.0) + totalActiveInvested + totalProfitAccrued
            )
        }

        // --- ACTIVE INVESTMENTS SECTION WITH LIVE ACCRUAL CLAIM ---
        item {
            Column {
                Text(
                    text = "Portfolio Investasi Aktif",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Klaim simulasi yield harian untuk menambah saldo dompet Anda",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (userInvestments.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum Ada Investasi Aktif",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Pilih paket investasi di bawah untuk mulai menghasilkan keuntungan harian.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        userInvestments.forEach { inv ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = inv.packageName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Modal: Rp ${String.format("%,.0f", inv.capitalAmount)}",
                                                fontSize = 12.sp,
                                                color = PrimaryEmerald,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = AccentGold.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "+${inv.dailyReturnPct}% / Hari",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AccentGold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(DarkCardSurface)
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "Keuntungan Terkumpul", fontSize = 11.sp, color = TextSecondary)
                                            Text(
                                                text = "+Rp ${String.format("%,.0f", inv.accruedProfit)}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.accrueProfit(inv, 1, "daily") },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp).testTag("accrue_profit_daily_${inv.id}")
                                            ) {
                                                Text(text = "+Harian", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }

                                            Button(
                                                onClick = { viewModel.accrueProfit(inv, 30, "monthly") },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp).testTag("accrue_profit_monthly_${inv.id}")
                                            ) {
                                                Text(text = "+Bulanan", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }

                                            Button(
                                                onClick = { viewModel.accrueProfit(inv, 365, "yearly") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp).testTag("accrue_profit_yearly_${inv.id}")
                                            ) {
                                                Text(text = "+Tahunan", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- PAKET INVESTASI SECTION FOR ACTIVE ACCOUNT TYPE ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Katalog Paket Investasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pilihan paket untuk ${if (currentAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        onClick = { viewModel.navigateTo(ScreenRoute.SUPER_ADMIN) },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCardSurface
                    ) {
                        Text(
                            text = "Atur Super Admin",
                            fontSize = 11.sp,
                            color = AccentGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (packages.isEmpty()) {
                    Text(text = "Tidak ada paket yang tersedia untuk tipe akun ini.", color = TextSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        packages.forEach { pkg ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = pkg.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = pkg.description,
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkCardSurface,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(text = "Harian", fontSize = 10.sp, color = TextSecondary)
                                                Text(text = "${pkg.dailyReturnPct}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkCardSurface,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(text = "Bulanan", fontSize = 10.sp, color = TextSecondary)
                                                Text(text = "${pkg.monthlyReturnPct}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkCardSurface,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(text = "Tahunan", fontSize = 10.sp, color = TextSecondary)
                                                Text(text = "${pkg.yearlyReturnPct}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64B5F6))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Modal Min: Rp ${String.format("%,.0f", pkg.minCapital)} | Max: Rp ${String.format("%,.0f", pkg.maxCapital)}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            selectedPackageToInvest = pkg
                                            capitalInput = pkg.minCapital.toLong().toString()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("invest_package_button_${pkg.id}")
                                    ) {
                                        Text(
                                            text = "Investasi Sekarang",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DOWNLOAD APLIKASI MOBIL RESMI (FOR ALL USERS) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("download_app_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "Download Aplikasi Mobile",
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Unduh Aplikasi Android Resmi",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Versi Terbaru v2.4.8 (APK)",
                                    fontSize = 11.sp,
                                    color = AccentGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Untuk pengalaman investasi yang lebih responsif, cepat, aman, dan didukung notifikasi real-time, silakan instal aplikasi mobile kami langsung di handphone Android Anda.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isDownloading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mengunduh file APK...",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${(downloadProgress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = PrimaryEmerald,
                                trackColor = DarkCardSurface
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                if (!isDownloading) {
                                    isDownloading = true
                                    downloadProgress = 0f
                                    scope.launch {
                                        for (i in 1..20) {
                                            delay(100L)
                                            downloadProgress = i / 20f
                                        }
                                        isDownloading = false
                                        Toast.makeText(
                                            context,
                                            "Aplikasi berhasil diunduh! Klik berkas APK di notifikasi untuk menginstal.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("start_download_app_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GetApp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Unduh APK Sekarang",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // --- RECENT TRANSACTIONS LOG ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Transaksi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { viewModel.navigateTo(ScreenRoute.TRANSACTION_HISTORY) },
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardSurface,
                            modifier = Modifier.testTag("btn_see_all_transactions")
                        ) {
                            Text(
                                text = "Lihat Semua",
                                fontSize = 11.sp,
                                color = AccentGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            onClick = { viewModel.navigateTo(ScreenRoute.PAYMENT_GATEWAY_API) },
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardSurface
                        ) {
                            Text(
                                text = "Log Gateway API",
                                fontSize = 11.sp,
                                color = PrimaryEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (transactions.isEmpty()) {
                    Text(text = "Belum ada riwayat transaksi.", color = TextSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        transactions.take(10).forEach { tx ->
                            val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                            val dateStr = dateFormat.format(Date(tx.timestamp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (tx.type) {
                                                        "DEPOSIT" -> PrimaryEmerald.copy(alpha = 0.2f)
                                                        "WITHDRAWAL" -> ErrorRed.copy(alpha = 0.2f)
                                                        "INVESTMENT" -> AccentGold.copy(alpha = 0.2f)
                                                        else -> PrimaryEmerald.copy(alpha = 0.2f)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (tx.type) {
                                                    "DEPOSIT" -> Icons.Default.ArrowDownward
                                                    "WITHDRAWAL" -> Icons.Default.ArrowUpward
                                                    "INVESTMENT" -> Icons.Default.Savings
                                                    else -> Icons.Default.CheckCircle
                                                },
                                                contentDescription = null,
                                                tint = when (tx.type) {
                                                    "DEPOSIT" -> PrimaryEmerald
                                                    "WITHDRAWAL" -> ErrorRed
                                                    "INVESTMENT" -> AccentGold
                                                    else -> SuccessGreen
                                                },
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = when (tx.type) {
                                                    "DEPOSIT" -> "Deposit Saldo"
                                                    "WITHDRAWAL" -> "Tarik Saldo"
                                                    "INVESTMENT" -> "Investasi Paket"
                                                    else -> "Hasil Dividend"
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "$dateStr • ${tx.paymentMethod}",
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        val prefix = if (tx.type == "DEPOSIT" || tx.type == "PROFIT_CLAIM") "+" else "-"
                                        Text(
                                            text = "$prefix Rp ${String.format("%,.0f", tx.amount)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (tx.type == "DEPOSIT" || tx.type == "PROFIT_CLAIM") SuccessGreen else TextPrimary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (tx.status == "SUCCESS") SuccessGreen.copy(alpha = 0.15f) else AccentGold.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = tx.status,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (tx.status == "SUCCESS") SuccessGreen else AccentGold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- WhatsApp Live Chat Floating Action Button ---
    WhatsAppLiveChatFab(
        brandConfig = brandConfig,
        accountType = currentAccountType,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 24.dp, end = 20.dp)
    )
}

    // --- DIALOG INVESTASI MODAL INPUT ---
    selectedPackageToInvest?.let { pkg ->
        AlertDialog(
            onDismissRequest = { selectedPackageToInvest = null },
            title = {
                Text(text = "Investasi di ${pkg.name}", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "Modal diatur Super Admin: Min Rp ${String.format("%,.0f", pkg.minCapital)}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = capitalInput,
                        onValueChange = { capitalInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Jumlah Modal Investasi (Rp)") },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invest_capital_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val enteredCapital = capitalInput.toDoubleOrNull() ?: 0.0
                    val dailyGain = enteredCapital * (pkg.dailyReturnPct / 100.0)
                    val monthlyGain = enteredCapital * (pkg.monthlyReturnPct / 100.0)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkCardSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Estimasi Hasil Yield:", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "Harian: +Rp ${String.format("%,.0f", dailyGain)} (${pkg.dailyReturnPct}%)", fontSize = 12.sp, color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                            Text(text = "Bulanan: +Rp ${String.format("%,.0f", monthlyGain)} (${pkg.monthlyReturnPct}%)", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingInvestAmount = capitalInput.toDoubleOrNull() ?: 0.0
                        showPackageBiometricDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.testTag("confirm_invest_button")
                ) {
                    Text(text = "Konfirmasi Investasi", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPackageToInvest = null }) {
                    Text(text = "Batal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showPackageBiometricDialog && selectedPackageToInvest != null) {
        val pkg = selectedPackageToInvest!!
        com.example.ui.components.HighSecurityBiometricVerificationDialog(
            transactionTitle = "Otorisasi Biometrik Pembelian Paket",
            transactionDetails = "Diperlukan verifikasi sidik jari/wajah untuk mengaktifkan paket ${pkg.name} dengan modal Rp ${String.format("%,.0f", pendingInvestAmount)}",
            userPin = profile?.transactionPin ?: "123456",
            onVerificationSuccess = {
                showPackageBiometricDialog = false
                viewModel.investInPackage(pkg, pendingInvestAmount)
                selectedPackageToInvest = null
            },
            onDismiss = { showPackageBiometricDialog = false }
        )
    }
}
