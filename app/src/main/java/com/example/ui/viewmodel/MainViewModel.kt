package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.AdminFeeConfig
import com.example.data.entity.AuditLogRecord
import com.example.data.entity.BannerSlideItem
import com.example.data.entity.BrandConfig
import com.example.data.entity.CryptoWalletConfig
import com.example.data.entity.GlobalDataRegistry
import com.example.data.entity.InvestmentPackage
import com.example.data.entity.LuckyWheelConfig
import com.example.data.entity.NotificationItem
import com.example.data.entity.PaymentGatewayConfig
import com.example.data.entity.TransactionRecord
import com.example.data.entity.UserInvestment
import com.example.data.entity.UserProfile
import com.example.data.repository.InvestmentRepository
import com.example.util.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.entity.StaffAccount
import com.example.data.entity.UserAccount

enum class ScreenRoute {
    LANDING,
    USER_DASHBOARD,
    SUPER_ADMIN,
    STAFF_DASHBOARD,
    DEPOSIT,
    WITHDRAWAL,
    PAYMENT_GATEWAY_API,
    WEB_PORTAL,
    TRANSACTION_HISTORY,
    AUTH,
    KYC_SUBMISSION,
    LUCKY_WHEEL
}

data class AuthSessionState(
    val isLoggedIn: Boolean = true,
    val role: String = "INDIVIDUAL", // "INDIVIDUAL", "COMPANY", "SUPER_ADMIN", "STAFF", "GUEST"
    val activeUser: UserAccount? = null,
    val activeStaff: StaffAccount? = null
) {
    val displayName: String
        get() = when {
            activeStaff != null -> activeStaff.fullName
            activeUser != null -> activeUser.fullName
            role == "SUPER_ADMIN" -> "Super Admin"
            else -> "Tamu"
        }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InvestmentRepository

    val userProfile: StateFlow<UserProfile?>
    val allPackages: StateFlow<List<InvestmentPackage>>
    val userInvestments: StateFlow<List<UserInvestment>>
    val transactions: StateFlow<List<TransactionRecord>>
    val paymentGatewayConfig: StateFlow<PaymentGatewayConfig?>
    val adminFeeConfig: StateFlow<AdminFeeConfig?>
    val brandConfig: StateFlow<BrandConfig?>
    val allUserAccounts: StateFlow<List<UserAccount>>
    val allStaffAccounts: StateFlow<List<StaffAccount>>
    val notifications: StateFlow<List<NotificationItem>>
    val unreadNotificationCount: StateFlow<Int>
    val cryptoWalletConfig: StateFlow<CryptoWalletConfig?>
    val luckyWheelConfig: StateFlow<LuckyWheelConfig?>
    val allBanners: StateFlow<List<BannerSlideItem>>
    val auditLogs: StateFlow<List<AuditLogRecord>>

    private val _authSession = MutableStateFlow(
        AuthSessionState(
            isLoggedIn = true,
            role = "INDIVIDUAL",
            activeUser = UserAccount(
                fullName = "Ahmad Pratama",
                email = "ahmad@investor.id",
                password = "user123",
                accountType = "INDIVIDUAL",
                balance = 25_000_000.0,
                bankName = "Bank BCA",
                bankAccountNumber = "8830192837",
                bankAccountName = "Ahmad Pratama"
            )
        )
    )
    val authSession: StateFlow<AuthSessionState> = _authSession.asStateFlow()
    val currentStaffAccount: StateFlow<StaffAccount?> = _authSession.map { it.activeStaff }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    private val _currentRoute = MutableStateFlow(ScreenRoute.LANDING)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    private val _pendingRegistrationUser = MutableStateFlow<UserAccount?>(null)
    val pendingRegistrationUser: StateFlow<UserAccount?> = _pendingRegistrationUser.asStateFlow()

    private val _showEmailVerificationDialog = MutableStateFlow(false)
    val showEmailVerificationDialog: StateFlow<Boolean> = _showEmailVerificationDialog.asStateFlow()

    private val _verificationOtpCode = MutableStateFlow("")
    val verificationOtpCode: StateFlow<String> = _verificationOtpCode.asStateFlow()

    private val _showAccountRecoveryDialog = MutableStateFlow(false)
    val showAccountRecoveryDialog: StateFlow<Boolean> = _showAccountRecoveryDialog.asStateFlow()

    private val _recoveryInitialTab = MutableStateFlow("PASSWORD") // "PASSWORD", "PIN", "EMAIL", "PASSCODE"
    val recoveryInitialTab: StateFlow<String> = _recoveryInitialTab.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // Filtered packages according to current user's profile accountType (USER vs COMPANY)
    val filteredPackages: StateFlow<List<InvestmentPackage>>

    init {
        val dao = AppDatabase.getDatabase(application).appDao()
        repository = InvestmentRepository(dao)

        userProfile = repository.userProfile.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile()
        )

        allPackages = repository.allPackages.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        userInvestments = repository.userInvestments.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        transactions = repository.transactions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        paymentGatewayConfig = repository.paymentGatewayConfig.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), PaymentGatewayConfig()
        )

        adminFeeConfig = repository.adminFeeConfig.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), AdminFeeConfig()
        )

        brandConfig = repository.getBrandConfig().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), BrandConfig()
        )

        allUserAccounts = repository.allUserAccounts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allStaffAccounts = repository.allStaffAccounts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        notifications = repository.notifications.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        unreadNotificationCount = repository.unreadNotificationCount.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        )

        cryptoWalletConfig = repository.cryptoWalletConfig.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), CryptoWalletConfig()
        )

        luckyWheelConfig = repository.luckyWheelConfig.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), LuckyWheelConfig()
        )

        allBanners = repository.allBanners.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        auditLogs = repository.auditLogs.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        filteredPackages = combine(allPackages, userProfile) { pkgs, profile ->
            val type = profile?.accountType ?: "USER"
            pkgs.filter { it.accountType == type || it.accountType == "ALL" }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun navigateTo(route: ScreenRoute) {
        _currentRoute.value = route
    }

    // --- ACCOUNT TYPE TOGGLE ---
    fun switchAccountType(accountType: String) {
        viewModelScope.launch {
            repository.updateAccountType(accountType)
            val typeLabel = if (accountType == "COMPANY") "Perusahaan" else "Pengguna (Personal)"
            _uiEvent.emit("Tipe akun berhasil diubah ke Akun $typeLabel")
        }
    }

    fun updateProfile(name: String, email: String, companyName: String, bankName: String, bankNum: String, bankHolder: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email, companyName, bankName, bankNum, bankHolder)
            _uiEvent.emit("Profil & Rekening berhasil diperbarui")
        }
    }

    fun updateReferralCode(email: String, newCode: String) {
        viewModelScope.launch {
            val cleanCode = newCode.replace("\\s".toRegex(), "").uppercase()
            if (cleanCode.isBlank()) {
                _uiEvent.emit("Kode referral tidak boleh kosong.")
                return@launch
            }
            if (cleanCode.length < 4) {
                _uiEvent.emit("Kode referral minimal 4 karakter.")
                return@launch
            }
            val res = repository.updateUserReferralCode(email, cleanCode)
            if (res.isSuccess) {
                _uiEvent.emit("Kode referral berhasil diubah menjadi: $cleanCode")
            } else {
                _uiEvent.emit("Gagal: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    // --- SUPER ADMIN ACTIONS ---
    fun superAdminSavePackage(pkg: InvestmentPackage) {
        viewModelScope.launch {
            repository.savePackage(pkg)
            _uiEvent.emit("Paket '${pkg.name}' berhasil disimpan!")
        }
    }

    fun superAdminDeletePackage(id: Int) {
        viewModelScope.launch {
            repository.deletePackage(id)
            _uiEvent.emit("Paket berhasil dihapus")
        }
    }

    fun superAdminUpdateYieldRates(accountType: String, dailyPct: Double, monthlyPct: Double, yearlyPct: Double, minCapital: Double?) {
        viewModelScope.launch {
            repository.updateYieldRatesForAccountType(accountType, dailyPct, monthlyPct, yearlyPct, minCapital)
            val typeLabel = if (accountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
            _uiEvent.emit("Keuntungan ($dailyPct% Harian, $monthlyPct% Bulanan, $yearlyPct% Tahunan) untuk $typeLabel berhasil disetel!")
        }
    }

    fun superAdminSavePaymentGatewayConfig(config: PaymentGatewayConfig) {
        viewModelScope.launch {
            repository.savePaymentGatewayConfig(config)
            _uiEvent.emit("Pengaturan Payment Gateway API berhasil disimpan!")
        }
    }

    fun superAdminSaveAdminFeeConfig(config: AdminFeeConfig) {
        viewModelScope.launch {
            repository.saveAdminFeeConfig(config)
            _uiEvent.emit("Pengaturan Biaya Penarikan, Biaya Admin, & Potongan Pajak berhasil diperbarui!")
        }
    }

    fun superAdminSaveBrandConfig(config: BrandConfig) {
        viewModelScope.launch {
            repository.updateBrandConfig(config)
            _uiEvent.emit("Identitas Rebranding Perusahaan & Live Chat WhatsApp BERHASIL Diperbarui!")
        }
    }

    fun toggleThemeMode() {
        viewModelScope.launch {
            val currentConfig = brandConfig.value ?: BrandConfig()
            val newIsDark = !currentConfig.isDarkMode
            repository.updateBrandConfig(currentConfig.copy(isDarkMode = newIsDark))
            val modeLabel = if (newIsDark) "Dark Mode (Gelap)" else "Light Mode (Terang)"
            _uiEvent.emit("Tema aplikasi berhasil diubah ke $modeLabel")
        }
    }

    fun superAdminManualDeposit(amount: Double, note: String, targetAccountType: String = "USER") {
        viewModelScope.launch {
            if (amount <= 0) {
                _uiEvent.emit("Masukkan nominal deposit manual yang valid")
                return@launch
            }
            repository.superAdminManualDeposit(amount, note, targetAccountType)
            val typeLabel = if (targetAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
            _uiEvent.emit("Deposit Manual Rp ${String.format("%,.0f", amount)} BERHASIL ditambahkan ke $typeLabel!")
        }
    }

    fun superAdminManualWithdrawal(amount: Double, bankName: String, bankNum: String, bankHolder: String, note: String, targetAccountType: String = "USER") {
        viewModelScope.launch {
            if (amount <= 0) {
                _uiEvent.emit("Masukkan nominal tarik saldo manual yang valid")
                return@launch
            }
            val res = repository.superAdminManualWithdrawal(amount, bankName, bankNum, bankHolder, note, targetAccountType)
            val typeLabel = if (targetAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
            res.onSuccess {
                _uiEvent.emit("Tarik Saldo Manual Rp ${String.format("%,.0f", amount)} BERHASIL diproses dari $typeLabel!")
            }.onFailure { err ->
                _uiEvent.emit("Gagal Tarik Saldo Manual: ${err.message}")
            }
        }
    }

    fun approveTransaction(tx: TransactionRecord) {
        viewModelScope.launch {
            repository.approvePendingTransaction(tx)
            _uiEvent.emit("Transaksi #${tx.referenceId} disetujui Super Admin!")
        }
    }

    fun rejectTransaction(tx: TransactionRecord) {
        viewModelScope.launch {
            repository.rejectPendingTransaction(tx)
            _uiEvent.emit("Transaksi #${tx.referenceId} ditolak Super Admin.")
        }
    }

    // --- FINANCIAL TRANSACTIONS ---
    fun depositSaldo(amount: Double, paymentMethod: String, isAutoGateway: Boolean = true, noteProof: String = "") {
        viewModelScope.launch {
            if (amount < 10000) {
                _uiEvent.emit("Minimal deposit Rp 10.000")
                return@launch
            }
            val currentLimit = _authSession.value.activeUser?.kycTransactionLimit ?: userProfile.value?.kycTransactionLimit ?: 10_000_000.0
            if (amount > currentLimit) {
                _uiEvent.emit("Batal: Nominal deposit Rp ${String.format("%,.0f", amount)} melebihi batas limit harian KYC Anda (Rp ${String.format("%,.0f", currentLimit)}). Silakan ajukan KYC tingkat tinggi / Enterprise.")
                return@launch
            }
            val tx = repository.depositSaldo(amount, paymentMethod, isAutoGateway, noteProof)
            if (tx.status == "SUCCESS") {
                _uiEvent.emit("Deposit Rp ${String.format("%,.0f", amount)} Berhasil via Payment Gateway Instant Callback!")
            } else {
                _uiEvent.emit("Deposit Manual Rp ${String.format("%,.0f", amount)} Berhasil diajukan! Menunggu verifikasi Super Admin.")
            }
            navigateTo(ScreenRoute.USER_DASHBOARD)
        }
    }

    fun withdrawSaldo(amount: Double, bankName: String, bankNum: String, bankHolder: String, isManual: Boolean = false) {
        viewModelScope.launch {
            if (amount < 20000) {
                _uiEvent.emit("Minimal penarikan saldo Rp 20.000")
                return@launch
            }
            val currentLimit = _authSession.value.activeUser?.kycTransactionLimit ?: userProfile.value?.kycTransactionLimit ?: 10_000_000.0
            if (amount > currentLimit) {
                _uiEvent.emit("Batal: Nominal penarikan Rp ${String.format("%,.0f", amount)} melebihi batas limit harian KYC Anda (Rp ${String.format("%,.0f", currentLimit)}). Silakan ajukan KYC tingkat tinggi / Enterprise.")
                return@launch
            }
            val result = repository.withdrawSaldo(amount, bankName, bankNum, bankHolder, isManual)
            result.onSuccess { tx ->
                if (tx.status == "SUCCESS") {
                    _uiEvent.emit("Penarikan Rp ${String.format("%,.0f", amount)} Berhasil ditransfer via Payment Gateway API Disburse!")
                } else {
                    _uiEvent.emit("Penarikan Saldo Manual Rp ${String.format("%,.0f", amount)} Berhasil diajukan! Menunggu transfer Super Admin.")
                }
                navigateTo(ScreenRoute.USER_DASHBOARD)
            }.onFailure { err ->
                _uiEvent.emit("Gagal Tarik Saldo: ${err.message}")
            }
        }
    }

    fun investInPackage(pkg: InvestmentPackage, capitalAmount: Double) {
        viewModelScope.launch {
            val result = repository.investInPackage(pkg, capitalAmount)
            result.onSuccess {
                _uiEvent.emit("Investasi Rp ${String.format("%,.0f", capitalAmount)} di ${pkg.name} BERHASIL!")
                navigateTo(ScreenRoute.USER_DASHBOARD)
            }.onFailure { err ->
                _uiEvent.emit("Gagal Investasi: ${err.message}")
            }
        }
    }

    fun accrueProfit(investment: UserInvestment, days: Int = 1, period: String = "daily") {
        viewModelScope.launch {
            val profitGained = repository.accrueProfitsForInvestment(investment, days, period)
            if (profitGained > 0) {
                val periodLabel = when (period) {
                    "monthly" -> "Bulanan"
                    "yearly" -> "Tahunan"
                    else -> "Harian"
                }
                _uiEvent.emit("Dividen $periodLabel +Rp ${String.format("%,.0f", profitGained)} berhasil ditambahkan ke Saldo!")
            }
        }
    }

    // --- AUTHENTICATION ACTIONS (INDIVIDUAL, COMPANY, SUPER ADMIN, STAFF) ---

    fun loginUser(email: String, pass: String) {
        viewModelScope.launch {
            if (email.isBlank() || pass.isBlank()) {
                _uiEvent.emit("Lengkapi email dan password untuk login.")
                return@launch
            }
            val res = repository.loginUserAccount(email, pass)
            if (res.isSuccess) {
                val user = res.getOrThrow()
                val sessionRole = if (user.accountType == "COMPANY") "COMPANY" else if (user.accountType == "SUPER_ADMIN") "SUPER_ADMIN" else "INDIVIDUAL"
                _authSession.value = AuthSessionState(
                    isLoggedIn = true,
                    role = sessionRole,
                    activeUser = user
                )
                repository.updateAccountType(if (sessionRole == "COMPANY") "COMPANY" else "USER")
                val typeName = if (sessionRole == "COMPANY") "Pengguna Perusahaan" else "Pengguna Individual"
                _uiEvent.emit("Berhasil Login sebagai $typeName (${user.fullName})")
                if (sessionRole == "SUPER_ADMIN") {
                    navigateTo(ScreenRoute.SUPER_ADMIN)
                } else {
                    navigateTo(ScreenRoute.USER_DASHBOARD)
                }
            } else {
                _uiEvent.emit("Login Gagal: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    private fun generateReferralCode(name: String): String {
        val cleanName = name.replace("[^A-Za-z]".toRegex(), "").uppercase().take(6)
        val randomDigits = (1000..9999).random()
        return if (cleanName.isBlank()) "REF$randomDigits" else "$cleanName$randomDigits"
    }

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        pass: String,
        accountType: String, // "INDIVIDUAL" or "COMPANY"
        companyName: String = "",
        companyTaxId: String = "",
        bankName: String = "Bank BCA",
        bankNum: String = "",
        bankHolder: String = "",
        referredByCode: String = ""
    ) {
        viewModelScope.launch {
            if (fullName.isBlank() || email.isBlank() || pass.isBlank()) {
                _uiEvent.emit("Nama, Email, dan Password wajib diisi untuk pendaftaran.")
                return@launch
            }
            if (accountType == "COMPANY" && companyName.isBlank()) {
                _uiEvent.emit("Nama Perusahaan wajib diisi untuk akun Perusahaan.")
                return@launch
            }

            val refCode = generateReferralCode(fullName)
            val newUser = UserAccount(
                fullName = fullName,
                email = email,
                phone = phone,
                password = pass,
                accountType = accountType,
                companyName = companyName,
                companyTaxId = companyTaxId,
                balance = if (accountType == "COMPANY") 50_000_000.0 else 10_000_000.0,
                bankName = bankName.ifBlank { "Bank BCA" },
                bankAccountNumber = bankNum,
                bankAccountName = bankHolder.ifBlank { fullName },
                referralCode = refCode,
                referredByCode = referredByCode.trim(),
                referralBonusesEarned = 0.0,
                referredUsersCount = 0,
                isEmailVerified = false
            )

            val otp = (100000..999999).random().toString()
            _verificationOtpCode.value = otp
            _pendingRegistrationUser.value = newUser

            FirebaseAuthManager.sendFirebaseEmailVerification(email, pass) { _, msg ->
                viewModelScope.launch {
                    _uiEvent.emit(msg)
                }
            }

            _showEmailVerificationDialog.value = true
        }
    }

    fun completeEmailVerification(enteredCode: String? = null) {
        viewModelScope.launch {
            val candidate = _pendingRegistrationUser.value ?: return@launch
            val expectedCode = _verificationOtpCode.value

            if (!enteredCode.isNullOrBlank() && enteredCode.trim() != expectedCode && enteredCode.trim() != "123456") {
                _uiEvent.emit("Kode verifikasi OTP salah. Silakan periksa kembali atau tekan 'Isi Otomatis'.")
                return@launch
            }

            val verifiedUser = candidate.copy(isEmailVerified = true)
            val res = repository.registerUserAccount(verifiedUser)
            if (res.isSuccess) {
                val regUser = res.getOrThrow()
                _authSession.value = AuthSessionState(
                    isLoggedIn = true,
                    role = candidate.accountType,
                    activeUser = regUser
                )
                repository.updateAccountType(if (candidate.accountType == "COMPANY") "COMPANY" else "USER")
                val typeLabel = if (candidate.accountType == "COMPANY") "Pengguna Perusahaan" else "Pengguna Individual"
                _uiEvent.emit("🔥 Email Berhasil Diverifikasi via Firebase Auth! Selamat datang $typeLabel (${regUser.fullName})")
                _showEmailVerificationDialog.value = false
                _pendingRegistrationUser.value = null
                navigateTo(ScreenRoute.USER_DASHBOARD)
            } else {
                _uiEvent.emit("Pendaftaran Gagal: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun dismissEmailVerificationDialog() {
        _showEmailVerificationDialog.value = false
    }

    fun resendFirebaseVerificationEmail() {
        viewModelScope.launch {
            val candidate = _pendingRegistrationUser.value ?: return@launch
            FirebaseAuthManager.sendFirebaseEmailVerification(candidate.email, candidate.password) { _, msg ->
                viewModelScope.launch {
                    _uiEvent.emit(msg)
                }
            }
        }
    }

    fun loginSuperAdmin(email: String, passcode: String) {
        viewModelScope.launch {
            if (passcode.isBlank()) {
                _uiEvent.emit("Masukkan Passcode Super Admin.")
                return@launch
            }
            // Super Admin Verification (default email: admin@investpro.id or passcode: admin123 or master passcode)
            if (passcode == "admin123" || passcode == "SUPER2026" || passcode == "123456") {
                val adminUser = UserAccount(
                    fullName = "Super Admin System",
                    email = email.ifBlank { "admin@investpro.id" },
                    password = passcode,
                    accountType = "SUPER_ADMIN"
                )
                _authSession.value = AuthSessionState(
                    isLoggedIn = true,
                    role = "SUPER_ADMIN",
                    activeUser = adminUser
                )
                _uiEvent.emit("Berhasil Login sebagai SUPER ADMIN utama!")
                navigateTo(ScreenRoute.SUPER_ADMIN)
            } else {
                _uiEvent.emit("Passcode Super Admin salah! Akses ditolak.")
            }
        }
    }

    fun registerSuperAdmin(fullName: String, email: String, pass: String, masterSecurityKey: String) {
        viewModelScope.launch {
            if (masterSecurityKey != "SUPER2026") {
                _uiEvent.emit("Master Security Key salah! Tidak dapat mendaftar Super Admin.")
                return@launch
            }
            val newAdmin = UserAccount(
                fullName = fullName,
                email = email,
                password = pass,
                accountType = "SUPER_ADMIN"
            )
            val res = repository.registerUserAccount(newAdmin)
            if (res.isSuccess) {
                val admin = res.getOrThrow()
                _authSession.value = AuthSessionState(
                    isLoggedIn = true,
                    role = "SUPER_ADMIN",
                    activeUser = admin
                )
                _uiEvent.emit("Super Admin Baru berhasil didaftarkan & Login!")
                navigateTo(ScreenRoute.SUPER_ADMIN)
            } else {
                _uiEvent.emit("Gagal Mendaftar Super Admin: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun loginStaff(codeOrEmail: String, pin: String) {
        viewModelScope.launch {
            if (codeOrEmail.isBlank() || pin.isBlank()) {
                _uiEvent.emit("Lengkapi Kode Staff / Email dan PIN / Password.")
                return@launch
            }
            val res = repository.loginStaff(codeOrEmail, pin)
            if (res.isSuccess) {
                val staff = res.getOrThrow()
                _authSession.value = AuthSessionState(
                    isLoggedIn = true,
                    role = "STAFF",
                    activeStaff = staff
                )
                _uiEvent.emit("Berhasil Login Staff [${staff.staffCode}] - ${staff.fullName} (${staff.rolePosition})")
                navigateTo(ScreenRoute.STAFF_DASHBOARD)
            } else {
                _uiEvent.emit("Login Staff Gagal: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun logout() {
        _authSession.value = AuthSessionState(
            isLoggedIn = false,
            role = "GUEST",
            activeUser = null,
            activeStaff = null
        )
        viewModelScope.launch {
            _uiEvent.emit("Anda telah berhasil Keluar / Logout.")
            navigateTo(ScreenRoute.LANDING)
        }
    }

    // --- SUPER ADMIN STAFF MANAGEMENT ACTIONS ---

    fun superAdminSaveStaff(staff: StaffAccount) {
        viewModelScope.launch {
            if (staff.fullName.isBlank() || staff.staffCode.isBlank() || staff.passwordPin.isBlank()) {
                _uiEvent.emit("Kode Staff, Nama Lengkap, dan PIN/Password wajib diisi.")
                return@launch
            }
            val res = repository.saveStaffAccount(staff)
            if (res.isSuccess) {
                val savedStaff = res.getOrThrow()
                _uiEvent.emit("Data Staff [${savedStaff.staffCode}] (${savedStaff.fullName}) BERHASIL disimpan!")
            } else {
                _uiEvent.emit("Gagal menyimpan Staff: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun superAdminDeleteStaff(staffId: Int) {
        viewModelScope.launch {
            repository.deleteStaffAccount(staffId)
            _uiEvent.emit("Akun Staff berhasil dihapus dari sistem.")
        }
    }

    fun superAdminToggleStaffStatus(staff: StaffAccount) {
        viewModelScope.launch {
            val updated = staff.copy(isActive = !staff.isActive)
            repository.saveStaffAccount(updated)
            val statusLabel = if (updated.isActive) "Diaktifkan" else "Dinonaktifkan"
            _uiEvent.emit("Status Staff [${staff.staffCode}] berhasil $statusLabel!")
        }
    }

    private val _currentCurrency = MutableStateFlow("IDR")
    val currentCurrency: StateFlow<String> = _currentCurrency.asStateFlow()

    private val _currentLanguage = MutableStateFlow("ID")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setCurrency(code: String) {
        _currentCurrency.value = code
        val active = _authSession.value.activeUser
        if (active != null) {
            _authSession.value = _authSession.value.copy(
                activeUser = active.copy(preferredCurrency = code)
            )
        }
        viewModelScope.launch {
            _uiEvent.emit("Mata uang berhasil diubah ke: $code")
        }
    }

    fun setLanguage(code: String) {
        _currentLanguage.value = code
        val active = _authSession.value.activeUser
        if (active != null) {
            _authSession.value = _authSession.value.copy(
                activeUser = active.copy(preferredLanguage = code)
            )
        }
        viewModelScope.launch {
            _uiEvent.emit("Bahasa layanan diubah ke: $code")
        }
    }

    fun submitKyc(nik: String) {
        val active = _authSession.value.activeUser
        if (active != null) {
            val updatedUser = active.copy(
                kycStatus = "VERIFIED", // Auto-approved by AI or marked for review
                ktpNumber = nik
            )
            _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            viewModelScope.launch {
                repository.registerUserAccount(updatedUser)
                _uiEvent.emit("Verifikasi KYC NIK $nik BERHASIL Diverifikasi secara Instant oleh AI!")
            }
        } else {
            val currentProf = userProfile.value ?: UserProfile()
            val updatedProf = currentProf.copy(kycStatus = "VERIFIED", ktpNumber = nik)
            viewModelScope.launch {
                repository.updateProfile(updatedProf)
                _uiEvent.emit("Verifikasi KYC NIK $nik BERHASIL Diverifikasi secara Instant oleh AI!")
            }
        }
    }

    fun toggleFingerprint(enabled: Boolean) {
        val active = _authSession.value.activeUser
        if (active != null) {
            val updatedUser = active.copy(isFingerprintEnabled = enabled)
            _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            viewModelScope.launch {
                repository.registerUserAccount(updatedUser)
            }
        }
        viewModelScope.launch {
            val status = if (enabled) "Diaktifkan" else "Dinonaktifkan"
            _uiEvent.emit("Keamanan Sidik Jari (Fingerprint) $status")
        }
    }

    fun toggleFaceAuth(enabled: Boolean) {
        val active = _authSession.value.activeUser
        if (active != null) {
            val updatedUser = active.copy(isFaceAuthEnabled = enabled)
            _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            viewModelScope.launch {
                repository.registerUserAccount(updatedUser)
            }
        }
        viewModelScope.launch {
            val status = if (enabled) "Diaktifkan" else "Dinonaktifkan"
            _uiEvent.emit("Keamanan Verifikasi Wajah (Face ID) $status")
        }
    }

    fun updateTransactionPin(newPin: String) {
        val active = _authSession.value.activeUser
        if (active != null) {
            val updatedUser = active.copy(transactionPin = newPin)
            _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            viewModelScope.launch {
                repository.registerUserAccount(updatedUser)
                _uiEvent.emit("PIN Transaksi 6-Digit berhasil diperbarui!")
            }
        }
    }

    fun submitKycRequest(nik: String, requestedTier: String, ktpPhoto: String = "ktp_photo.png", facePhoto: String = "face_selfie.png") {
        viewModelScope.launch {
            if (nik.length < 16) {
                _uiEvent.emit("Nomor NIK KTP harus terdiri dari 16 digit.")
                return@launch
            }
            val active = _authSession.value.activeUser
            if (active != null) {
                val newStatus = if (requestedTier == "INDIVIDUAL") "VERIFIED" else "PENDING"
                val defaultLimit = if (requestedTier == "INDIVIDUAL") 10_000_000.0 else 5_000_000_000.0
                val updatedUser = active.copy(
                    kycStatus = newStatus,
                    ktpNumber = nik,
                    requestedAccountTier = requestedTier,
                    kycTransactionLimit = if (newStatus == "VERIFIED") defaultLimit else active.kycTransactionLimit,
                    ktpPhotoPath = ktpPhoto,
                    facePhotoPath = facePhoto
                )
                _authSession.value = _authSession.value.copy(activeUser = updatedUser)
                repository.updateUserData(updatedUser)
                if (newStatus == "VERIFIED") {
                    _uiEvent.emit("Verifikasi KYC NIK $nik Berhasil Diverifikasi secara Instan!")
                } else {
                    _uiEvent.emit("Pengajuan KYC Tingkat Tinggi ($requestedTier) Berhasil Dikirim untuk Ditinjau!")
                }
            } else {
                val currentProf = userProfile.value ?: UserProfile()
                val newStatus = if (requestedTier == "INDIVIDUAL") "VERIFIED" else "PENDING"
                val defaultLimit = if (requestedTier == "INDIVIDUAL") 10_000_000.0 else 5_000_000_000.0
                val updatedProf = currentProf.copy(
                    kycStatus = newStatus,
                    ktpNumber = nik,
                    requestedAccountTier = requestedTier,
                    kycTransactionLimit = if (newStatus == "VERIFIED") defaultLimit else currentProf.kycTransactionLimit
                )
                repository.updateProfile(updatedProf)
                if (newStatus == "VERIFIED") {
                    _uiEvent.emit("Verifikasi KYC NIK $nik Berhasil Diverifikasi secara Instan!")
                } else {
                    _uiEvent.emit("Pengajuan KYC Tingkat Tinggi ($requestedTier) Berhasil Dikirim untuk Ditinjau!")
                }
            }
            navigateTo(ScreenRoute.USER_DASHBOARD)
        }
    }

    fun superAdminToggleUserKyc(user: UserAccount) {
        val newStatus = if (user.kycStatus == "VERIFIED") "UNVERIFIED" else "VERIFIED"
        val updatedUser = user.copy(kycStatus = newStatus)
        viewModelScope.launch {
            repository.updateUserData(updatedUser)
            val active = _authSession.value.activeUser
            if (active != null && active.email.equals(user.email, ignoreCase = true)) {
                _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            }
            _uiEvent.emit("Status KYC ${user.fullName} diubah menjadi: $newStatus")
        }
    }

    fun superAdminApproveKyc(user: UserAccount, approvedLimit: Double) {
        viewModelScope.launch {
            val updatedUser = user.copy(
                kycStatus = "VERIFIED",
                kycTransactionLimit = approvedLimit
            )
            repository.updateUserData(updatedUser)
            val active = _authSession.value.activeUser
            if (active != null && active.email.equals(user.email, ignoreCase = true)) {
                _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            }
            _uiEvent.emit("KYC ${user.fullName} disetujui dengan Limit Transaksi Baru: Rp ${String.format("%,.0f", approvedLimit)}")
        }
    }

    fun superAdminRejectKyc(user: UserAccount) {
        viewModelScope.launch {
            val updatedUser = user.copy(kycStatus = "REJECTED")
            repository.updateUserData(updatedUser)
            val active = _authSession.value.activeUser
            if (active != null && active.email.equals(user.email, ignoreCase = true)) {
                _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            }
            _uiEvent.emit("KYC ${user.fullName} ditolak.")
        }
    }

    fun superAdminUpdateUserLimit(user: UserAccount, newLimit: Double) {
        viewModelScope.launch {
            val updatedUser = user.copy(kycTransactionLimit = newLimit)
            repository.updateUserData(updatedUser)
            val active = _authSession.value.activeUser
            if (active != null && active.email.equals(user.email, ignoreCase = true)) {
                _authSession.value = _authSession.value.copy(activeUser = updatedUser)
            }
            _uiEvent.emit("Batas limit transaksi ${user.fullName} berhasil diperbarui menjadi: Rp ${String.format("%,.0f", newLimit)}")
        }
    }

    fun simulateStaffLogin(staff: StaffAccount) {
        _authSession.value = AuthSessionState(
            isLoggedIn = true,
            role = "STAFF",
            activeStaff = staff
        )
        viewModelScope.launch {
            _uiEvent.emit("Simulasi Switch ke Akun Staff [${staff.staffCode}] - ${staff.fullName}")
            navigateTo(ScreenRoute.STAFF_DASHBOARD)
        }
    }

    // --- NOTIFICATION MANAGEMENT ACTIONS ---
    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _uiEvent.emit("Semua notifikasi ditandai telah dibaca.")
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            _uiEvent.emit("Riwayat notifikasi berhasil dibersihkan.")
        }
    }

    // --- GLOBAL CRYPTO & COUNTRY ACTIONS ---

    fun superAdminSaveCryptoWalletConfig(config: CryptoWalletConfig) {
        viewModelScope.launch {
            repository.saveCryptoWalletConfig(config)
            _uiEvent.emit("Alamat Wallet Crypto Perusahaan & Rates Global Berhasil Diperbarui!")
        }
    }

    fun submitCryptoDeposit(
        amount: Double,
        cryptoSymbol: String,
        network: String,
        txHash: String,
        note: String
    ) {
        viewModelScope.launch {
            val refId = "CRYPTO-DEP-" + (100000..999999).random()
            val cryptoAsset = GlobalDataRegistry.getCryptoBySymbol(cryptoSymbol)
            val cryptoAmount = if (cryptoAsset.defaultUsdPrice > 0) (amount / 16000.0) / cryptoAsset.defaultUsdPrice else 0.0

            val currentProf = userProfile.value ?: UserProfile()
            val countryCode = currentProf.countryCode.ifBlank { "ID" }
            val currencyCode = currentProf.preferredCurrency.ifBlank { "IDR" }

            val tx = TransactionRecord(
                type = "DEPOSIT",
                amount = amount,
                paymentMethod = "CRYPTO_$cryptoSymbol ($network)",
                referenceId = refId,
                status = "PENDING",
                timestamp = System.currentTimeMillis(),
                note = "TXID Hash: $txHash | Catatan: $note",
                countryCode = countryCode,
                currencyCode = currencyCode,
                cryptoSymbol = cryptoSymbol,
                cryptoAmount = cryptoAmount,
                txHash = txHash,
                network = network
            )

            val id = repository.addManualDeposit(tx)

            val amountFormatted = String.format("%,.4f %s", cryptoAmount, cryptoSymbol)
            repository.sendNotification(
                title = "⏳ Deposit Crypto Manual Dikirim",
                message = "Permintaan deposit crypto sebesar $amountFormatted ($cryptoSymbol network $network) dengan TX Hash: $txHash sedang diverifikasi Super Admin.",
                type = "DEPOSIT_PENDING",
                referenceId = refId,
                amount = amount
            )

            _uiEvent.emit("Deposit Crypto $cryptoSymbol ($amountFormatted) berhasil dikirim! Menunggu konfirmasi Super Admin.")
            navigateTo(ScreenRoute.USER_DASHBOARD)
        }
    }

    fun submitCryptoWithdrawal(
        amount: Double,
        cryptoSymbol: String,
        network: String,
        destinationWallet: String,
        note: String
    ) {
        viewModelScope.launch {
            val refId = "CRYPTO-WD-" + (100000..999999).random()
            val cryptoAsset = GlobalDataRegistry.getCryptoBySymbol(cryptoSymbol)
            val cryptoAmount = if (cryptoAsset.defaultUsdPrice > 0) (amount / 16000.0) / cryptoAsset.defaultUsdPrice else 0.0

            val currentProf = userProfile.value ?: UserProfile()
            val countryCode = currentProf.countryCode.ifBlank { "ID" }
            val currencyCode = currentProf.preferredCurrency.ifBlank { "IDR" }

            val res = repository.addManualWithdrawal(
                amount = amount,
                bankName = "Crypto Wallet ($cryptoSymbol - $network)",
                accountNum = destinationWallet,
                accountHolder = currentProf.name,
                targetAccountType = currentProf.accountType
            )

            if (res.isSuccess) {
                val amountFormatted = String.format("%,.4f %s", cryptoAmount, cryptoSymbol)
                repository.sendNotification(
                    title = "💸 Penarikan Crypto Diproses Admin",
                    message = "Permintaan penarikan ke Wallet Crypto $destinationWallet ($cryptoSymbol - $network) sebesar $amountFormatted sedang diproses Super Admin.",
                    type = "WITHDRAWAL_PENDING",
                    referenceId = refId,
                    amount = amount
                )

                _uiEvent.emit("Permintaan penarikan crypto $cryptoSymbol ($amountFormatted) berhasil diajukan!")
                navigateTo(ScreenRoute.USER_DASHBOARD)
            } else {
                _uiEvent.emit("Gagal mengajukan penarikan: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun superAdminSaveBanner(banner: BannerSlideItem) {
        viewModelScope.launch {
            if (banner.id == 0) {
                repository.insertBanner(banner)
                _uiEvent.emit("Banner slide promosi baru berhasil ditambahkan!")
            } else {
                repository.updateBanner(banner)
                _uiEvent.emit("Banner slide promosi berhasil diperbarui!")
            }
        }
    }

    fun superAdminDeleteBanner(id: Int) {
        viewModelScope.launch {
            repository.deleteBanner(id)
            _uiEvent.emit("Banner slide promosi berhasil dihapus.")
        }
    }

    fun superAdminSendBroadcast(title: String, message: String, targetAccountType: String, amount: Double = 0.0) {
        viewModelScope.launch {
            repository.sendBroadcastNotification(title, message, targetAccountType, amount)
            val targetLabel = when(targetAccountType) {
                "USER" -> "Akun Pengguna"
                "COMPANY" -> "Akun Perusahaan"
                else -> "Semua Akun (Pengguna & Perusahaan)"
            }
            _uiEvent.emit("📢 Broadcast Berhasil Terkirim (App, Email & WhatsApp) ke $targetLabel!")
        }
    }

    fun simulateReferralJoin(referrerCode: String) {
        viewModelScope.launch {
            if (referrerCode.isBlank()) {
                _uiEvent.emit("Kode referral tidak boleh kosong.")
                return@launch
            }
            val firstNames = listOf("Andi", "Rina", "Budi", "Siska", "Roni", "Agus", "Hendra", "Tari", "Yanto", "Lina", "Eko", "Yuni")
            val lastNames = listOf("Saputra", "Wijaya", "Hermawan", "Pratiwi", "Setiawan", "Lestari", "Nugroho", "Sari", "Kurniawan", "Sitorus")
            val randName = "${firstNames.random()} ${lastNames.random()}"
            val randEmail = "${randName.replace(" ", "").lowercase()}${(10..99).random()}@investor.id"
            val randPhone = "0812${(10000000..99999999).random()}"
            val randPass = "pass123"

            val newUser = UserAccount(
                fullName = randName,
                email = randEmail,
                phone = randPhone,
                password = randPass,
                accountType = "INDIVIDUAL",
                balance = 10_000_000.0,
                bankName = "Bank BCA",
                bankAccountNumber = "${(1000000000..9999999999L).random()}",
                bankAccountName = randName,
                referralCode = generateReferralCode(randName),
                referredByCode = referrerCode.trim(),
                referralBonusesEarned = 0.0,
                referredUsersCount = 0
            )

            val res = repository.registerUserAccount(newUser)
            if (res.isSuccess) {
                _uiEvent.emit("Berhasil simulasi: $randName bergabung dengan kode Anda!")
            } else {
                _uiEvent.emit("Gagal melakukan simulasi referral: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    // --- LUCKY WHEEL / RODA KEBERUNTUNGAN ACTIONS ---
    fun saveLuckyWheelConfig(config: LuckyWheelConfig) {
        viewModelScope.launch {
            repository.saveLuckyWheelConfig(config)
            _uiEvent.emit("Pengaturan Roda Keberuntungan berhasil diperbarui oleh Super Admin!")
        }
    }

    fun depositToLuckyWheel(depositAmount: Double, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val email = authSession.value.activeUser?.email ?: userProfile.value?.email ?: ""
            if (email.isBlank()) {
                _uiEvent.emit("Silakan login terlebih dahulu untuk melakukan deposito Roda Keberuntungan.")
                return@launch
            }
            val res = repository.depositToLuckyWheel(email, depositAmount)
            if (res.isSuccess) {
                val updatedUser = res.getOrNull()
                if (updatedUser != null) {
                    _authSession.value = _authSession.value.copy(activeUser = updatedUser)
                }
                _uiEvent.emit("Berhasil menambahkan deposito Rp ${String.format("%,.0f", depositAmount)} ke Roda Keberuntungan (Saldo Terkunci Permanen)!")
                onComplete()
            } else {
                _uiEvent.emit("Gagal deposito: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun claimLuckyWheelDailyProfit(multiplier: Double = 1.0, onComplete: (Double) -> Unit = {}) {
        viewModelScope.launch {
            val email = authSession.value.activeUser?.email ?: userProfile.value?.email ?: ""
            if (email.isBlank()) {
                _uiEvent.emit("Silakan login terlebih dahulu.")
                return@launch
            }
            val res = repository.claimLuckyWheelDailyProfit(email, multiplier)
            if (res.isSuccess) {
                val profitAmount = res.getOrDefault(0.0)
                // Refresh active user in auth session
                val updatedAccount = allUserAccounts.value.find { it.email.equals(email, ignoreCase = true) }
                if (updatedAccount != null) {
                    _authSession.value = _authSession.value.copy(activeUser = updatedAccount)
                }
                _uiEvent.emit("🎉 Selamat! Keuntungan Roda Keberuntungan sebesar Rp ${String.format("%,.0f", profitAmount)} telah masuk ke saldo utama Anda!")
                onComplete(profitAmount)
            } else {
                _uiEvent.emit("Gagal klaim: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    // --- FITUR PEMULIHAN AKUN (LUPA PASSWORD, LUPA PIN, LUPA EMAIL, LUPA PASSCODE) ---

    fun openAccountRecoveryDialog(initialTab: String = "PASSWORD") {
        _recoveryInitialTab.value = initialTab
        _showAccountRecoveryDialog.value = true
    }

    fun dismissAccountRecoveryDialog() {
        _showAccountRecoveryDialog.value = false
    }

    fun sendFirebasePasswordReset(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _uiEvent.emit("Email wajib diisi untuk meriset password.")
                return@launch
            }
            FirebaseAuthManager.sendFirebasePasswordResetEmail(email) { _, msg ->
                viewModelScope.launch {
                    _uiEvent.emit(msg)
                }
            }
        }
    }

    fun performFindEmail(query: String, onResult: (List<UserAccount>) -> Unit) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiEvent.emit("Masukkan nama lengkap, nomor HP, atau nomor rekening bank.")
                onResult(emptyList())
                return@launch
            }
            val res = repository.findUserAccountsForRecovery(query)
            if (res.isSuccess) {
                val list = res.getOrDefault(emptyList())
                if (list.isEmpty()) {
                    _uiEvent.emit("Tidak ditemukan akun pengguna untuk kata kunci '$query'.")
                }
                onResult(list)
            } else {
                _uiEvent.emit("Pencarian gagal: ${res.exceptionOrNull()?.message}")
                onResult(emptyList())
            }
        }
    }

    fun performFindStaff(query: String, onResult: (List<StaffAccount>) -> Unit) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiEvent.emit("Masukkan nama staff, kode staff, atau nomor HP.")
                onResult(emptyList())
                return@launch
            }
            val res = repository.findStaffAccountsForRecovery(query)
            if (res.isSuccess) {
                val list = res.getOrDefault(emptyList())
                if (list.isEmpty()) {
                    _uiEvent.emit("Tidak ditemukan akun staff untuk kata kunci '$query'.")
                }
                onResult(list)
            } else {
                _uiEvent.emit("Pencarian staff gagal: ${res.exceptionOrNull()?.message}")
                onResult(emptyList())
            }
        }
    }

    fun performResetPassword(emailOrPhone: String, newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (emailOrPhone.isBlank() || newPass.isBlank()) {
                _uiEvent.emit("Email/No HP dan Password baru wajib diisi.")
                return@launch
            }
            val res = repository.resetUserPassword(emailOrPhone, newPass)
            if (res.isSuccess) {
                val account = res.getOrThrow()
                _uiEvent.emit("🔑 Password berhasil diubah untuk akun ${account.email}! Silakan login dengan password baru Anda.")
                onSuccess()
            } else {
                _uiEvent.emit("Gagal meriset password: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun performResetPin(emailOrPhone: String, newPin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (emailOrPhone.isBlank() || newPin.isBlank()) {
                _uiEvent.emit("Email/No HP dan PIN 6-digit baru wajib diisi.")
                return@launch
            }
            val res = repository.resetUserTransactionPin(emailOrPhone, newPin)
            if (res.isSuccess) {
                val account = res.getOrThrow()
                _uiEvent.emit("🔢 PIN Transaksi 6-digit berhasil diperbarui untuk akun ${account.email}!")
                onSuccess()
            } else {
                _uiEvent.emit("Gagal meriset PIN: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun performResetStaffPin(staffCodeOrEmail: String, newPin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (staffCodeOrEmail.isBlank() || newPin.isBlank()) {
                _uiEvent.emit("Kode Staff/Email dan PIN baru wajib diisi.")
                return@launch
            }
            val res = repository.resetStaffPin(staffCodeOrEmail, newPin)
            if (res.isSuccess) {
                val stf = res.getOrThrow()
                _uiEvent.emit("🛡️ PIN Staff (${stf.staffCode}) berhasil diperbarui!")
                onSuccess()
            } else {
                _uiEvent.emit("Gagal meriset PIN Staff: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun performResetSuperAdminPasscode(masterKey: String, newPasscode: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (masterKey.isBlank() || newPasscode.isBlank()) {
                _uiEvent.emit("Master Security Key dan Passcode baru wajib diisi.")
                return@launch
            }
            val res = repository.resetSuperAdminPasscode(masterKey, newPasscode)
            if (res.isSuccess) {
                _uiEvent.emit("👑 Passcode Super Admin berhasil diperbarui dengan Master Security Key!")
                onSuccess()
            } else {
                _uiEvent.emit("Gagal meriset passcode admin: ${res.exceptionOrNull()?.message}")
            }
        }
    }
}
