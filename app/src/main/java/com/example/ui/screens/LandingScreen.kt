package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import com.example.ui.components.CompanyLogoView
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.InvestmentPackage
import com.example.data.entity.UserProfile
import com.example.ui.components.WhatsAppLiveChatFab
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldLight
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.PrimaryEmeraldDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun LandingScreen(
    viewModel: MainViewModel,
    userProfile: UserProfile?,
    packages: List<InvestmentPackage>
) {
    val brandConfig by viewModel.brandConfig.collectAsState()
    var selectedTabAccountType by remember { mutableStateOf("USER") } // "USER" or "COMPANY"
    var calcCapitalInput by remember { mutableStateOf("10000000") } // Default Rp 10.000.000

    val appTitle = brandConfig?.appName ?: "InvestPro"

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- HERO BANNER & APP HEADER ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_invest_1786158449305),
                        contentDescription = "$appTitle Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color(0xFF0A111E).copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header Brand
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CompanyLogoView(brandConfig = brandConfig, size = 40.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = appTitle,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                        // Super Admin Direct Access Badge & Theme Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isDark = brandConfig?.isDarkMode ?: true
                            IconButton(
                                onClick = { viewModel.toggleThemeMode() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                                    tint = if (isDark) AccentGold else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                onClick = { viewModel.navigateTo(ScreenRoute.SUPER_ADMIN) },
                                shape = RoundedCornerShape(20.dp),
                                color = AccentGold.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold),
                                modifier = Modifier.testTag("super_admin_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Super Admin",
                                        tint = AccentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Super Admin",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                }
                            }
                        }
                    }

                    // Hero Text & Call to Action
                    Column {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryEmerald.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "⚡ Platform Investasi Otomatis & Transparan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryEmerald,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Kembangkan Modal dengan Keuntungan Harian & Bulanan",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Dukungan Akun Pengguna & Akun Perusahaan dengan penyesuaian modal langsung dari Super Admin.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("goto_dashboard_button")
                            ) {
                                Text(
                                    text = "Mulai Investasi",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(ScreenRoute.DEPOSIT) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("deposit_shortcut_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Deposit", color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // --- ACCOUNT TYPE TAB SELECTOR ---
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Pilih Kategori Akun Investment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTabAccountType == "USER") PrimaryEmerald else Color.Transparent)
                            .clickable { selectedTabAccountType = "USER" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedTabAccountType == "USER") Color.Black else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Akun Pengguna",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (selectedTabAccountType == "USER") Color.Black else TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTabAccountType == "COMPANY") AccentGold else Color.Transparent)
                            .clickable { selectedTabAccountType = "COMPANY" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = if (selectedTabAccountType == "COMPANY") Color.Black else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Akun Perusahaan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (selectedTabAccountType == "COMPANY") Color.Black else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // --- INVESTMENT YIELD CALCULATOR ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = PrimaryEmerald
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kalkulator Simulasi Keuntungan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkCardSurface
                        ) {
                            Text(
                                text = if (selectedTabAccountType == "COMPANY") "Rate Perusahaan" else "Rate Pengguna",
                                fontSize = 11.sp,
                                color = AccentGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Masukkan Jumlah Modal Investasi (Rp):",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = calcCapitalInput,
                        onValueChange = { calcCapitalInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkCardSurface,
                            unfocusedContainerColor = DarkCardSurface
                        ),
                        leadingIcon = {
                            Text(
                                text = "Rp",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val inputAmount = calcCapitalInput.toDoubleOrNull() ?: 0.0
                    val currentPackages = packages.filter { it.accountType == selectedTabAccountType || it.accountType == "ALL" }
                    val samplePkg = currentPackages.firstOrNull() ?: InvestmentPackage(
                        name = "Paket Standard",
                        description = "",
                        accountType = selectedTabAccountType,
                        minCapital = 100000.0,
                        maxCapital = 1000000000.0,
                        dailyReturnPct = if (selectedTabAccountType == "COMPANY") 1.5 else 0.8,
                        monthlyReturnPct = if (selectedTabAccountType == "COMPANY") 45.0 else 24.0,
                        yearlyReturnPct = if (selectedTabAccountType == "COMPANY") 540.0 else 288.0
                    )

                    val dailyGain = inputAmount * (samplePkg.dailyReturnPct / 100.0)
                    val monthlyGain = inputAmount * (samplePkg.monthlyReturnPct / 100.0)
                    val yearlyGain = inputAmount * (samplePkg.yearlyReturnPct / 100.0)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Daily Box
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Harian (${samplePkg.dailyReturnPct}%)", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", dailyGain)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        }

                        // Monthly Box
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Bulanan (${samplePkg.monthlyReturnPct}%)", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", monthlyGain)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }

                        // Yearly Box
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Tahunan (${samplePkg.yearlyReturnPct}%)", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", yearlyGain)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64B5F6)
                            )
                        }
                    }
                }
            }
        }

        // --- PAKET INVESTASI CONFIG BY SUPER ADMIN ---
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Paket Investasi ${if (selectedTabAccountType == "COMPANY") "Perusahaan" else "Pengguna"}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Modal & Persen Keuntungan Diatur Super Admin",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = "Lihat Semua",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald,
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val availablePkgs = packages.filter { it.accountType == selectedTabAccountType || it.accountType == "ALL" }
                if (availablePkgs.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Text(
                            text = "Belum ada paket investasi disetel Super Admin.",
                            modifier = Modifier.padding(20.dp),
                            color = TextSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        availablePkgs.forEach { pkg ->
                            PackageItemCard(
                                pkg = pkg,
                                onInvestClick = {
                                    // Set account type & navigate to dashboard to invest
                                    viewModel.switchAccountType(pkg.accountType)
                                    viewModel.navigateTo(ScreenRoute.USER_DASHBOARD)
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- PAYMENT GATEWAY API & SECURITY BANNER ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = PrimaryEmerald
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Integrasi Payment Gateway API",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Deposit & Tarik Saldo Otomatis Instant Callback",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardSurface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Auto Deposit QRIS", fontSize = 11.sp, color = TextPrimary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardSurface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Auto Disburse Bank", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.navigateTo(ScreenRoute.PAYMENT_GATEWAY_API) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pengaturan & Tester API Payment Gateway",
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // --- WhatsApp Live Chat Floating Action Button ---
    WhatsAppLiveChatFab(
        brandConfig = brandConfig,
        accountType = selectedTabAccountType,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 24.dp, end = 20.dp)
    )
}
}

@Composable
fun PackageItemCard(
    pkg: InvestmentPackage,
    onInvestClick: () -> Unit
) {
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
                        text = if (pkg.accountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna",
                        fontSize = 12.sp,
                        color = if (pkg.accountType == "COMPANY") AccentGold else PrimaryEmerald,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
                ) {
                    Text(
                        text = "+${pkg.dailyReturnPct}% / Hari",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pkg.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Modal Min (Super Admin)", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "Rp ${String.format("%,.0f", pkg.minCapital)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Estimasi Bulanan", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "${pkg.monthlyReturnPct}% ROI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onInvestClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pkg.accountType == "COMPANY") AccentGold else PrimaryEmerald
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
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
