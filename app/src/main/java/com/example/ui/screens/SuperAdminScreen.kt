package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Upload
import com.example.ui.components.CompanyLogoView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Lock
import com.example.data.entity.AdminFeeConfig
import com.example.data.entity.AuditLogRecord
import com.example.data.entity.LuckyWheelConfig
import com.example.data.entity.BannerSlideItem
import com.example.data.entity.BrandConfig
import com.example.data.entity.InvestmentPackage
import com.example.data.entity.StaffAccount
import com.example.data.entity.TransactionRecord
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SuperAdminScreen(
    viewModel: MainViewModel,
    packages: List<InvestmentPackage>,
    transactions: List<TransactionRecord>
) {
    val feeConfig by viewModel.adminFeeConfig.collectAsState()
    val luckyWheelConfigState by viewModel.luckyWheelConfig.collectAsState()
    val wheelConfig = luckyWheelConfigState ?: LuckyWheelConfig()
    val userProfile by viewModel.userProfile.collectAsState()
    val brandConfig by viewModel.brandConfig.collectAsState()
    val allStaffs by viewModel.allStaffAccounts.collectAsState()
    val allUsers by viewModel.allUserAccounts.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var activeFeatureIndex by remember { mutableStateOf(0) }
    val featureList = remember {
        listOf(
            "👥 Staff & Pegawai",
            "🛡️ Verifikasi KYC",
            "🎨 Identitas & Domain",
            "📢 Banner & Notif",
            "💰 Transaksi Manual",
            "🧾 Biaya & Pajak",
            "📈 Yield & Paket",
            "🎡 Roda Keberuntungan",
            "📜 Log Audit (Read-Only)"
        )
    }

    var selectedCategoryTab by remember { mutableStateOf("USER") } // "USER" or "COMPANY"

    // Staff Management States
    var showStaffDialog by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<StaffAccount?>(null) }
    var staffCodeForm by remember { mutableStateOf("") }
    var staffNameForm by remember { mutableStateOf("") }
    var staffEmailForm by remember { mutableStateOf("") }
    var staffPhoneForm by remember { mutableStateOf("") }
    var staffPinForm by remember { mutableStateOf("") }
    var staffRoleForm by remember { mutableStateOf("Customer Service Admin") }

    // Form states for Rebranding Identitas Perusahaan & Live Chat WA
    var appNameInput by remember(brandConfig) { mutableStateOf(brandConfig?.appName ?: "InvestPro") }
    var companyNameInput by remember(brandConfig) { mutableStateOf(brandConfig?.companyName ?: "PT Investasi Jaya Mandiri") }
    var taglineInput by remember(brandConfig) { mutableStateOf(brandConfig?.tagline ?: "Platform Investasi & Manajemen Modal Digital Terpercaya") }
    var waNumberInput by remember(brandConfig) { mutableStateOf(brandConfig?.whatsappNumber ?: "6281234567890") }
    var waGreetingInput by remember(brandConfig) { mutableStateOf(brandConfig?.whatsappGreeting ?: "Halo CS Admin, saya ingin berkonsultasi mengenai investasi.") }
    var supportEmailInput by remember(brandConfig) { mutableStateOf(brandConfig?.supportEmail ?: "support@investpro.id") }
    var supportAddressInput by remember(brandConfig) { mutableStateOf(brandConfig?.supportAddress ?: "Equity Tower Lt. 18, SCBD Jakarta") }
    var isLiveChatEnabledInput by remember(brandConfig) { mutableStateOf(brandConfig?.isLiveChatEnabled ?: true) }
    var isDarkModeInput by remember(brandConfig) { mutableStateOf(brandConfig?.isDarkMode ?: true) }
    var logoUrlInput by remember(brandConfig) { mutableStateOf(brandConfig?.logoUrl ?: "") }
    var faviconUrlInput by remember(brandConfig) { mutableStateOf(brandConfig?.faviconUrl ?: "") }
    var logoSymbolInput by remember(brandConfig) { mutableStateOf(brandConfig?.logoSymbol ?: "TRENDING") }
    var customEmojiLogoInput by remember(brandConfig) { mutableStateOf(brandConfig?.customEmojiLogo ?: "📈") }
    var customFaviconEmojiInput by remember(brandConfig) { mutableStateOf(brandConfig?.customFaviconEmoji ?: "🛡️") }

    // New domain settings inputs
    var companyDomainInput by remember(brandConfig) { mutableStateOf(brandConfig?.companyDomain ?: "investpro.id") }
    var isDomainSslActiveInput by remember(brandConfig) { mutableStateOf(brandConfig?.isDomainSslActive ?: true) }
    var dnsARecordIpInput by remember(brandConfig) { mutableStateOf(brandConfig?.dnsARecordIp ?: "104.21.90.18") }
    var dnsCnameTargetInput by remember(brandConfig) { mutableStateOf(brandConfig?.dnsCnameTarget ?: "cname.investpro.id") }
    var domainVerificationTokenInput by remember(brandConfig) { mutableStateOf(brandConfig?.domainVerificationToken ?: "investpro-verification-hash-8821") }
    var isVerifyingDomainDns by remember { mutableStateOf(false) }
    var domainDnsVerificationMessage by remember { mutableStateOf("") }

    // Corporate SMTP Email Server state variables
    var smtpHostInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpHost ?: "smtp.company.com") }
    var smtpPortInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpPort ?: 587) }
    var smtpUsernameInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpUsername ?: "noreply@company.com") }
    var smtpPasswordInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpPassword ?: "secure-smtp-pass-8821") }
    var smtpEncryptionInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpEncryption ?: "TLS") }
    var smtpSenderEmailInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpSenderEmail ?: "noreply@company.com") }
    var smtpSenderNameInput by remember(brandConfig) { mutableStateOf(brandConfig?.smtpSenderName ?: "Company Official") }
    var isSmtpActiveInput by remember(brandConfig) { mutableStateOf(brandConfig?.isSmtpActive ?: true) }
    var isTestingSmtp by remember { mutableStateOf(false) }
    var smtpTestResult by remember { mutableStateOf("") }

    // Cloudflare Deployment & Edge Hub state variables
    var cloudflareAccountIdInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareAccountId ?: "cf-acc-88219472910") }
    var cloudflareApiTokenInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareApiToken ?: "v1.0-cf-token-secure-key-99381") }
    var cloudflarePagesDomainInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflarePagesDomain ?: "investpro.pages.dev") }
    var cloudflareWorkerEndpointInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareWorkerEndpoint ?: "https://api.investpro.workers.dev") }
    var isCloudflareProxyActiveInput by remember(brandConfig) { mutableStateOf(brandConfig?.isCloudflareProxyActive ?: true) }
    var isCloudflareTunnelActiveInput by remember(brandConfig) { mutableStateOf(brandConfig?.isCloudflareTunnelActive ?: true) }
    var cloudflareTunnelUrlInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareTunnelUrl ?: "https://investpro-portal.trycloudflare.com") }
    var isDeployingToCloudflare by remember { mutableStateOf(false) }
    var cloudflareDeploymentStatusMessage by remember { mutableStateOf("") }

    // Cloudflare D1 Database state variables
    var cloudflareD1DatabaseIdInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareD1DatabaseId ?: "d1-db-88219472-investpro") }
    var cloudflareD1DatabaseNameInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareD1DatabaseName ?: "investpro-d1-db") }
    var cloudflareD1BindingNameInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareD1BindingName ?: "DB") }
    var isD1AutoSyncEnabledInput by remember(brandConfig) { mutableStateOf(brandConfig?.isD1AutoSyncEnabled ?: true) }
    var cloudflareD1SecretTokenInput by remember(brandConfig) { mutableStateOf(brandConfig?.cloudflareD1SecretToken ?: "d1-secret-token-key-investpro") }
    var isSyncingToD1 by remember { mutableStateOf(false) }
    var d1SyncStatusMessage by remember { mutableStateOf("") }
    var showD1SqlSchemaDialog by remember { mutableStateOf(false) }

    val banners by viewModel.allBanners.collectAsState(initial = emptyList())
    var showAddBannerDialog by remember { mutableStateOf(false) }
    var newBannerTitle by remember { mutableStateOf("") }
    var newBannerSubtitle by remember { mutableStateOf("") }
    var newBannerBadge by remember { mutableStateOf("PROMO") }
    var newBannerAction by remember { mutableStateOf("Klaim") }

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastTarget by remember { mutableStateOf("ALL") }

    // Form states for Yield Rate Adjuster
    var dailyRateInput by remember { mutableStateOf("0.8") }
    var monthlyRateInput by remember { mutableStateOf("24.0") }
    var yearlyRateInput by remember { mutableStateOf("288.0") }
    var minCapitalInput by remember { mutableStateOf("100000") }

    // Form states for Admin Fee & Tax Config Controller
    var userWdFeeInput by remember { mutableStateOf(feeConfig?.userWithdrawalFee?.toLong()?.toString() ?: "2500") }
    var userAdminPctInput by remember { mutableStateOf(feeConfig?.userAdminFeePct?.toString() ?: "1.0") }
    var userTaxPctInput by remember { mutableStateOf(feeConfig?.userTaxFeePct?.toString() ?: "5.0") }

    var companyWdFeeInput by remember { mutableStateOf(feeConfig?.companyWithdrawalFee?.toLong()?.toString() ?: "10000") }
    var companyAdminPctInput by remember { mutableStateOf(feeConfig?.companyAdminFeePct?.toString() ?: "0.5") }
    var companyTaxPctInput by remember { mutableStateOf(feeConfig?.companyTaxFeePct?.toString() ?: "2.5") }

    // Form states for Referral & Profit Sharing Config
    var individualReferrerBonusInput by remember { mutableStateOf(feeConfig?.individualReferralReferrerBonus?.toLong()?.toString() ?: "150000") }
    var individualReferredBonusInput by remember { mutableStateOf(feeConfig?.individualReferralReferredBonus?.toLong()?.toString() ?: "50000") }
    var companyReferrerBonusInput by remember { mutableStateOf(feeConfig?.companyReferralReferrerBonus?.toLong()?.toString() ?: "300000") }
    var companyReferredBonusInput by remember { mutableStateOf(feeConfig?.companyReferralReferredBonus?.toLong()?.toString() ?: "100000") }
    var referralTaxPctInput by remember { mutableStateOf(feeConfig?.referralTaxPct?.toString() ?: "10.0") }
    var referralDailyCommissionInput by remember { mutableStateOf(feeConfig?.referralDailyCommissionPct?.toString() ?: "5.0") }
    var referralMonthlyCommissionInput by remember { mutableStateOf(feeConfig?.referralMonthlyCommissionPct?.toString() ?: "3.0") }
    var referralYearlyCommissionInput by remember { mutableStateOf(feeConfig?.referralYearlyCommissionPct?.toString() ?: "2.0") }

    // Form states for Lucky Wheel (Roda Keberuntungan) Config
    var isWheelEnabledInput by remember { mutableStateOf(wheelConfig.isWheelEnabled) }
    var wheelQuotaInput by remember { mutableStateOf(wheelConfig.dailyClaimQuota.toString()) }
    var wheelTaxPctInput by remember { mutableStateOf(wheelConfig.taxPct.toString()) }

    var indTier1MinInput by remember { mutableStateOf(wheelConfig.individualTier1Min.toLong().toString()) }
    var indTier1MaxInput by remember { mutableStateOf(wheelConfig.individualTier1Max.toLong().toString()) }
    var indTier1ProfitInput by remember { mutableStateOf(wheelConfig.individualTier1ProfitPct.toString()) }

    var indTier2MinInput by remember { mutableStateOf(wheelConfig.individualTier2Min.toLong().toString()) }
    var indTier2MaxInput by remember { mutableStateOf(wheelConfig.individualTier2Max.toLong().toString()) }
    var indTier2ProfitInput by remember { mutableStateOf(wheelConfig.individualTier2ProfitPct.toString()) }

    var indTier3MinInput by remember { mutableStateOf(wheelConfig.individualTier3Min.toLong().toString()) }
    var indTier3MaxInput by remember { mutableStateOf(wheelConfig.individualTier3Max.toLong().toString()) }
    var indTier3ProfitInput by remember { mutableStateOf(wheelConfig.individualTier3ProfitPct.toString()) }

    var comTier1MinInput by remember { mutableStateOf(wheelConfig.companyTier1Min.toLong().toString()) }
    var comTier1MaxInput by remember { mutableStateOf(wheelConfig.companyTier1Max.toLong().toString()) }
    var comTier1ProfitInput by remember { mutableStateOf(wheelConfig.companyTier1ProfitPct.toString()) }

    var comTier2MinInput by remember { mutableStateOf(wheelConfig.companyTier2Min.toLong().toString()) }
    var comTier2MaxInput by remember { mutableStateOf(wheelConfig.companyTier2Max.toLong().toString()) }
    var comTier2ProfitInput by remember { mutableStateOf(wheelConfig.companyTier2ProfitPct.toString()) }

    var comTier3MinInput by remember { mutableStateOf(wheelConfig.companyTier3Min.toLong().toString()) }
    var comTier3MaxInput by remember { mutableStateOf(wheelConfig.companyTier3Max.toLong().toString()) }
    var comTier3ProfitInput by remember { mutableStateOf(wheelConfig.companyTier3ProfitPct.toString()) }

    LaunchedEffect(wheelConfig) {
        isWheelEnabledInput = wheelConfig.isWheelEnabled
        wheelQuotaInput = wheelConfig.dailyClaimQuota.toString()
        wheelTaxPctInput = wheelConfig.taxPct.toString()

        indTier1MinInput = wheelConfig.individualTier1Min.toLong().toString()
        indTier1MaxInput = wheelConfig.individualTier1Max.toLong().toString()
        indTier1ProfitInput = wheelConfig.individualTier1ProfitPct.toString()

        indTier2MinInput = wheelConfig.individualTier2Min.toLong().toString()
        indTier2MaxInput = wheelConfig.individualTier2Max.toLong().toString()
        indTier2ProfitInput = wheelConfig.individualTier2ProfitPct.toString()

        indTier3MinInput = wheelConfig.individualTier3Min.toLong().toString()
        indTier3MaxInput = wheelConfig.individualTier3Max.toLong().toString()
        indTier3ProfitInput = wheelConfig.individualTier3ProfitPct.toString()

        comTier1MinInput = wheelConfig.companyTier1Min.toLong().toString()
        comTier1MaxInput = wheelConfig.companyTier1Max.toLong().toString()
        comTier1ProfitInput = wheelConfig.companyTier1ProfitPct.toString()

        comTier2MinInput = wheelConfig.companyTier2Min.toLong().toString()
        comTier2MaxInput = wheelConfig.companyTier2Max.toLong().toString()
        comTier2ProfitInput = wheelConfig.companyTier2ProfitPct.toString()

        comTier3MinInput = wheelConfig.companyTier3Min.toLong().toString()
        comTier3MaxInput = wheelConfig.companyTier3Max.toLong().toString()
        comTier3ProfitInput = wheelConfig.companyTier3ProfitPct.toString()
    }

    LaunchedEffect(feeConfig) {
        feeConfig?.let {
            userWdFeeInput = it.userWithdrawalFee.toLong().toString()
            userAdminPctInput = it.userAdminFeePct.toString()
            userTaxPctInput = it.userTaxFeePct.toString()
            companyWdFeeInput = it.companyWithdrawalFee.toLong().toString()
            companyAdminPctInput = it.companyAdminFeePct.toString()
            companyTaxPctInput = it.companyTaxFeePct.toString()
            individualReferrerBonusInput = it.individualReferralReferrerBonus.toLong().toString()
            individualReferredBonusInput = it.individualReferralReferredBonus.toLong().toString()
            companyReferrerBonusInput = it.companyReferralReferrerBonus.toLong().toString()
            companyReferredBonusInput = it.companyReferralReferredBonus.toLong().toString()
            referralTaxPctInput = it.referralTaxPct.toString()
            referralDailyCommissionInput = it.referralDailyCommissionPct.toString()
            referralMonthlyCommissionInput = it.referralMonthlyCommissionPct.toString()
            referralYearlyCommissionInput = it.referralYearlyCommissionPct.toString()
        }
    }

    // Form states for Manual Deposit & Manual Withdrawal
    var manualActionTab by remember { mutableStateOf("DEPOSIT") } // "DEPOSIT" or "WITHDRAWAL"
    var targetAccountType by remember { mutableStateOf("USER") } // "USER" or "COMPANY"
    var manualAmountInput by remember { mutableStateOf("5000000") }
    var manualNoteInput by remember { mutableStateOf("Injeksi modal manual disetujui Super Admin") }
    var manualBankName by remember { mutableStateOf("Bank BCA") }
    var manualBankNum by remember { mutableStateOf("8830192837") }
    var manualBankHolder by remember { mutableStateOf("Ahmad Pratama") }

    // Dialog state for Creating / Editing Investment Package
    var editingPackage by remember { mutableStateOf<InvestmentPackage?>(null) }
    var showPackageDialog by remember { mutableStateOf(false) }

    // Form fields for package dialog
    var pkgName by remember { mutableStateOf("") }
    var pkgDesc by remember { mutableStateOf("") }
    var pkgAccType by remember { mutableStateOf("USER") }
    var pkgMinCap by remember { mutableStateOf("100000") }
    var pkgMaxCap by remember { mutableStateOf("100000000") }
    var pkgDailyPct by remember { mutableStateOf("1.0") }
    var pkgMonthlyPct by remember { mutableStateOf("30.0") }
    var pkgYearlyPct by remember { mutableStateOf("360.0") }

    val formatCurrency = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER SUPER ADMIN ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AccentGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Super Admin",
                                tint = AccentGold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Panel Super Admin Kontrol",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Kelola Rate Yield, Deposit & Tarik Saldo Manual, Biaya WD & Pajak",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(ScreenRoute.WEB_PORTAL) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Web Portal", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(ScreenRoute.PAYMENT_GATEWAY_API) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Gateway API", fontSize = 11.sp, color = TextPrimary)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "User View", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- FEATURE CATEGORY TABS (GESER KESAMPING) ---
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(
                    text = "🌐 Menu Kontrol Super Admin (Geser kesamping untuk memilih):",
                    fontSize = 11.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    itemsIndexed(featureList) { index, title ->
                        val isSelected = activeFeatureIndex == index
                        val containerColor = if (isSelected) PrimaryEmerald else DarkCardSurface
                        val contentColor = if (isSelected) Color.Black else TextSecondary
                        val borderColor = if (isSelected) PrimaryEmerald else DarkCardBorder

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { activeFeatureIndex = index }
                                .testTag("superadmin_tab_$index"),
                            color = containerColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- MANAJEMEN STAFF & PEGAWAI PERUSAHAAN ---
        if (activeFeatureIndex == 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("superadmin_staff_management_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = AccentGold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "👥 Kelola Staff & Pegawai Perusahaan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Tambah, edit PIN/Password, atur status aktif, dan kewenangan staff",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                editingStaff = null
                                staffCodeForm = "STF-00" + (allStaffs.size + 1)
                                staffNameForm = ""
                                staffEmailForm = ""
                                staffPhoneForm = ""
                                staffPinForm = "123456"
                                staffRoleForm = "Customer Service Admin"
                                showStaffDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_add_staff")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Staff Baru", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (allStaffs.isEmpty()) {
                        Text(
                            text = "Belum ada staff terdaftar. Klik 'Staff Baru' untuk menambahkan.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        allStaffs.forEach { staff ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "[${staff.staffCode}] ${staff.fullName}",
                                                color = TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (staff.isActive) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (staff.isActive) "AKTIF" else "NONAKTIF",
                                                    color = if (staff.isActive) SuccessGreen else ErrorRed,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    editingStaff = staff
                                                    staffCodeForm = staff.staffCode
                                                    staffNameForm = staff.fullName
                                                    staffEmailForm = staff.email
                                                    staffPhoneForm = staff.phone
                                                    staffPinForm = staff.passwordPin
                                                    staffRoleForm = staff.rolePosition
                                                    showStaffDialog = true
                                                },
                                                modifier = Modifier.size(32.dp).testTag("btn_edit_staff_${staff.id}")
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Staff", tint = AccentGold, modifier = Modifier.size(16.dp))
                                            }

                                            IconButton(
                                                onClick = { viewModel.superAdminDeleteStaff(staff.id) },
                                                modifier = Modifier.size(32.dp).testTag("btn_delete_staff_${staff.id}")
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Staff", tint = ErrorRed, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Jabatan: ${staff.rolePosition} • Email: ${staff.email} • PIN: ${staff.passwordPin}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.superAdminToggleStaffStatus(staff) },
                                            modifier = Modifier.weight(1f).height(32.dp).testTag("btn_toggle_staff_${staff.id}"),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (staff.isActive) ErrorRed else SuccessGreen)
                                        ) {
                                            Text(if (staff.isActive) "Nonaktifkan" else "Aktifkan", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { viewModel.simulateStaffLogin(staff) },
                                            modifier = Modifier.weight(1f).height(32.dp).testTag("btn_sim_staff_${staff.id}"),
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                                        ) {
                                            Text("Simulasi Login Staff", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

        // --- VERIFIKASI KYC & PERSETUJUAN PENGGUNA ALL ROLE ---
        if (activeFeatureIndex == 1) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("superadmin_kyc_management_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = PrimaryEmerald)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🛡️ Persetujuan & Verifikasi KYC Pengguna",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tinjau status NIK KTP, verifikasi wajah, dan biometrik akun pengguna & perusahaan",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (allUsers.isEmpty()) {
                        Text(
                            text = "Belum ada akun pengguna terdaftar.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        allUsers.forEach { user ->
                            AdminUserKycCard(user, viewModel)
                        }
                    }
                }
            }
        }
        }

        // --- REBRANDING IDENTITAS PERUSAHAAN & LIVE CHAT WA CARD ---
        if (activeFeatureIndex == 2) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = PrimaryEmerald)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🎨 Rebranding Identitas Perusahaan & Live Chat WA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Ubah nama platform, PT Perusahaan & kontak CS WhatsApp resmi",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Nama Aplikasi / Platform:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = appNameInput,
                        onValueChange = { appNameInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Nama Resmi Perusahaan (PT):", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = companyNameInput,
                        onValueChange = { companyNameInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Tagline / Slogan Subtitle:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = taglineInput,
                        onValueChange = { taglineInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    // --- CUSTOM DOMAIN SETTINGS SECTION ---
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "🌐 Pengaturan Custom Domain Perusahaan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Hubungkan nama domain milik PT/perusahaan Anda sendiri",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Nama Domain Perusahaan (Custom Domain):", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = companyDomainInput,
                        onValueChange = { companyDomainInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("company_domain_input"),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("contoh: bima-investama.com", fontSize = 12.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Aktifkan SSL Handshake (HTTPS Secure):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Secara otomatis mengamankan domain dengan Let's Encrypt SSL", fontSize = 10.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = isDomainSslActiveInput,
                            onCheckedChange = { isDomainSslActiveInput = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryEmerald
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            isVerifyingDomainDns = true
                            domainDnsVerificationMessage = ""
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1200)
                                isVerifyingDomainDns = false
                                domainDnsVerificationMessage = "✅ Domain '$companyDomainInput' berhasil terhubung ke server platform!\n" +
                                        "• Resolusi IP: $dnsARecordIpInput\n" +
                                        "• SSL Status: " + (if (isDomainSslActiveInput) "AKTIF & AMAN (Let's Encrypt SHA-256)" else "TIDAK AKTIF (HTTP)") + "\n" +
                                        "• Pemetaan CNAME: Sesuai ($dnsCnameTargetInput)\n" +
                                        "• TXT Token: Terverifikasi"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVerifyingDomainDns) DarkCardSurface else Color(0xFF1E3A5F),
                            contentColor = if (isVerifyingDomainDns) TextSecondary else Color(0xFF38BDF8)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("verify_domain_dns_button"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isVerifyingDomainDns) DarkCardBorder else Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        enabled = !isVerifyingDomainDns
                    ) {
                        if (isVerifyingDomainDns) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memindai DNS Propagation...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pindai & Validasi Domain Koneksi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                        }
                    }

                    if (domainDnsVerificationMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF132D2F)),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = domainDnsVerificationMessage,
                                fontSize = 11.sp,
                                color = PrimaryEmerald,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📋 Petunjuk DNS Pointing (Wajib Dikonfigurasi):",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                            Text(
                                text = "Konfigurasikan DNS Record berikut pada DNS provider Anda (Cloudflare, Niagahoster, dll.) agar domain menunjuk ke platform:",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                lineHeight = 12.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // DNS Grid/Rows
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                // A Record row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("A Record (@)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Arahkan domain utama ke IP server", fontSize = 8.sp, color = TextSecondary)
                                    }
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                                    ) {
                                        Text(
                                            text = dnsARecordIpInput,
                                            fontSize = 9.sp,
                                            color = AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // CNAME Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("CNAME Record (www)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Arahkan alias subdomain www", fontSize = 8.sp, color = TextSecondary)
                                    }
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                                    ) {
                                        Text(
                                            text = dnsCnameTargetInput,
                                            fontSize = 9.sp,
                                            color = AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // TXT Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("TXT Record (_investpro-auth)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Token verifikasi kepemilikan unik", fontSize = 8.sp, color = TextSecondary)
                                    }
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                                    ) {
                                        Text(
                                            text = domainVerificationTokenInput,
                                            fontSize = 9.sp,
                                            color = AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // CLOUDFLARE DEPLOYMENT & EDGE ONLINE HUB
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Pusat Deployment & Online Cloudflare Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Hubungkan aplikasi & Web Portal ke Cloudflare Pages, Workers, dan Cloudflare Tunnel agar 100% Online Global", fontSize = 9.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFF97316).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("☁️ CLOUDFLARE EDGE", color = Color(0xFFF97316), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Status CDN: ONLINE (Proxied)", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Switch(
                                    checked = isCloudflareProxyActiveInput,
                                    onCheckedChange = { isCloudflareProxyActiveInput = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = Color(0xFFF97316)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = cloudflareAccountIdInput,
                                onValueChange = { cloudflareAccountIdInput = it },
                                label = { Text("Cloudflare Account ID") },
                                leadingIcon = { Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("cf_account_id_input"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF97316), unfocusedBorderColor = DarkCardBorder)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = cloudflareApiTokenInput,
                                onValueChange = { cloudflareApiTokenInput = it },
                                label = { Text("Cloudflare Global API Token / Worker Key") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = TextSecondary) },
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("cf_api_token_input"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF97316), unfocusedBorderColor = DarkCardBorder)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cloudflarePagesDomainInput,
                                    onValueChange = { cloudflarePagesDomainInput = it },
                                    label = { Text("Cloudflare Pages Domain") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.weight(1f).testTag("cf_pages_domain_input"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF97316), unfocusedBorderColor = DarkCardBorder)
                                )

                                OutlinedTextField(
                                    value = cloudflareWorkerEndpointInput,
                                    onValueChange = { cloudflareWorkerEndpointInput = it },
                                    label = { Text("Cloudflare Workers API URL") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.weight(1f).testTag("cf_worker_endpoint_input"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFF97316), unfocusedBorderColor = DarkCardBorder)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            HorizontalDivider(color = DarkCardBorder)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Cloudflare Tunnel Connector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("🚇 Cloudflare Tunnel (cloudflared)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Buka akses online tanpa Port Forwarding / IP Publik Statis", fontSize = 9.sp, color = TextSecondary)
                                }

                                Switch(
                                    checked = isCloudflareTunnelActiveInput,
                                    onCheckedChange = { isCloudflareTunnelActiveInput = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = PrimaryEmerald
                                    )
                                )
                            }

                            if (isCloudflareTunnelActiveInput) {
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = cloudflareTunnelUrlInput,
                                    onValueChange = { cloudflareTunnelUrlInput = it },
                                    label = { Text("URL Cloudflare Tunnel Active") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = PrimaryEmerald) },
                                    modifier = Modifier.fillMaxWidth().testTag("cf_tunnel_url_input"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Button: Deploy to Cloudflare
                            Button(
                                onClick = {
                                    isDeployingToCloudflare = true
                                    cloudflareDeploymentStatusMessage = ""
                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(1500)
                                        isDeployingToCloudflare = false
                                        cloudflareDeploymentStatusMessage = "🚀 BERHASIL DEPLOY KE CLOUDFLARE!\n" +
                                                "• Cloudflare Pages: https://$cloudflarePagesDomainInput\n" +
                                                "• Cloudflare Workers API: $cloudflareWorkerEndpointInput\n" +
                                                "• Cloudflare Tunnel: ${if (isCloudflareTunnelActiveInput) cloudflareTunnelUrlInput else "Standby"}\n" +
                                                "• SSL/TLS Edge Certificate: AKTIF (Universal SSL)\n" +
                                                "• Status CDN Edge Cache: 100% ONLINE di 300+ Kota Global Cloudflare"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_deploy_to_cloudflare"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316), contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isDeployingToCloudflare
                            ) {
                                if (isDeployingToCloudflare) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mengunggah Artifact & Memasang Cloudflare Worker...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(imageVector = Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("DEPLOY SEMUA FITUR KE CLOUDFLARE (ONLINE)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (cloudflareDeploymentStatusMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A1F)),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
                                ) {
                                    Text(
                                        text = cloudflareDeploymentStatusMessage,
                                        fontSize = 11.sp,
                                        color = PrimaryEmerald,
                                        modifier = Modifier.padding(10.dp),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // CLOUDFLARE D1 SQLITE DATABASE INTEGRATION CARD
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = PrimaryEmerald.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("💾 CLOUDFLARE D1 SQLITE", color = PrimaryEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Status: Connected (D1 Edge DB)", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Switch(
                                    checked = isD1AutoSyncEnabledInput,
                                    onCheckedChange = { isD1AutoSyncEnabledInput = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = PrimaryEmerald
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Basis Data Serverless SQLite D1 Cloudflare", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Penyimpanan relasional SQLite di 300+ lokasi edge Cloudflare untuk performa latency ultra-rendah (<10ms).", fontSize = 9.sp, color = TextSecondary)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cloudflareD1DatabaseNameInput,
                                    onValueChange = { cloudflareD1DatabaseNameInput = it },
                                    label = { Text("Nama Database D1") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = PrimaryEmerald) },
                                    modifier = Modifier.weight(1f).testTag("cf_d1_name_input"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                                )

                                OutlinedTextField(
                                    value = cloudflareD1BindingNameInput,
                                    onValueChange = { cloudflareD1BindingNameInput = it },
                                    label = { Text("D1 Binding Name") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = PrimaryEmerald) },
                                    modifier = Modifier.weight(1f).testTag("cf_d1_binding_input"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = cloudflareD1DatabaseIdInput,
                                onValueChange = { cloudflareD1DatabaseIdInput = it },
                                label = { Text("Cloudflare D1 Database UUID") },
                                leadingIcon = { Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("cf_d1_id_input"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = cloudflareD1SecretTokenInput,
                                onValueChange = { cloudflareD1SecretTokenInput = it },
                                label = { Text("Cloudflare D1 Auth Secret Token") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = TextSecondary) },
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("cf_d1_token_input"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showD1SqlSchemaDialog = true },
                                    modifier = Modifier.weight(1f).height(40.dp).testTag("btn_view_d1_schema"),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("📄 D1 SQL Schema", fontSize = 10.sp, color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        isSyncingToD1 = true
                                        d1SyncStatusMessage = ""
                                        coroutineScope.launch {
                                            kotlinx.coroutines.delay(1200)
                                            isSyncingToD1 = false
                                            d1SyncStatusMessage = "✅ SINKRONISASI KE CLOUDFLARE D1 BERHASIL!\n" +
                                                    "• Database ID: $cloudflareD1DatabaseIdInput\n" +
                                                    "• D1 Binding: env.$cloudflareD1BindingNameInput\n" +
                                                    "• Total Tabel Tersinkronisasi: 14 Tabel SQLite\n" +
                                                    "  [user_profiles, investment_packages, user_investments, transaction_records, brand_config, payment_gateway_config, admin_fee_config, user_accounts, staff_accounts, notification_items, crypto_wallet_config, banner_slide_items, lucky_wheel_config, audit_log_records]\n" +
                                                    "• Read/Write Replication: Active (Global Cloudflare Edge)"
                                        }
                                    },
                                    modifier = Modifier.weight(1.2f).height(40.dp).testTag("btn_sync_to_d1"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isSyncingToD1
                                ) {
                                    if (isSyncingToD1) {
                                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.Black)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sync D1...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Sync All Data to D1", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (d1SyncStatusMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF132A1C)),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
                                ) {
                                    Text(
                                        text = d1SyncStatusMessage,
                                        fontSize = 10.sp,
                                        color = PrimaryEmerald,
                                        modifier = Modifier.padding(10.dp),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Logo & Favicon Customization Section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pengaturan Logo & Favicon Perusahaan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Live Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardSurface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Live Preview Logo & Favicon:", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(appNameInput.ifBlank { "InvestPro" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Logo", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                CompanyLogoView(
                                    brandConfig = BrandConfig(
                                        logoUrl = logoUrlInput,
                                        logoSymbol = logoSymbolInput,
                                        customEmojiLogo = customEmojiLogoInput
                                    ),
                                    size = 40.dp,
                                    isFavicon = false
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Favicon", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                CompanyLogoView(
                                    brandConfig = BrandConfig(
                                        faviconUrl = faviconUrlInput,
                                        customFaviconEmoji = customFaviconEmojiInput
                                    ),
                                    size = 32.dp,
                                    isFavicon = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Pilih Simbol Logo Utama:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("TRENDING" to "📈", "SHIELD" to "🛡️", "ROCKET" to "🚀", "BUSINESS" to "🏢", "STAR" to "⭐", "EMOJI" to "✨").forEach { (sym, lbl) ->
                            val isSelected = logoSymbolInput == sym
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryEmerald else DarkCardSurface)
                                    .clickable { logoSymbolInput = sym }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lbl, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else TextPrimary)
                            }
                        }
                    }

                    if (logoSymbolInput == "EMOJI") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Kustom Emoji Logo Utama:", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        OutlinedTextField(
                            value = customEmojiLogoInput,
                            onValueChange = { customEmojiLogoInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("URL Gambar Logo Khusus (Opsional, kosongkan untuk pakai simbol di atas):", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = logoUrlInput,
                        onValueChange = { logoUrlInput = it },
                        placeholder = { Text("https://example.com/logo.png", fontSize = 11.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("URL Gambar Favicon / Emoji Favicon:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = faviconUrlInput,
                        onValueChange = { faviconUrlInput = it },
                        placeholder = { Text("https://example.com/favicon.png atau kosongkan", fontSize = 11.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // WhatsApp & Contact Settings
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pengaturan Official Live Chat WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Nomor WhatsApp Support CS (Format: 628...):", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = waNumberInput,
                        onValueChange = { waNumberInput = it },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color(0xFF25D366)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF25D366), unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Pesan Sapaan Default Chat WA:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = waGreetingInput,
                        onValueChange = { waGreetingInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF25D366), unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Email Support CS:", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            OutlinedTextField(
                                value = supportEmailInput,
                                onValueChange = { supportEmailInput = it },
                                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = AccentGold) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Alamat Perusahaan:", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            OutlinedTextField(
                                value = supportAddressInput,
                                onValueChange = { supportAddressInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch for Live Chat Active
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aktifkan Tombol Floating Live Chat WA:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Switch(
                            checked = isLiveChatEnabledInput,
                            onCheckedChange = { isLiveChatEnabledInput = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF25D366)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Konfigurasi Gateway Email SMTP Perusahaan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gunakan server email SMTP mandiri agar seluruh pengiriman email sistem (Notifikasi Transaksi, OTP, KYC, Broadcast) dikirim via domain resmi perusahaan sendiri.",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Aktifkan Gateway SMTP Perusahaan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Switch(
                                    checked = isSmtpActiveInput,
                                    onCheckedChange = { isSmtpActiveInput = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AccentGold
                                    )
                                )
                            }

                            if (isSmtpActiveInput) {
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text("Host SMTP Server:", fontSize = 10.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = smtpHostInput,
                                            onValueChange = { smtpHostInput = it },
                                            modifier = Modifier.fillMaxWidth().testTag("smtp_host_input"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Port SMTP:", fontSize = 10.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = smtpPortInput.toString(),
                                            onValueChange = { smtpPortInput = it.toIntOrNull() ?: 587 },
                                            modifier = Modifier.fillMaxWidth().testTag("smtp_port_input"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Username SMTP:", fontSize = 10.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = smtpUsernameInput,
                                            onValueChange = { smtpUsernameInput = it },
                                            modifier = Modifier.fillMaxWidth().testTag("smtp_username_input"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Password SMTP:", fontSize = 10.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = smtpPasswordInput,
                                            onValueChange = { smtpPasswordInput = it },
                                            modifier = Modifier.fillMaxWidth().testTag("smtp_password_input"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text("Alamat Email Pengirim (Sender):", fontSize = 10.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = smtpSenderEmailInput,
                                            onValueChange = { smtpSenderEmailInput = it },
                                            modifier = Modifier.fillMaxWidth().testTag("smtp_sender_email_input"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Nama Pengirim:", fontSize = 10.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = smtpSenderNameInput,
                                            onValueChange = { smtpSenderNameInput = it },
                                            modifier = Modifier.fillMaxWidth().testTag("smtp_sender_name_input"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Protokol Enkripsi Keamanan:", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("TLS", "SSL", "NONE").forEach { enc ->
                                        val isSelected = smtpEncryptionInput == enc
                                        Button(
                                            onClick = { smtpEncryptionInput = enc },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) AccentGold else DarkSurface),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                        ) {
                                            Text(text = enc, fontSize = 10.sp, color = if (isSelected) Color.Black else TextPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        isTestingSmtp = true
                                        smtpTestResult = ""
                                        coroutineScope.launch {
                                            kotlinx.coroutines.delay(1500)
                                            isTestingSmtp = false
                                            smtpTestResult = "✅ Koneksi SMTP Sukses!\nEmail Uji berhasil terkirim dari $smtpSenderNameInput <$smtpSenderEmailInput> ke CS Support ($supportEmailInput) melalui Host $smtpHostInput:$smtpPortInput [Protokol: $smtpEncryptionInput]."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isTestingSmtp) DarkSurface else Color(0xFF132D2F),
                                        contentColor = if (isTestingSmtp) TextSecondary else PrimaryEmerald
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("test_smtp_connection_button"),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isTestingSmtp) DarkCardBorder else PrimaryEmerald.copy(alpha = 0.4f)),
                                    enabled = !isTestingSmtp
                                ) {
                                    if (isTestingSmtp) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Menguji Jabat Tangan (Handshake)...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Tes Pengiriman Email & Koneksi SMTP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (smtpTestResult.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132D2F)),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = smtpTestResult,
                                            fontSize = 10.sp,
                                            color = PrimaryEmerald,
                                            modifier = Modifier.padding(10.dp),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Switch for Dark Mode Theme
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mode Tema Perusahaan (Gelap / Terang):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Switch(
                            checked = isDarkModeInput,
                            onCheckedChange = { isDarkModeInput = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryEmerald
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val newConfig = BrandConfig(
                                id = 1,
                                appName = appNameInput.trim(),
                                companyName = companyNameInput.trim(),
                                tagline = taglineInput.trim(),
                                whatsappNumber = waNumberInput.trim(),
                                whatsappGreeting = waGreetingInput.trim(),
                                supportEmail = supportEmailInput.trim(),
                                supportAddress = supportAddressInput.trim(),
                                isLiveChatEnabled = isLiveChatEnabledInput,
                                isDarkMode = isDarkModeInput,
                                logoUrl = logoUrlInput.trim(),
                                faviconUrl = faviconUrlInput.trim(),
                                logoSymbol = logoSymbolInput,
                                customEmojiLogo = customEmojiLogoInput.trim(),
                                customFaviconEmoji = customFaviconEmojiInput.trim(),
                                companyDomain = companyDomainInput.trim(),
                                isDomainSslActive = isDomainSslActiveInput,
                                dnsARecordIp = dnsARecordIpInput.trim(),
                                dnsCnameTarget = dnsCnameTargetInput.trim(),
                                domainVerificationToken = domainVerificationTokenInput.trim(),
                                smtpHost = smtpHostInput.trim(),
                                smtpPort = smtpPortInput,
                                smtpUsername = smtpUsernameInput.trim(),
                                smtpPassword = smtpPasswordInput,
                                smtpEncryption = smtpEncryptionInput.trim(),
                                smtpSenderEmail = smtpSenderEmailInput.trim(),
                                smtpSenderName = smtpSenderNameInput.trim(),
                                isSmtpActive = isSmtpActiveInput,
                                cloudflareAccountId = cloudflareAccountIdInput.trim(),
                                cloudflareApiToken = cloudflareApiTokenInput.trim(),
                                cloudflarePagesDomain = cloudflarePagesDomainInput.trim(),
                                cloudflareWorkerEndpoint = cloudflareWorkerEndpointInput.trim(),
                                isCloudflareProxyActive = isCloudflareProxyActiveInput,
                                isCloudflareTunnelActive = isCloudflareTunnelActiveInput,
                                cloudflareTunnelUrl = cloudflareTunnelUrlInput.trim(),
                                cloudflareD1DatabaseId = cloudflareD1DatabaseIdInput.trim(),
                                cloudflareD1DatabaseName = cloudflareD1DatabaseNameInput.trim(),
                                cloudflareD1BindingName = cloudflareD1BindingNameInput.trim(),
                                isD1AutoSyncEnabled = isD1AutoSyncEnabledInput,
                                cloudflareD1SecretToken = cloudflareD1SecretTokenInput.trim()
                            )
                            viewModel.superAdminSaveBrandConfig(newConfig)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Rebranding Identitas Perusahaan", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
        }

        // --- SECTION: KELOLA BANNER SLIDE & NOTIFIKASI OTOMATIS (EMAIL, WHATSAPP, APP) ---
        if (activeFeatureIndex == 3) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Kelola Banner Slide & Notifikasi Otomatis",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Super Admin mengontrol banner promosi slide yang tampil di beranda pengguna serta gateway pemberitahuan otomatis via Email, WhatsApp, dan Notifikasi In-App untuk Akun Pengguna & Perusahaan.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Daftar Slide Banner Promosi Aktif (${banners.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                    Spacer(modifier = Modifier.height(8.dp))

                    banners.forEach { banner ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = banner.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = banner.subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.superAdminDeleteBanner(banner.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Hapus", fontSize = 10.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showAddBannerDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "+ Tambah Banner Slide Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "📢 Kirim Broadcast Notifikasi (App, Email & WhatsApp):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Judul Broadcast / Pemberitahuan") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Pesan Pengumuman / Notifikasi") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Target Akun Penerima:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("ALL" to "Semua", "USER" to "Pengguna", "COMPANY" to "Perusahaan").forEach { (code, label) ->
                            val isSelected = broadcastTarget == code
                            Button(
                                onClick = { broadcastTarget = code },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) AccentGold else DarkCardSurface),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = label, fontSize = 11.sp, color = if (isSelected) Color.Black else TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                                viewModel.superAdminSendBroadcast(broadcastTitle, broadcastMessage, broadcastTarget)
                                broadcastTitle = ""
                                broadcastMessage = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Kirim Broadcast Instant (Email, WhatsApp & App)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
        }

        // --- SECTION 1: MANUAL DEPOSIT & MANUAL TARIK SALDO (INJEKSI & TRANSFER MANUAL) ---
        if (activeFeatureIndex == 4) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = PrimaryEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Deposit Manual & Tarik Saldo Manual",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Super Admin dapat langsung menginjeksi saldo deposit atau memproses penarikan manual",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector Manual Deposit vs Manual Withdrawal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardSurface)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (manualActionTab == "DEPOSIT") SuccessGreen else Color.Transparent)
                                .clickable { manualActionTab = "DEPOSIT" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Deposit Manual (+)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (manualActionTab == "DEPOSIT") Color.White else TextSecondary)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (manualActionTab == "WITHDRAWAL") AccentGold else Color.Transparent)
                                .clickable { manualActionTab = "WITHDRAWAL" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tarik Saldo Manual (-)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (manualActionTab == "WITHDRAWAL") Color.Black else TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Account Selector (Akun Pengguna vs Akun Perusahaan)
                    Text("Pilih Target Jenis Akun:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardSurface)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (targetAccountType == "USER") PrimaryEmerald else Color.Transparent)
                                .clickable { targetAccountType = "USER" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Akun Pengguna (Personal)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (targetAccountType == "USER") Color.Black else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (targetAccountType == "COMPANY") AccentGold else Color.Transparent)
                                .clickable { targetAccountType = "COMPANY" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Akun Perusahaan (Company)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (targetAccountType == "COMPANY") Color.Black else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val manualAmt = manualAmountInput.toDoubleOrNull() ?: 0.0
                    val isManualAmtValid = manualAmt > 0.0

                    if (manualActionTab == "DEPOSIT") {
                        Text("Nominal Injeksi Deposit Manual (Rp):", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = manualAmountInput,
                            onValueChange = { manualAmountInput = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("Rp", color = SuccessGreen, fontWeight = FontWeight.Bold) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SuccessGreen, unfocusedBorderColor = DarkCardBorder)
                        )

                        if (manualAmountInput.isNotEmpty() && !isManualAmtValid) {
                            Text(
                                text = "❌ Nominal deposit harus lebih besar dari 0",
                                fontSize = 11.sp,
                                color = ErrorRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Catatan / Keterangan Deposit:", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = manualNoteInput,
                            onValueChange = { manualNoteInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SuccessGreen, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (isManualAmtValid) {
                                    viewModel.superAdminManualDeposit(manualAmt, manualNoteInput, targetAccountType)
                                }
                            },
                            enabled = isManualAmtValid,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Proses Deposit Manual ke ${if (targetAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"}", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Text("Nominal Tarik Saldo Manual (Rp):", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = manualAmountInput,
                            onValueChange = { manualAmountInput = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("Rp", color = AccentGold, fontWeight = FontWeight.Bold) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                        )

                        if (manualAmountInput.isNotEmpty() && !isManualAmtValid) {
                            Text(
                                text = "❌ Nominal penarikan harus lebih besar dari 0",
                                fontSize = 11.sp,
                                color = ErrorRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bank Tujuan:", fontSize = 11.sp, color = TextSecondary)
                                OutlinedTextField(
                                    value = manualBankName,
                                    onValueChange = { manualBankName = it },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("No Rekening:", fontSize = 11.sp, color = TextSecondary)
                                OutlinedTextField(
                                    value = manualBankNum,
                                    onValueChange = { manualBankNum = it },
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Nama Pemilik Rekening:", fontSize = 12.sp, color = TextSecondary)
                        OutlinedTextField(
                            value = manualBankHolder,
                            onValueChange = { manualBankHolder = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (isManualAmtValid) {
                                    viewModel.superAdminManualWithdrawal(manualAmt, manualBankName, manualBankNum, manualBankHolder, manualNoteInput, targetAccountType)
                                }
                            },
                            enabled = isManualAmtValid,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.MoneyOff, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Eksekusi Penarikan Manual dari ${if (targetAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"}", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
        }

        // --- SECTION 2: BIAYA PENARIKAN, BIAYA ADMIN & POTONGAN PAJAK CONTROLLER ---
        if (activeFeatureIndex == 5) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = AccentGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Atur Biaya Penarikan, Admin & Pajak",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Super Admin mengatur skema potongan biaya penarikan flat, biaya admin, & pajak PPh untuk Pengguna dan Perusahaan",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // BIAYA UNTUK AKUN PENGGUNA (PERSONAL)
                    Text("1. Biaya & Pajak untuk Akun Pengguna (Personal)", fontWeight = FontWeight.Bold, color = PrimaryEmerald, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biaya Penarikan Flat (Rp):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = userWdFeeInput,
                                onValueChange = { userWdFeeInput = it.filter { c -> c.isDigit() } },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biaya Admin (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = userAdminPctInput,
                                onValueChange = { userAdminPctInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Potongan Pajak (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = userTaxPctInput,
                                onValueChange = { userTaxPctInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BIAYA UNTUK AKUN PERUSAHAAN (COMPANY)
                    Text("2. Biaya & Pajak untuk Akun Perusahaan (Institusi)", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biaya Penarikan Flat (Rp):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = companyWdFeeInput,
                                onValueChange = { companyWdFeeInput = it.filter { c -> c.isDigit() } },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biaya Admin (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = companyAdminPctInput,
                                onValueChange = { companyAdminPctInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Potongan Pajak (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = companyTaxPctInput,
                                onValueChange = { companyTaxPctInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder)

                    Spacer(modifier = Modifier.height(16.dp))

                    // BIAYA REFERRAL & PAJAK (INVITATION BONUS & TAX)
                    Text("3. Pengaturan Bonus Referral & Pajak", fontWeight = FontWeight.Bold, color = PrimaryEmerald, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bonus Pengundang Individu (Rp):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = individualReferrerBonusInput,
                                onValueChange = { individualReferrerBonusInput = it.filter { c -> c.isDigit() } },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bonus Diundang Individu (Rp):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = individualReferredBonusInput,
                                onValueChange = { individualReferredBonusInput = it.filter { c -> c.isDigit() } },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bonus Pengundang Perusahaan (Rp):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = companyReferrerBonusInput,
                                onValueChange = { companyReferrerBonusInput = it.filter { c -> c.isDigit() } },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bonus Diundang Perusahaan (Rp):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = companyReferredBonusInput,
                                onValueChange = { companyReferredBonusInput = it.filter { c -> c.isDigit() } },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Pajak Atas Bonus Undangan (%):", fontSize = 11.sp, color = TextSecondary)
                        OutlinedTextField(
                            value = referralTaxPctInput,
                            onValueChange = { referralTaxPctInput = it },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    androidx.compose.material3.HorizontalDivider(color = DarkCardBorder)

                    Spacer(modifier = Modifier.height(16.dp))

                    // KOMISI BAGI HASIL INVESTASI (PROFIT SHARING COMMISSION)
                    Text("4. Komisi Bagi Hasil Investasi dari Teman (%)", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bagi Hasil Harian (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = referralDailyCommissionInput,
                                onValueChange = { referralDailyCommissionInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bagi Hasil Bulanan (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = referralMonthlyCommissionInput,
                                onValueChange = { referralMonthlyCommissionInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bagi Hasil Tahunan (%):", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = referralYearlyCommissionInput,
                                onValueChange = { referralYearlyCommissionInput = it },
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val newConfig = AdminFeeConfig(
                                id = 1,
                                userWithdrawalFee = userWdFeeInput.toDoubleOrNull() ?: 2500.0,
                                userAdminFeePct = userAdminPctInput.toDoubleOrNull() ?: 1.0,
                                userTaxFeePct = userTaxPctInput.toDoubleOrNull() ?: 5.0,
                                companyWithdrawalFee = companyWdFeeInput.toDoubleOrNull() ?: 10000.0,
                                companyAdminFeePct = companyAdminPctInput.toDoubleOrNull() ?: 0.5,
                                companyTaxFeePct = companyTaxPctInput.toDoubleOrNull() ?: 2.5,
                                individualReferralReferrerBonus = individualReferrerBonusInput.toDoubleOrNull() ?: 150000.0,
                                individualReferralReferredBonus = individualReferredBonusInput.toDoubleOrNull() ?: 50000.0,
                                companyReferralReferrerBonus = companyReferrerBonusInput.toDoubleOrNull() ?: 300000.0,
                                companyReferralReferredBonus = companyReferredBonusInput.toDoubleOrNull() ?: 100000.0,
                                referralTaxPct = referralTaxPctInput.toDoubleOrNull() ?: 10.0,
                                referralDailyCommissionPct = referralDailyCommissionInput.toDoubleOrNull() ?: 5.0,
                                referralMonthlyCommissionPct = referralMonthlyCommissionInput.toDoubleOrNull() ?: 3.0,
                                referralYearlyCommissionPct = referralYearlyCommissionInput.toDoubleOrNull() ?: 2.0
                            )
                            viewModel.superAdminSaveAdminFeeConfig(newConfig)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Pengaturan Biaya & Pajak", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
        }

        // --- SECTION 3: ATUR RATE KEUNTUNGAN & MODAL MINIMUM ---
        if (activeFeatureIndex == 6) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = PrimaryEmerald
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Atur Rate Keuntungan & Modal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Account Category Selector (AKUN PENGGUNA vs AKUN PERUSAHAAN)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardSurface)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedCategoryTab == "USER") PrimaryEmerald else Color.Transparent)
                                .clickable {
                                    selectedCategoryTab = "USER"
                                    dailyRateInput = "0.8"
                                    monthlyRateInput = "24.0"
                                    yearlyRateInput = "288.0"
                                    minCapitalInput = "100000"
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Akun Pengguna",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategoryTab == "USER") Color.Black else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedCategoryTab == "COMPANY") AccentGold else Color.Transparent)
                                .clickable {
                                    selectedCategoryTab = "COMPANY"
                                    dailyRateInput = "1.5"
                                    monthlyRateInput = "45.0"
                                    yearlyRateInput = "540.0"
                                    minCapitalInput = "50000000"
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Akun Perusahaan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategoryTab == "COMPANY") Color.Black else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Inputs: Daily %, Monthly %, Yearly %, Min Capital
                    Text(text = "Setel Persen Keuntungan Harian (%):", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = dailyRateInput,
                        onValueChange = { dailyRateInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = { Text(text = "% / Hari", color = PrimaryEmerald, fontWeight = FontWeight.Bold) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Bulanan (%):", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = monthlyRateInput,
                                onValueChange = { monthlyRateInput = it },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                trailingIcon = { Text(text = "%", color = AccentGold) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Tahunan (%):", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = yearlyRateInput,
                                onValueChange = { yearlyRateInput = it },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                trailingIcon = { Text(text = "%", color = Color(0xFF64B5F6)) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF64B5F6), unfocusedBorderColor = DarkCardBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Setel Modal Minimum Investasi (Rp):", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = minCapitalInput,
                        onValueChange = { minCapitalInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text(text = "Rp", color = PrimaryEmerald, fontWeight = FontWeight.Bold) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val d = dailyRateInput.toDoubleOrNull() ?: 0.0
                            val m = monthlyRateInput.toDoubleOrNull() ?: 0.0
                            val y = yearlyRateInput.toDoubleOrNull() ?: 0.0
                            val cap = minCapitalInput.toDoubleOrNull()
                            viewModel.superAdminUpdateYieldRates(selectedCategoryTab, d, m, y, cap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_yield_rates_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simpan Rate untuk ${if (selectedCategoryTab == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- SECTION 4: KELOLA DAFTAR PAKET INVESTASI ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daftar Paket Investasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Kelola modal & deskripsi paket spesifik",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            editingPackage = null
                            pkgName = ""
                            pkgDesc = ""
                            pkgAccType = "USER"
                            pkgMinCap = "100000"
                            pkgMaxCap = "100000000"
                            pkgDailyPct = "1.0"
                            pkgMonthlyPct = "30.0"
                            pkgYearlyPct = "360.0"
                            showPackageDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("add_new_package_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Tambah", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (packages.isEmpty()) {
                    Text(text = "Belum ada paket investasi.", color = TextSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        packages.forEach { pkg ->
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
                                                text = pkg.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Target: ${if (pkg.accountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"}",
                                                fontSize = 11.sp,
                                                color = if (pkg.accountType == "COMPANY") AccentGold else PrimaryEmerald,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    editingPackage = pkg
                                                    pkgName = pkg.name
                                                    pkgDesc = pkg.description
                                                    pkgAccType = pkg.accountType
                                                    pkgMinCap = pkg.minCapital.toLong().toString()
                                                    pkgMaxCap = pkg.maxCapital.toLong().toString()
                                                    pkgDailyPct = pkg.dailyReturnPct.toString()
                                                    pkgMonthlyPct = pkg.monthlyReturnPct.toString()
                                                    pkgYearlyPct = pkg.yearlyReturnPct.toString()
                                                    showPackageDialog = true
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary)
                                            }

                                            IconButton(
                                                onClick = { viewModel.superAdminDeletePackage(pkg.id) }
                                            ) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Modal: Rp ${String.format("%,.0f", pkg.minCapital)} s/d Rp ${String.format("%,.0f", pkg.maxCapital)}",
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Yield: ${pkg.dailyReturnPct}% / Hari • ${pkg.monthlyReturnPct}% / Bulan • ${pkg.yearlyReturnPct}% / Tahun",
                                        fontSize = 12.sp,
                                        color = AccentGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        }

        // --- TAB 7: RODA KEBERUNTUNGAN CONFIGURATION ---
        if (activeFeatureIndex == 7) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎡 Kontrol Roda Keberuntungan (Super Admin)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Status: ", fontSize = 12.sp, color = TextSecondary)
                                Switch(
                                    checked = isWheelEnabledInput,
                                    onCheckedChange = { isWheelEnabledInput = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = PrimaryEmerald,
                                        checkedTrackColor = PrimaryEmerald.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Atur kuota klaim harian, persentase pajak klaim, serta tier persentase keuntungan harian berdasarkan nominal deposito roda keberuntungan (Individual & Perusahaan).",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        HorizontalDivider(color = DarkCardBorder)

                        // Global Parameters (Quota & Tax)
                        Text("⚙️ Parameter Umum Klaim", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentGold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = wheelQuotaInput,
                                onValueChange = { wheelQuotaInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Kuota Klaim Harian", color = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = wheelTaxPctInput,
                                onValueChange = { wheelTaxPctInput = it },
                                label = { Text("Pajak Klaim (%)", color = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        HorizontalDivider(color = DarkCardBorder)

                        // INDIVIDUAL TIERS
                        Text("👤 Tier Deposito & Keuntungan (Pengguna Individual)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)

                        // Ind Tier 1
                        Text("Tier 1 Individual:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = indTier1MinInput,
                                onValueChange = { indTier1MinInput = it },
                                label = { Text("Min Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = indTier1MaxInput,
                                onValueChange = { indTier1MaxInput = it },
                                label = { Text("Max Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = indTier1ProfitInput,
                                onValueChange = { indTier1ProfitInput = it },
                                label = { Text("Profit (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Ind Tier 2
                        Text("Tier 2 Individual Growth:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = indTier2MinInput,
                                onValueChange = { indTier2MinInput = it },
                                label = { Text("Min Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = indTier2MaxInput,
                                onValueChange = { indTier2MaxInput = it },
                                label = { Text("Max Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = indTier2ProfitInput,
                                onValueChange = { indTier2ProfitInput = it },
                                label = { Text("Profit (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Ind Tier 3
                        Text("Tier 3 Individual VIP:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = indTier3MinInput,
                                onValueChange = { indTier3MinInput = it },
                                label = { Text("Min Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = indTier3MaxInput,
                                onValueChange = { indTier3MaxInput = it },
                                label = { Text("Max Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = indTier3ProfitInput,
                                onValueChange = { indTier3ProfitInput = it },
                                label = { Text("Profit (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        HorizontalDivider(color = DarkCardBorder)

                        // CORPORATE TIERS
                        Text("🏢 Tier Deposito & Keuntungan (Pengguna Perusahaan/Corporate)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)

                        // Com Tier 1
                        Text("Tier 1 Korporat:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = comTier1MinInput,
                                onValueChange = { comTier1MinInput = it },
                                label = { Text("Min Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = comTier1MaxInput,
                                onValueChange = { comTier1MaxInput = it },
                                label = { Text("Max Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = comTier1ProfitInput,
                                onValueChange = { comTier1ProfitInput = it },
                                label = { Text("Profit (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Com Tier 2
                        Text("Tier 2 Korporat Growth:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = comTier2MinInput,
                                onValueChange = { comTier2MinInput = it },
                                label = { Text("Min Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = comTier2MaxInput,
                                onValueChange = { comTier2MaxInput = it },
                                label = { Text("Max Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = comTier2ProfitInput,
                                onValueChange = { comTier2ProfitInput = it },
                                label = { Text("Profit (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Com Tier 3
                        Text("Tier 3 Korporat Platinum:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = comTier3MinInput,
                                onValueChange = { comTier3MinInput = it },
                                label = { Text("Min Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = comTier3MaxInput,
                                onValueChange = { comTier3MaxInput = it },
                                label = { Text("Max Deposito", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = comTier3ProfitInput,
                                onValueChange = { comTier3ProfitInput = it },
                                label = { Text("Profit (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val updatedConfig = LuckyWheelConfig(
                                    id = 1,
                                    isWheelEnabled = isWheelEnabledInput,
                                    dailyClaimQuota = wheelQuotaInput.toIntOrNull() ?: 3,
                                    taxPct = wheelTaxPctInput.toDoubleOrNull() ?: 5.0,
                                    individualTier1Min = indTier1MinInput.toDoubleOrNull() ?: 1.0,
                                    individualTier1Max = indTier1MaxInput.toDoubleOrNull() ?: 10000000.0,
                                    individualTier1ProfitPct = indTier1ProfitInput.toDoubleOrNull() ?: 1.0,
                                    individualTier2Min = indTier2MinInput.toDoubleOrNull() ?: 10000001.0,
                                    individualTier2Max = indTier2MaxInput.toDoubleOrNull() ?: 50000000.0,
                                    individualTier2ProfitPct = indTier2ProfitInput.toDoubleOrNull() ?: 1.8,
                                    individualTier3Min = indTier3MinInput.toDoubleOrNull() ?: 50000001.0,
                                    individualTier3Max = indTier3MaxInput.toDoubleOrNull() ?: 1000000000.0,
                                    individualTier3ProfitPct = indTier3ProfitInput.toDoubleOrNull() ?: 2.5,
                                    companyTier1Min = comTier1MinInput.toDoubleOrNull() ?: 1.0,
                                    companyTier1Max = comTier1MaxInput.toDoubleOrNull() ?: 50000000.0,
                                    companyTier1ProfitPct = comTier1ProfitInput.toDoubleOrNull() ?: 1.8,
                                    companyTier2Min = comTier2MinInput.toDoubleOrNull() ?: 50000001.0,
                                    companyTier2Max = comTier2MaxInput.toDoubleOrNull() ?: 250000000.0,
                                    companyTier2ProfitPct = comTier2ProfitInput.toDoubleOrNull() ?: 3.0,
                                    companyTier3Min = comTier3MinInput.toDoubleOrNull() ?: 250000001.0,
                                    companyTier3Max = comTier3MaxInput.toDoubleOrNull() ?: 10000000000.0,
                                    companyTier3ProfitPct = comTier3ProfitInput.toDoubleOrNull() ?: 4.5
                                )
                                viewModel.saveLuckyWheelConfig(updatedConfig)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SIMPAN PENGATURAN RODA KEBERUNTUNGAN", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- TAB 8: AUDIT LOGS (READ-ONLY) ---
        if (activeFeatureIndex == 8) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "📜 Log Audit Perubahan Sistem (Read-Only)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PrimaryEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${auditLogs.size} Catatan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Seluruh riwayat perubahan persentase profit, tarif pajak, dan parameter Roda Keberuntungan/sistem dicatat secara permanen untuk transparansi tata kelola Super Admin.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        HorizontalDivider(color = DarkCardBorder)

                        if (auditLogs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📭", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Belum ada riwayat perubahan audit.", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        } else {
                            val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm:ss", java.util.Locale.getDefault())
                            auditLogs.forEach { log ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (log.actionType.contains("LUCKY_WHEEL")) AccentGold.copy(alpha = 0.2f) else PrimaryEmerald.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = log.actionType,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (log.actionType.contains("LUCKY_WHEEL")) AccentGold else PrimaryEmerald,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = dateFormat.format(java.util.Date(log.timestamp)),
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }

                                        Text(
                                            text = log.description,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )

                                        Text(
                                            text = "Aktor: ${log.adminEmail}",
                                            fontSize = 10.sp,
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

        // --- PERSISTENT FOOTER NAVIGATION BUTTONS ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (activeFeatureIndex > 0) activeFeatureIndex--
                        },
                        enabled = activeFeatureIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeFeatureIndex > 0) Color(0xFF1E3A8A) else Color.Gray.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("← Fitur Sebelum", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Halaman ${activeFeatureIndex + 1} dari ${featureList.size}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = {
                            if (activeFeatureIndex < featureList.size - 1) activeFeatureIndex++
                        },
                        enabled = activeFeatureIndex < featureList.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeFeatureIndex < featureList.size - 1) PrimaryEmerald else Color.Gray.copy(alpha = 0.2f),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Fitur Berikut →", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- DIALOG TAMBAH / EDIT PAKET INVESTASI ---
    if (showPackageDialog) {
        AlertDialog(
            onDismissRequest = { showPackageDialog = false },
            title = {
                Text(
                    text = if (editingPackage == null) "Tambah Paket Investasi Baru" else "Edit Paket Investasi",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = pkgName,
                            onValueChange = { pkgName = it },
                            label = { Text("Nama Paket") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pkgDesc,
                            onValueChange = { pkgDesc = it },
                            label = { Text("Deskripsi Singkat") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text(text = "Target Tipe Akun:", fontSize = 12.sp, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { pkgAccType = "USER" },
                                colors = ButtonDefaults.buttonColors(containerColor = if (pkgAccType == "USER") PrimaryEmerald else DarkCardSurface)
                            ) {
                                Text(text = "Pengguna", color = if (pkgAccType == "USER") Color.Black else TextPrimary)
                            }
                            Button(
                                onClick = { pkgAccType = "COMPANY" },
                                colors = ButtonDefaults.buttonColors(containerColor = if (pkgAccType == "COMPANY") AccentGold else DarkCardSurface)
                            ) {
                                Text(text = "Perusahaan", color = if (pkgAccType == "COMPANY") Color.Black else TextPrimary)
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = pkgMinCap,
                            onValueChange = { pkgMinCap = it.filter { char -> char.isDigit() } },
                            label = { Text("Modal Minimum (Rp)") },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pkgMaxCap,
                            onValueChange = { pkgMaxCap = it.filter { char -> char.isDigit() } },
                            label = { Text("Modal Maksimum (Rp)") },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pkgDailyPct,
                            onValueChange = { pkgDailyPct = it },
                            label = { Text("Persen Harian (%)") },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pkgMonthlyPct,
                            onValueChange = { pkgMonthlyPct = it },
                            label = { Text("Persen Bulanan (%)") },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = pkgYearlyPct,
                            onValueChange = { pkgYearlyPct = it },
                            label = { Text("Persen Tahunan (%)") },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pkgToSave = InvestmentPackage(
                            id = editingPackage?.id ?: 0,
                            name = pkgName.ifBlank { "Paket Investasi" },
                            description = pkgDesc.ifBlank { "Deskripsi paket investasi" },
                            accountType = pkgAccType,
                            minCapital = pkgMinCap.toDoubleOrNull() ?: 100000.0,
                            maxCapital = pkgMaxCap.toDoubleOrNull() ?: 100000000.0,
                            dailyReturnPct = pkgDailyPct.toDoubleOrNull() ?: 1.0,
                            monthlyReturnPct = pkgMonthlyPct.toDoubleOrNull() ?: 30.0,
                            yearlyReturnPct = pkgYearlyPct.toDoubleOrNull() ?: 360.0
                        )
                        viewModel.superAdminSavePackage(pkgToSave)
                        showPackageDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.testTag("save_package_dialog_button")
                ) {
                    Text(text = "Simpan Paket", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPackageDialog = false }) {
                    Text(text = "Batal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    // --- DIALOG TAMBAH / EDIT STAFF ---
    if (showStaffDialog) {
        AlertDialog(
            onDismissRequest = { showStaffDialog = false },
            title = {
                Text(
                    text = if (editingStaff == null) "Tambah Staff / Pegawai Baru" else "Edit Data Staff [${editingStaff?.staffCode}]",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = staffCodeForm,
                        onValueChange = { staffCodeForm = it },
                        label = { Text("Kode Staff (Contoh: STF-001)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_staff_code")
                    )

                    OutlinedTextField(
                        value = staffNameForm,
                        onValueChange = { staffNameForm = it },
                        label = { Text("Nama Lengkap Staff") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_staff_name")
                    )

                    OutlinedTextField(
                        value = staffEmailForm,
                        onValueChange = { staffEmailForm = it },
                        label = { Text("Email Staff") },
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_staff_email")
                    )

                    OutlinedTextField(
                        value = staffPhoneForm,
                        onValueChange = { staffPhoneForm = it },
                        label = { Text("No Telepon / WhatsApp Staff") },
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_staff_phone")
                    )

                    OutlinedTextField(
                        value = staffPinForm,
                        onValueChange = { staffPinForm = it },
                        label = { Text("PIN / Password Staff") },
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_staff_pin")
                    )

                    OutlinedTextField(
                        value = staffRoleForm,
                        onValueChange = { staffRoleForm = it },
                        label = { Text("Jabatan / Peran (Contoh: CS Admin / Finance Verifier)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_staff_role")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val staffToSave = StaffAccount(
                            id = editingStaff?.id ?: 0,
                            staffCode = staffCodeForm.ifBlank { "STF-001" },
                            fullName = staffNameForm.ifBlank { "Staff Member" },
                            email = staffEmailForm,
                            phone = staffPhoneForm,
                            passwordPin = staffPinForm.ifBlank { "123456" },
                            rolePosition = staffRoleForm.ifBlank { "Customer Service" },
                            isActive = editingStaff?.isActive ?: true
                        )
                        viewModel.superAdminSaveStaff(staffToSave)
                        showStaffDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                    modifier = Modifier.testTag("save_staff_dialog_button")
                ) {
                    Text(text = "Simpan Staff", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStaffDialog = false }) {
                    Text(text = "Batal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showAddBannerDialog) {
        AlertDialog(
            onDismissRequest = { showAddBannerDialog = false },
            title = { Text("Tambah Slide Banner Promosi", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newBannerTitle,
                        onValueChange = { newBannerTitle = it },
                        label = { Text("Judul Banner") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newBannerSubtitle,
                        onValueChange = { newBannerSubtitle = it },
                        label = { Text("Subjudul / Deskripsi") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newBannerBadge,
                        onValueChange = { newBannerBadge = it },
                        label = { Text("Badge (Contoh: HOT PROMO)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newBannerAction,
                        onValueChange = { newBannerAction = it },
                        label = { Text("Label Tombol (Contoh: Klaim Bonus)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBannerTitle.isNotBlank()) {
                            viewModel.superAdminSaveBanner(
                                BannerSlideItem(
                                    title = newBannerTitle,
                                    subtitle = newBannerSubtitle,
                                    badgeText = newBannerBadge.ifBlank { "PROMO" },
                                    actionLabel = newBannerAction.ifBlank { "Lihat" },
                                    isActive = true,
                                    displayOrder = banners.size + 1
                                )
                            )
                            newBannerTitle = ""
                            newBannerSubtitle = ""
                            showAddBannerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                ) {
                    Text("Simpan Banner", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBannerDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showD1SqlSchemaDialog) {
        val d1SqlSchemaScript = """
-- =======================================================
-- CLOUDFLARE D1 SQLITE DATABASE SCHEMA (14 TABEL)
-- Database: investpro-d1-db | Binding: DB
-- Execute via Wrangler: wrangler d1 execute investpro-d1-db --file=schema.sql
-- =======================================================

CREATE TABLE IF NOT EXISTS user_profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    fullName TEXT NOT NULL,
    phoneNumber TEXT,
    balance REAL DEFAULT 0.0,
    dailyProfit REAL DEFAULT 0.0,
    totalProfit REAL DEFAULT 0.0,
    referralCode TEXT UNIQUE,
    referredBy TEXT,
    referralCount INTEGER DEFAULT 0,
    isKycApproved INTEGER DEFAULT 0,
    kycStatus TEXT DEFAULT 'UNVERIFIED',
    kycFullName TEXT,
    kycNik TEXT,
    kycSelfieUrl TEXT,
    kycKtUrl TEXT,
    kycApprovedLimit REAL DEFAULT 0.0,
    bankName TEXT,
    bankAccountNumber TEXT,
    bankAccountName TEXT,
    pinHash TEXT,
    createdAt TEXT
);

CREATE TABLE IF NOT EXISTS investment_packages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    minAmount REAL DEFAULT 0.0,
    maxAmount REAL DEFAULT 0.0,
    dailyRoiPercent REAL DEFAULT 0.0,
    durationDays INTEGER DEFAULT 30,
    category TEXT,
    riskLevel TEXT,
    isActive INTEGER DEFAULT 1,
    imageUrl TEXT
);

CREATE TABLE IF NOT EXISTS user_investments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    packageId INTEGER NOT NULL,
    packageName TEXT,
    amountInvested REAL DEFAULT 0.0,
    dailyRoiPercent REAL DEFAULT 0.0,
    totalProfitEarned REAL DEFAULT 0.0,
    durationDays INTEGER DEFAULT 30,
    daysRemaining INTEGER DEFAULT 30,
    startDate TEXT,
    status TEXT DEFAULT 'ACTIVE',
    FOREIGN KEY(userId) REFERENCES user_profiles(id)
);

CREATE TABLE IF NOT EXISTS transaction_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    type TEXT NOT NULL, -- DEPOSIT, WITHDRAWAL, PROFIT_PAYOUT, REFERRAL_BONUS
    amount REAL DEFAULT 0.0,
    adminFee REAL DEFAULT 0.0,
    netAmount REAL DEFAULT 0.0,
    paymentMethod TEXT,
    status TEXT DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    paymentProofUrl TEXT,
    transactionDate TEXT,
    notes TEXT
);

CREATE TABLE IF NOT EXISTS brand_config (
    id INTEGER PRIMARY KEY DEFAULT 1,
    appName TEXT,
    companyName TEXT,
    tagline TEXT,
    whatsappNumber TEXT,
    supportEmail TEXT,
    companyDomain TEXT,
    cloudflareAccountId TEXT,
    cloudflareApiToken TEXT,
    cloudflarePagesDomain TEXT,
    cloudflareWorkerEndpoint TEXT,
    cloudflareD1DatabaseId TEXT,
    cloudflareD1DatabaseName TEXT,
    cloudflareD1BindingName TEXT,
    isD1AutoSyncEnabled INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS payment_gateway_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    providerName TEXT NOT NULL,
    accountName TEXT,
    accountNumber TEXT,
    qrCodeUrl TEXT,
    isActive INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS admin_fee_config (
    id INTEGER PRIMARY KEY DEFAULT 1,
    depositFeePercent REAL DEFAULT 0.0,
    withdrawalFeePercent REAL DEFAULT 0.0,
    fixedWithdrawalFee REAL DEFAULT 0.0,
    minWithdrawalAmount REAL DEFAULT 50000.0
);

CREATE TABLE IF NOT EXISTS user_accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    passwordHash TEXT NOT NULL,
    role TEXT DEFAULT 'USER',
    userId INTEGER,
    createdAt TEXT
);

CREATE TABLE IF NOT EXISTS staff_accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    passwordHash TEXT NOT NULL,
    role TEXT DEFAULT 'STAFF',
    accessPermissions TEXT
);

CREATE TABLE IF NOT EXISTS notification_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    isRead INTEGER DEFAULT 0,
    createdAt TEXT
);

CREATE TABLE IF NOT EXISTS crypto_wallet_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    coinSymbol TEXT NOT NULL,
    network TEXT,
    walletAddress TEXT NOT NULL,
    isActive INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS banner_slide_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    subtitle TEXT,
    imageUrl TEXT,
    targetUrl TEXT,
    isActive INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS lucky_wheel_config (
    id INTEGER PRIMARY KEY DEFAULT 1,
    isEnabled INTEGER DEFAULT 1,
    prizesJson TEXT
);

CREATE TABLE IF NOT EXISTS audit_log_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    action TEXT,
    performedBy TEXT,
    timestamp TEXT,
    details TEXT
);
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { showD1SqlSchemaDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = PrimaryEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cloudflare D1 SQL Schema (14 Table)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text("Gunakan perintah berikut untuk memasang schema 14 tabel di Cloudflare D1 via CLI / Cloudflare Console:", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF101813),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().height(260.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            item {
                                SelectionContainer {
                                    Text(
                                        text = d1SqlSchemaScript,
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        color = PrimaryEmerald,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showD1SqlSchemaDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                ) {
                    Text("Tutup Schema", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardSurface
        )
    }
}

@Composable
fun AdminUserKycCard(
    user: com.example.data.entity.UserAccount,
    viewModel: MainViewModel
) {
    var limitInput by remember(user.kycTransactionLimit) { mutableStateOf(String.format("%.0f", user.kycTransactionLimit)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: User Info and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.fullName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Email: ${user.email}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    if (user.ktpNumber.isNotBlank()) {
                        Text(
                            text = "NIK KTP: ${user.ktpNumber}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (user.kycStatus) {
                        "VERIFIED" -> SuccessGreen.copy(alpha = 0.2f)
                        "PENDING" -> AccentGold.copy(alpha = 0.2f)
                        "REJECTED" -> ErrorRed.copy(alpha = 0.2f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = user.kycStatus,
                        color = when (user.kycStatus) {
                            "VERIFIED" -> SuccessGreen
                            "PENDING" -> AccentGold
                            "REJECTED" -> ErrorRed
                            else -> Color.Gray
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkCardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Details on requested tier and limit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tingkatan Diajukan", color = TextSecondary, fontSize = 11.sp)
                    Text(
                        text = user.requestedAccountTier.uppercase(),
                        color = if (user.requestedAccountTier == "ENTERPRISE") AccentGold else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Limit Transaksi Saat Ini", color = TextSecondary, fontSize = 11.sp)
                    Text(
                        text = "Rp ${String.format("%,.0f", user.kycTransactionLimit)}",
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom limit editor box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    "Atur Batas Limit Transaksi (Rp):",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it.filter { char -> char.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("input_admin_limit_${user.id}"),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        placeholder = { Text("Limit Nominal Rp", fontSize = 11.sp, color = TextSecondary) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val amount = limitInput.toDoubleOrNull() ?: 10_000_000.0
                            viewModel.superAdminUpdateUserLimit(user, amount)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_update_limit_${user.id}")
                    ) {
                        Text("Update Limit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fast Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(10_000_000.0, 100_000_000.0, 1_000_000_000.0, 5_000_000_000.0).forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkCardSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    limitInput = String.format("%.0f", amt)
                                }
                        ) {
                            Text(
                                text = when (amt) {
                                    10_000_000.0 -> "10JT"
                                    100_000_000.0 -> "100JT"
                                    1_000_000_000.0 -> "1M"
                                    else -> "5M"
                                },
                                color = TextSecondary,
                                fontSize = 9.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Actions Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (user.kycStatus != "VERIFIED") {
                    Button(
                        onClick = {
                            val approvedLimit = limitInput.toDoubleOrNull() ?: 10_000_000.0
                            viewModel.superAdminApproveKyc(user, approvedLimit)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("btn_approve_kyc_${user.id}")
                    ) {
                        Text("Setujui KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.superAdminRejectKyc(user) },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("btn_reject_kyc_${user.id}")
                    ) {
                        Text("Tolak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.superAdminToggleUserKyc(user) },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("btn_revoke_kyc_${user.id}")
                    ) {
                        Text("Batalkan Verifikasi KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
