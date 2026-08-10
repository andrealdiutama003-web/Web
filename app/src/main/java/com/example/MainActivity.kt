package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ui.components.HighSecurityBiometricVerificationDialog
import com.example.ui.theme.DarkBackground
import com.example.util.GlobalLocaleAndCurrency
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.NotificationItem
import com.example.ui.components.NotificationBellButton
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.RealtimeNotificationBanner
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DepositScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.PaymentGatewayApiScreen
import com.example.ui.screens.StaffDashboardScreen
import com.example.ui.screens.SuperAdminScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.screens.WithdrawalScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.screens.KycSubmissionScreen
import com.example.ui.screens.LuckyWheelScreen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.InvestProTheme
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute
import kotlinx.coroutines.flow.collectLatest

class MainActivity : androidx.fragment.app.FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val brandConfig by viewModel.brandConfig.collectAsStateWithLifecycle()
            val isDark = brandConfig?.isDarkMode ?: true
            com.example.ui.theme.ThemeManager.isLightMode = !isDark
            InvestProTheme(darkTheme = isDark) {
                val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
                val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                val allPackages by viewModel.allPackages.collectAsStateWithLifecycle()
                val filteredPackages by viewModel.filteredPackages.collectAsStateWithLifecycle()
                val userInvestments by viewModel.userInvestments.collectAsStateWithLifecycle()
                val transactions by viewModel.transactions.collectAsStateWithLifecycle()
                val gatewayConfig by viewModel.paymentGatewayConfig.collectAsStateWithLifecycle()
                val authSession by viewModel.authSession.collectAsStateWithLifecycle()
                val currentStaff by viewModel.currentStaffAccount.collectAsStateWithLifecycle()
                val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
                val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
                val notifications by viewModel.notifications.collectAsStateWithLifecycle()
                val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()

                var showCurrencyLangDialog by remember { mutableStateOf(false) }
                var showKycDialog by remember { mutableStateOf(false) }
                var showBiometricDialog by remember { mutableStateOf(false) }
                var showPinDialog by remember { mutableStateOf(false) }
                var showNotificationCenterDialog by remember { mutableStateOf(false) }
                var activeBannerNotification by remember { mutableStateOf<NotificationItem?>(null) }

                // 5-Minute Background Security Auto-Lock
                var isAppLocked by remember { mutableStateOf(false) }
                var showAutoLockBiometricDialog by remember { mutableStateOf(false) }
                var backgroundTimestamp by remember { mutableLongStateOf(0L) }

                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_STOP -> {
                                if (authSession.isLoggedIn) {
                                    backgroundTimestamp = System.currentTimeMillis()
                                }
                            }
                            Lifecycle.Event.ON_START -> {
                                if (authSession.isLoggedIn && backgroundTimestamp > 0L) {
                                    val durationMs = System.currentTimeMillis() - backgroundTimestamp
                                    val fiveMinutesMs = 5 * 60 * 1000L // 5 minutes (300,000 ms)
                                    if (durationMs >= fiveMinutesMs) {
                                        isAppLocked = true
                                        showAutoLockBiometricDialog = true
                                    }
                                    backgroundTimestamp = 0L
                                }
                            }
                            else -> {}
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collectLatest { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                LaunchedEffect(notifications) {
                    val unreadNewest = notifications.firstOrNull { !it.isRead }
                    if (unreadNewest != null && activeBannerNotification?.id != unreadNewest.id) {
                        activeBannerNotification = unreadNewest
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        // Global Top Session Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .background(DarkSurface)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (authSession.role) {
                                                    "SUPER_ADMIN" -> AccentGold
                                                    "STAFF" -> AccentGold
                                                    "COMPANY" -> PrimaryEmerald
                                                    else -> PrimaryEmerald
                                                }.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (authSession.role) {
                                                "SUPER_ADMIN" -> Icons.Default.AdminPanelSettings
                                                "STAFF" -> Icons.Default.SupportAgent
                                                "COMPANY" -> Icons.Default.Business
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            tint = if (authSession.role == "SUPER_ADMIN" || authSession.role == "STAFF") AccentGold else PrimaryEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Column {
                                        Text(
                                            text = when (authSession.role) {
                                                "SUPER_ADMIN" -> "SUPER ADMIN: ${authSession.displayName}"
                                                "STAFF" -> "STAFF [${currentStaff?.staffCode ?: "STF"}]: ${authSession.displayName}"
                                                "COMPANY" -> "PERUSAHAAN: ${authSession.displayName}"
                                                else -> "INDIVIDUAL: ${authSession.displayName}"
                                            },
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${GlobalLocaleAndCurrency.t("active_access", currentLanguage)}: ${authSession.role}",
                                            color = if (authSession.isLoggedIn) SuccessGreen else TextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    // Real-time Notification Bell Button
                                    NotificationBellButton(
                                        unreadCount = unreadNotificationCount,
                                        onClick = { showNotificationCenterDialog = true }
                                    )

                                    // Direct 1-Tap Language Toggle Switch Button (ID <-> EN)
                                    Button(
                                        onClick = {
                                            val nextLang = if (currentLanguage == "ID") "EN" else "ID"
                                            viewModel.setLanguage(nextLang)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald.copy(alpha = 0.2f)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_top_language_toggle")
                                    ) {
                                        val flag = if (currentLanguage == "ID") "🇮🇩 ID" else "🇺🇸 EN"
                                        Text(flag, fontSize = 10.sp, color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                                    }

                                    // Currency & Language Selector Button Modal
                                    Button(
                                        onClick = { showCurrencyLangDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_top_currency_lang")
                                    ) {
                                        Text("🌐 $currentCurrency", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }

                                    // Security & KYC Button
                                    Button(
                                        onClick = { viewModel.navigateTo(ScreenRoute.KYC_SUBMISSION) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_top_kyc_sec")
                                    ) {
                                        Text("🛡️ KYC", fontSize = 10.sp, color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.navigateTo(ScreenRoute.AUTH) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_top_login_switch")
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = AccentGold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(GlobalLocaleAndCurrency.t("nav_login", currentLanguage), fontSize = 10.sp, color = TextPrimary)
                                    }

                                    Button(
                                        onClick = {
                                            isAppLocked = true
                                            showAutoLockBiometricDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_top_sim_lock")
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = AccentGold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Kunci 5m", fontSize = 10.sp, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = DarkSurface,
                            contentColor = TextPrimary,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentRoute == ScreenRoute.LANDING,
                                onClick = { viewModel.navigateTo(ScreenRoute.LANDING) },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text(GlobalLocaleAndCurrency.t("nav_home", currentLanguage), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = PrimaryEmerald,
                                    indicatorColor = PrimaryEmerald,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_landing")
                            )

                            NavigationBarItem(
                                selected = currentRoute == ScreenRoute.USER_DASHBOARD,
                                onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) },
                                icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Dashboard") },
                                label = { Text(GlobalLocaleAndCurrency.t("nav_dashboard", currentLanguage), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = PrimaryEmerald,
                                    indicatorColor = PrimaryEmerald,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_dashboard")
                            )

                            NavigationBarItem(
                                selected = currentRoute == ScreenRoute.AUTH,
                                onClick = { viewModel.navigateTo(ScreenRoute.AUTH) },
                                icon = { Icon(Icons.Default.Lock, contentDescription = "Auth") },
                                label = { Text(GlobalLocaleAndCurrency.t("nav_login", currentLanguage), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = AccentGold,
                                    indicatorColor = AccentGold,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_auth")
                            )

                            NavigationBarItem(
                                selected = currentRoute == ScreenRoute.STAFF_DASHBOARD,
                                onClick = { viewModel.navigateTo(ScreenRoute.STAFF_DASHBOARD) },
                                icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Staff") },
                                label = { Text(GlobalLocaleAndCurrency.t("nav_staff", currentLanguage), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = AccentGold,
                                    indicatorColor = AccentGold,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_staff")
                            )

                            NavigationBarItem(
                                selected = currentRoute == ScreenRoute.SUPER_ADMIN,
                                onClick = { viewModel.navigateTo(ScreenRoute.SUPER_ADMIN) },
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                                label = { Text(GlobalLocaleAndCurrency.t("nav_admin", currentLanguage), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = AccentGold,
                                    indicatorColor = AccentGold,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_item_admin")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Realtime Pop-up Banner Alert
                            RealtimeNotificationBanner(
                                latestNotification = activeBannerNotification,
                                onOpenCenter = {
                                    activeBannerNotification = null
                                    showNotificationCenterDialog = true
                                },
                                onDismiss = {
                                    activeBannerNotification = null
                                }
                            )

                            Box(modifier = Modifier.weight(1f)) {
                                when (currentRoute) {
                                    ScreenRoute.LANDING -> LandingScreen(
                                        viewModel = viewModel,
                                        userProfile = userProfile,
                                        packages = allPackages
                                    )
                                    ScreenRoute.AUTH -> AuthScreen(
                                        viewModel = viewModel
                                    )
                                    ScreenRoute.USER_DASHBOARD -> UserDashboardScreen(
                                        viewModel = viewModel,
                                        profile = userProfile,
                                        packages = filteredPackages,
                                        userInvestments = userInvestments,
                                        transactions = transactions
                                    )
                                    ScreenRoute.STAFF_DASHBOARD -> StaffDashboardScreen(
                                        viewModel = viewModel,
                                        staff = currentStaff,
                                        transactions = transactions
                                    )
                                    ScreenRoute.SUPER_ADMIN -> SuperAdminScreen(
                                        viewModel = viewModel,
                                        packages = allPackages,
                                        transactions = transactions
                                    )
                                    ScreenRoute.DEPOSIT -> DepositScreen(
                                        viewModel = viewModel,
                                        profile = userProfile
                                    )
                                    ScreenRoute.WITHDRAWAL -> WithdrawalScreen(
                                        viewModel = viewModel,
                                        profile = userProfile
                                    )
                                    ScreenRoute.PAYMENT_GATEWAY_API -> PaymentGatewayApiScreen(
                                        viewModel = viewModel,
                                        gatewayConfig = gatewayConfig
                                    )
                                    ScreenRoute.WEB_PORTAL -> WebPortalScreen(
                                        viewModel = viewModel
                                    )
                                    ScreenRoute.TRANSACTION_HISTORY -> TransactionHistoryScreen(
                                        viewModel = viewModel,
                                        transactions = transactions
                                    )
                                    ScreenRoute.KYC_SUBMISSION -> KycSubmissionScreen(
                                        viewModel = viewModel
                                    )
                                    ScreenRoute.LUCKY_WHEEL -> LuckyWheelScreen(
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }

                        // GLOBAL NOTIFICATION CENTER DIALOG
                        if (showNotificationCenterDialog) {
                            NotificationCenterDialog(
                                notifications = notifications,
                                onDismiss = { showNotificationCenterDialog = false },
                                onMarkAsRead = { id -> viewModel.markNotificationAsRead(id) },
                                onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                                onDeleteNotification = { id -> viewModel.deleteNotification(id) },
                                onClearAll = { viewModel.clearAllNotifications() }
                            )
                        }

                        // GLOBAL SECURITY & LOCALIZATION DIALOGS
                        if (showCurrencyLangDialog) {
                            com.example.ui.components.CurrencyAndLanguageDialog(
                                currentCurrency = currentCurrency,
                                currentLanguage = currentLanguage,
                                onCurrencySelected = { code -> viewModel.setCurrency(code) },
                                onLanguageSelected = { lang -> viewModel.setLanguage(lang) },
                                onDismiss = { showCurrencyLangDialog = false }
                            )
                        }

                        if (showKycDialog) {
                            com.example.ui.components.KycScanDialog(
                                currentNik = authSession.activeUser?.ktpNumber ?: (userProfile?.ktpNumber ?: ""),
                                onKycSubmitted = { nik -> viewModel.submitKyc(nik) },
                                onDismiss = { showKycDialog = false }
                            )
                        }

                        if (showBiometricDialog) {
                            com.example.ui.components.BiometricSettingsDialog(
                                isFingerprintEnabled = authSession.activeUser?.isFingerprintEnabled ?: (userProfile?.isFingerprintEnabled ?: false),
                                isFaceAuthEnabled = authSession.activeUser?.isFaceAuthEnabled ?: (userProfile?.isFaceAuthEnabled ?: false),
                                onToggleFingerprint = { enabled -> viewModel.toggleFingerprint(enabled) },
                                onToggleFaceAuth = { enabled -> viewModel.toggleFaceAuth(enabled) },
                                onDismiss = { showBiometricDialog = false }
                            )
                        }

                        if (showPinDialog) {
                            com.example.ui.components.TransactionPinDialog(
                                userPin = authSession.activeUser?.transactionPin ?: "123456",
                                onPinSuccess = { showPinDialog = false },
                                onDismiss = { showPinDialog = false }
                            )
                        }

                        // 5-MINUTE BACKGROUND AUTO-LOCK OVERLAY & RE-AUTHENTICATION DIALOG
                        if (isAppLocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkBackground)
                                    .clickable(enabled = false) {},
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .testTag("app_auto_locked_overlay")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryEmerald.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "App Locked",
                                            tint = PrimaryEmerald,
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "🔒 Aplikasi Terkunci Otomatis",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Aplikasi telah berada di latar belakang selama lebih dari 5 menit. Otentikasi biometrik diperlukan untuk membuka kembali aplikasi secara aman.",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = { showAutoLockBiometricDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("btn_unlock_app_biometric")
                                    ) {
                                        Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("BUKA KUNCI BIOMETRIK", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        if (showAutoLockBiometricDialog) {
                            HighSecurityBiometricVerificationDialog(
                                transactionTitle = "Buka Kunci Sesi Aplikasi",
                                transactionDetails = "Aplikasi terkunci otomatis setelah di latar belakang > 5 menit. Pindai sidik jari / wajah / PIN untuk melanjutkan.",
                                userPin = authSession.activeUser?.transactionPin ?: "123456",
                                onVerificationSuccess = {
                                    showAutoLockBiometricDialog = false
                                    isAppLocked = false
                                },
                                onDismiss = {
                                    showAutoLockBiometricDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
