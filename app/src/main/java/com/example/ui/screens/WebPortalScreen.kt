package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import com.example.ui.components.CompanyLogoView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WhatsAppLiveChatFab
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WebPortalScreen(viewModel: MainViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val userInvestments by viewModel.userInvestments.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val packages by viewModel.filteredPackages.collectAsState()
    val brandConfig by viewModel.brandConfig.collectAsState()

    var isDesktopMode by remember { mutableStateOf(true) }
    var urlPath by remember(brandConfig) { mutableStateOf("https://${brandConfig?.companyDomain ?: "investpro.id"}/portal/dashboard") }
    var syncLog by remember { mutableStateOf("WebSocket Connected to Mobile App Database. Listening for real-time state changes...") }

    val formatCurrency = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar Navigation back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    CompanyLogoView(brandConfig = brandConfig, size = 36.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Versi Website Terkoneksi",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Real-time Synchronization dengan Aplikasi Mobile",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isDark = brandConfig?.isDarkMode ?: true
                    IconButton(
                        onClick = { viewModel.toggleThemeMode() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF222634))
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDark) AccentGold else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Desktop / Mobile Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF222634))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDesktopMode) AccentGold else Color.Transparent)
                            .clickable { isDesktopMode = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Computer, contentDescription = null, tint = if (isDesktopMode) Color.Black else TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Web Desktop", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDesktopMode) Color.Black else TextSecondary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (!isDesktopMode) AccentGold else Color.Transparent)
                            .clickable { isDesktopMode = false }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = if (!isDesktopMode) Color.Black else TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Web Mobile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isDesktopMode) Color.Black else TextSecondary)
                        }
                    }
                }
            }
            }

            // Browser Header Frame
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2230))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Window controls & URL bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Red, Yellow, Green window dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // URL address box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color(0xFF131622))
                            .border(1.dp, Color(0xFF2C3246), RoundedCornerShape(17.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = urlPath,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Sync status pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF97316).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFF97316).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLOUDFLARE ONLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                    }
                }
            }

            // Web Portal Canvas Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cloudflare Edge Status Banner Item
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B18)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFF97316),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("☁️ CLOUDFLARE DEPLOYED", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Status: 100% Online Global Edge", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pages: https://${brandConfig?.cloudflarePagesDomain ?: "investpro.pages.dev"} | Worker API: ${brandConfig?.cloudflareWorkerEndpoint ?: "https://api.investpro.workers.dev"}",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }

                            Button(
                                onClick = {
                                    urlPath = if (urlPath.contains("pages.dev")) "https://${brandConfig?.companyDomain ?: "investpro.id"}/portal/dashboard" else "https://${brandConfig?.cloudflarePagesDomain ?: "investpro.pages.dev"}/portal/dashboard"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316), contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Ganti Domain", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                // Web Top Banner Nav
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3142))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AccentGold),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color.Black)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("${(brandConfig?.appName ?: "INVESTPRO").uppercase(Locale.getDefault())} WEB PORTAL", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                        Text("https://${brandConfig?.companyDomain ?: "investpro.id"}", color = AccentGold, fontSize = 11.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        syncLog = "Manual Sync Triggered at ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())} - State 100% In Sync with Mobile App DB"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sinkronkan State", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = syncLog,
                                color = SuccessGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF101820), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                // Web Portfolio Summary Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Saldo Web Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Saldo Web & App", color = TextSecondary, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = formatCurrency.format(userProfile?.balance ?: 0.0),
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { viewModel.navigateTo(ScreenRoute.DEPOSIT) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Deposit", fontSize = 11.sp, color = Color.White)
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.navigateTo(ScreenRoute.WITHDRAWAL) },
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Tarik", fontSize = 11.sp, color = AccentGold)
                                    }
                                }
                            }
                        }

                        // Status Akun Web Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3142))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Tipe Akun Terkoneksi", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (userProfile?.accountType == "COMPANY") Color(0xFF2A1B4E) else Color(0xFF1B3B2B))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (userProfile?.accountType == "COMPANY") "AKUN PERUSAHAAN" else "AKUN PENGGUNA",
                                            color = if (userProfile?.accountType == "COMPANY") Color(0xFFD1B3FF) else SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (userProfile?.accountType == "COMPANY") userProfile?.companyName ?: "-" else userProfile?.name ?: "-",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = userProfile?.email ?: "-",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Active Portfolios on Web
                item {
                    Text("Portofolio Aktif di Web Portal", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (userInvestments.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Belum ada investasi aktif di web portal.", color = TextSecondary, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                                ) {
                                    Text("Pilih Paket Investasi", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            userInvestments.forEach { inv ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3245))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(inv.packageName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Modal: ${formatCurrency.format(inv.capitalAmount)}", color = TextSecondary, fontSize = 12.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("+${formatCurrency.format(inv.accruedProfit)}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${inv.dailyReturnPct}% / hari", color = AccentGold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Web Synchronized Live Audit Transactions
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Riwayat Transaksi Real-time (Synced)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        transactions.take(5).forEach { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (tx.type == "DEPOSIT") SuccessGreen.copy(alpha = 0.2f)
                                                    else AccentGold.copy(alpha = 0.2f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (tx.type == "DEPOSIT") Icons.Default.Add else Icons.Default.SwapHoriz,
                                                contentDescription = null,
                                                tint = if (tx.type == "DEPOSIT") SuccessGreen else AccentGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("${tx.type} (${tx.paymentMethod})", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            Text("Ref: ${tx.referenceId}", color = TextSecondary, fontSize = 10.sp)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${if (tx.type == "DEPOSIT" || tx.type == "PROFIT_CLAIM") "+" else "-"}${formatCurrency.format(tx.amount)}",
                                            color = if (tx.type == "DEPOSIT" || tx.type == "PROFIT_CLAIM") SuccessGreen else Color(0xFFFF6B6B),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(tx.status, color = if (tx.status == "SUCCESS") SuccessGreen else AccentGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
        accountType = userProfile?.accountType ?: "USER",
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 24.dp, end = 20.dp)
    )
}
}
