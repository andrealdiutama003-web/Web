package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.entity.AdminFeeConfig
import com.example.data.entity.AuditLogRecord
import com.example.data.entity.BannerSlideItem
import com.example.data.entity.BrandConfig
import com.example.data.entity.CryptoWalletConfig
import com.example.data.entity.InvestmentPackage
import com.example.data.entity.LuckyWheelConfig
import com.example.data.entity.NotificationItem
import com.example.data.entity.PaymentGatewayConfig
import com.example.data.entity.StaffAccount
import com.example.data.entity.TransactionRecord
import com.example.data.entity.UserAccount
import com.example.data.entity.UserInvestment
import com.example.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

class InvestmentRepository(private val dao: AppDao) {

    val allPackages: Flow<List<InvestmentPackage>> = dao.getAllPackages()
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val userInvestments: Flow<List<UserInvestment>> = dao.getUserInvestments()
    val transactions: Flow<List<TransactionRecord>> = dao.getAllTransactions()
    val paymentGatewayConfig: Flow<PaymentGatewayConfig?> = dao.getPaymentGatewayConfig()
    val adminFeeConfig: Flow<AdminFeeConfig?> = dao.getAdminFeeConfig()
    val notifications: Flow<List<NotificationItem>> = dao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = dao.getUnreadNotificationCount()
    val cryptoWalletConfig: Flow<CryptoWalletConfig?> = dao.getCryptoWalletConfig()
    val luckyWheelConfig: Flow<LuckyWheelConfig?> = dao.getLuckyWheelConfig()
    val auditLogs: Flow<List<AuditLogRecord>> = dao.getAllAuditLogs()

    suspend fun insertAuditLog(actionType: String, description: String, adminEmail: String = "Super Admin") {
        dao.insertAuditLog(
            AuditLogRecord(
                actionType = actionType,
                adminEmail = adminEmail,
                description = description,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveCryptoWalletConfig(config: CryptoWalletConfig) {
        dao.insertOrUpdateCryptoWalletConfig(config)
    }

    suspend fun addManualDeposit(tx: TransactionRecord): Long {
        return dao.insertTransaction(tx)
    }

    suspend fun addManualWithdrawal(
        amount: Double,
        bankName: String,
        accountNum: String,
        accountHolder: String,
        targetAccountType: String = "USER"
    ): Result<TransactionRecord> {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        if (profile.balance < amount) {
            return Result.failure(Exception("Saldo tidak mencukupi untuk penarikan sebesar Rp ${String.format("%,.0f", amount)}"))
        }

        // Deduct balance immediately
        dao.insertOrUpdateProfile(profile.copy(balance = profile.balance - amount))

        val refId = "CRYPTO-WD-" + (100000..999999).random()
        val tx = TransactionRecord(
            type = "WITHDRAWAL",
            amount = amount,
            paymentMethod = "$bankName ($accountNum)",
            referenceId = refId,
            status = "PENDING",
            timestamp = System.currentTimeMillis(),
            note = "Penarikan Crypto ke $bankName ($accountNum a.n $accountHolder) - Menunggu Transfer Super Admin",
            countryCode = profile.countryCode.ifBlank { "ID" },
            currencyCode = profile.preferredCurrency.ifBlank { "IDR" },
            destinationWallet = accountNum
        )
        val txId = dao.insertTransaction(tx)
        return Result.success(tx.copy(id = txId.toInt()))
    }

    suspend fun sendNotification(
        title: String,
        message: String,
        type: String,
        referenceId: String = "",
        amount: Double = 0.0,
        targetAccountType: String = "ALL"
    ) {
        val notification = NotificationItem(
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            referenceId = referenceId,
            amount = amount,
            targetAccountType = targetAccountType
        )
        dao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Int) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()
    suspend fun deleteNotification(id: Int) = dao.deleteNotification(id)
    suspend fun clearAllNotifications() = dao.clearAllNotifications()

    fun getPackagesForAccountType(accountType: String): Flow<List<InvestmentPackage>> {
        return dao.getPackagesByAccountType(accountType)
    }

    // --- USER PROFILE & ACCOUNT TYPE SWITCHING ---
    suspend fun updateAccountType(accountType: String) {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val updated = current.copy(accountType = accountType)
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun updateProfile(profile: UserProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun updateProfile(name: String, email: String, companyName: String, bankName: String, bankNumber: String, bankAccountName: String) {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val updated = current.copy(
            name = name,
            email = email,
            companyName = companyName,
            bankName = bankName,
            bankAccountNumber = bankNumber,
            bankAccountName = bankAccountName
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun updateUserReferralCode(email: String, newCode: String): Result<Unit> {
        val user = dao.getUserByEmail(email) ?: return Result.failure(Exception("Akun tidak ditemukan."))
        
        // Check if code is already used by someone else
        val duplicate = dao.getUserByReferralCode(newCode)
        if (duplicate != null && duplicate.email != email) {
            return Result.failure(Exception("Kode referral '$newCode' sudah digunakan oleh pengguna lain! Silakan pilih kode lain."))
        }
        
        val updatedUser = user.copy(referralCode = newCode)
        dao.updateUserAccount(updatedUser)
        
        // Sync active profile if email matches
        val currentProf = dao.getUserProfile().firstOrNull() ?: UserProfile()
        if (currentProf.email == email) {
            dao.insertOrUpdateProfile(currentProf.copy(referralCode = newCode))
        }
        return Result.success(Unit)
    }

    // --- SUPER ADMIN PACKAGE MANAGEMENT & MODAL & PERCENTAGE SETTINGS ---
    suspend fun savePackage(pkg: InvestmentPackage) {
        if (pkg.id == 0) {
            dao.insertPackage(pkg)
        } else {
            dao.updatePackage(pkg)
        }
    }

    suspend fun deletePackage(id: Int) {
        dao.deletePackage(id)
    }

    // Super Admin Batch Update Modal and Profit Percentages for Account Type
    suspend fun updateYieldRatesForAccountType(
        accountType: String,
        dailyPct: Double,
        monthlyPct: Double,
        yearlyPct: Double,
        minCapital: Double? = null
    ) {
        val packages = dao.getAllPackages().firstOrNull() ?: emptyList()
        packages.filter { it.accountType == accountType || it.accountType == "ALL" }.forEach { pkg ->
            val updatedPkg = pkg.copy(
                dailyReturnPct = dailyPct,
                monthlyReturnPct = monthlyPct,
                yearlyReturnPct = yearlyPct,
                minCapital = minCapital ?: pkg.minCapital
            )
            dao.updatePackage(updatedPkg)
        }
    }

    // --- PAYMENT GATEWAY CONFIG ---
    suspend fun savePaymentGatewayConfig(config: PaymentGatewayConfig) {
        dao.insertOrUpdateGatewayConfig(config)
    }

    // --- ADMIN FEE CONFIG ---
    suspend fun saveAdminFeeConfig(config: AdminFeeConfig) {
        dao.insertOrUpdateFeeConfig(config)
        dao.insertAuditLog(
            AuditLogRecord(
                actionType = "FEE_TAX_CONFIG_UPDATE",
                adminEmail = "Super Admin",
                description = "Memperbarui konfigurasi Biaya & Pajak: Penarikan User Rp ${config.userWithdrawalFee}, Corp Rp ${config.companyWithdrawalFee}, Pajak Profit Personal ${config.userTaxFeePct}%, Corp ${config.companyTaxFeePct}%, Komisi Ref ${config.referralMonthlyCommissionPct}%",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // --- SUPER ADMIN MANUAL DEPOSIT & WITHDRAWAL ---
    suspend fun superAdminManualDeposit(amount: Double, note: String, targetAccountType: String = "USER"): TransactionRecord {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val typeLabel = if (targetAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
        val updatedProfile = if (profile.accountType != targetAccountType) {
            profile.copy(accountType = targetAccountType, balance = profile.balance + amount)
        } else {
            profile.copy(balance = profile.balance + amount)
        }
        val refId = "MAN-DEP-${System.currentTimeMillis().toString().takeLast(6)}"
        val tx = TransactionRecord(
            type = "DEPOSIT",
            amount = amount,
            paymentMethod = "Super Admin Injeksi Manual ($typeLabel)",
            referenceId = refId,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis(),
            note = if (note.isBlank()) "Deposit manual disetujui Super Admin untuk $typeLabel" else "$note ($typeLabel)"
        )
        val id = dao.insertTransaction(tx)
        dao.insertOrUpdateProfile(updatedProfile)

        val amountFormatted = String.format("%,.0f", amount)
        sendNotification(
            title = "⚡ Deposit Manual Disetujui Admin",
            message = "Injeksi deposit manual sebesar Rp $amountFormatted ($typeLabel) telah disetujui & ditambahkan ke saldo akun Anda.",
            type = "MANUAL_DEPOSIT",
            referenceId = refId,
            amount = amount,
            targetAccountType = targetAccountType
        )

        return tx.copy(id = id.toInt())
    }

    suspend fun superAdminManualWithdrawal(
        amount: Double,
        bankName: String,
        accountNum: String,
        accountHolder: String,
        note: String,
        targetAccountType: String = "USER"
    ): Result<TransactionRecord> {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val typeLabel = if (targetAccountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
        val updatedProfile = if (profile.accountType != targetAccountType) {
            profile.copy(accountType = targetAccountType)
        } else {
            profile
        }

        if (updatedProfile.balance < amount) {
            return Result.failure(Exception("Saldo $typeLabel (Rp ${String.format("%,.0f", updatedProfile.balance)}) tidak mencukupi untuk tarik saldo manual Rp ${String.format("%,.0f", amount)}"))
        }

        val refId = "MAN-WD-${System.currentTimeMillis().toString().takeLast(6)}"
        val tx = TransactionRecord(
            type = "WITHDRAWAL",
            amount = amount,
            paymentMethod = "Super Admin Transfer Manual ($typeLabel) ($bankName - $accountNum a.n $accountHolder)",
            referenceId = refId,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis(),
            note = if (note.isBlank()) "Penarikan manual langsung diproses Super Admin untuk $typeLabel" else "$note ($typeLabel)"
        )
        val id = dao.insertTransaction(tx)
        dao.insertOrUpdateProfile(updatedProfile.copy(balance = updatedProfile.balance - amount))

        val amountFormatted = String.format("%,.0f", amount)
        sendNotification(
            title = "💸 Penarikan Saldo Manual Diproses Admin",
            message = "Penarikan saldo sebesar Rp $amountFormatted ($typeLabel) ke $bankName ($accountNum) telah diproses oleh Admin.",
            type = "MANUAL_WITHDRAWAL",
            referenceId = refId,
            amount = amount,
            targetAccountType = targetAccountType
        )

        return Result.success(tx.copy(id = id.toInt()))
    }

    suspend fun approvePendingTransaction(tx: TransactionRecord) {
        if (tx.status == "SUCCESS") return
        val updatedTx = tx.copy(status = "SUCCESS", note = tx.note + " (Disetujui Super Admin)")
        dao.updateTransaction(updatedTx)

        val amountFormatted = String.format("%,.0f", tx.amount)

        if (tx.type == "DEPOSIT") {
            val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
            dao.insertOrUpdateProfile(profile.copy(balance = profile.balance + tx.amount))

            sendNotification(
                title = "⚡ Deposit Disetujui Admin",
                message = "Permintaan deposit sebesar Rp $amountFormatted (Reff: #${tx.referenceId}) telah DISETUJUI oleh Admin. Saldo Anda telah berhasil ditambahkan!",
                type = "DEPOSIT_APPROVED",
                referenceId = tx.referenceId,
                amount = tx.amount
            )
        } else {
            sendNotification(
                title = "💸 Penarikan Saldo Disetujui Admin",
                message = "Permintaan penarikan saldo sebesar Rp $amountFormatted (Reff: #${tx.referenceId}) telah DISETUJUI & ditransfer oleh Admin ke rekening tujuan Anda.",
                type = "WITHDRAWAL_APPROVED",
                referenceId = tx.referenceId,
                amount = tx.amount
            )
        }
    }

    suspend fun rejectPendingTransaction(tx: TransactionRecord) {
        if (tx.status == "FAILED") return
        val updatedTx = tx.copy(status = "FAILED", note = tx.note + " (Ditolak Super Admin)")
        dao.updateTransaction(updatedTx)

        val amountFormatted = String.format("%,.0f", tx.amount)

        if (tx.type == "DEPOSIT") {
            sendNotification(
                title = "❌ Deposit Ditolak Admin",
                message = "Permintaan deposit sebesar Rp $amountFormatted (Reff: #${tx.referenceId}) DITOLAK oleh Admin. Silakan periksa kembali bukti transfer Anda atau hubungi Customer Support.",
                type = "DEPOSIT_REJECTED",
                referenceId = tx.referenceId,
                amount = tx.amount
            )
        } else {
            // If withdrawal was pending and rejected, refund balance back to user
            val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
            dao.insertOrUpdateProfile(profile.copy(balance = profile.balance + tx.amount))

            sendNotification(
                title = "⚠️ Penarikan Saldo Ditolak Admin",
                message = "Permintaan penarikan sebesar Rp $amountFormatted (Reff: #${tx.referenceId}) DITOLAK oleh Admin. Saldo sebesar Rp $amountFormatted telah dikembalikan ke akun Anda.",
                type = "WITHDRAWAL_REJECTED",
                referenceId = tx.referenceId,
                amount = tx.amount
            )
        }
    }

    // --- DEPOSIT SALDO (WITH AUTOMATIC PAYMENT GATEWAY OR MANUAL TRANSFER) ---
    suspend fun depositSaldo(
        amount: Double,
        paymentMethod: String,
        isAutoGateway: Boolean = true,
        noteProof: String = ""
    ): TransactionRecord {
        val config = dao.getPaymentGatewayConfig().firstOrNull() ?: PaymentGatewayConfig()
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val accountLabel = if (profile.accountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
        val refId = "PAY-${if (isAutoGateway) "PG" else "MAN"}-${System.currentTimeMillis().toString().takeLast(6)}-${(100..999).random()}"
        
        val isAutoApproved = isAutoGateway && config.autoProcessDeposit
        val status = if (isAutoApproved) "SUCCESS" else "PENDING"
        
        val baseNote = if (isAutoApproved) 
            "Deposit otomatis dikonfirmasi via ${config.providerName} API ($accountLabel)" 
            else "Deposit manual transfer ($accountLabel) - Menunggu Verifikasi Super Admin"
        val fullNote = if (noteProof.isNotBlank()) "$baseNote | Catatan Bukti: $noteProof" else baseNote

        val tx = TransactionRecord(
            type = "DEPOSIT",
            amount = amount,
            paymentMethod = paymentMethod,
            referenceId = refId,
            status = status,
            timestamp = System.currentTimeMillis(),
            note = fullNote
        )
        
        val txId = dao.insertTransaction(tx)
        
        if (isAutoApproved) {
            dao.insertOrUpdateProfile(profile.copy(balance = profile.balance + amount))
        }
        
        return tx.copy(id = txId.toInt())
    }

    // --- TARIK SALDO / WITHDRAWAL (AUTOMATIC PG PAYOUT OR MANUAL TRANSFER) ---
    suspend fun withdrawSaldo(
        amount: Double,
        bankName: String,
        accountNumber: String,
        accountName: String,
        isManual: Boolean = false
    ): Result<TransactionRecord> {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val accountLabel = if (profile.accountType == "COMPANY") "Akun Perusahaan" else "Akun Pengguna"
        if (profile.balance < amount) {
            return Result.failure(Exception("Saldo $accountLabel tidak mencukupi untuk penarikan sebesar Rp ${String.format("%,.0f", amount)}"))
        }

        val feeConfig = dao.getAdminFeeConfig().firstOrNull() ?: AdminFeeConfig()
        val isCompany = profile.accountType == "COMPANY"

        val withdrawalFee = if (isCompany) feeConfig.companyWithdrawalFee else feeConfig.userWithdrawalFee
        val adminFeePct = if (isCompany) feeConfig.companyAdminFeePct else feeConfig.userAdminFeePct
        val taxFeePct = if (isCompany) feeConfig.companyTaxFeePct else feeConfig.userTaxFeePct

        val adminFee = amount * (adminFeePct / 100.0)
        val taxFee = amount * (taxFeePct / 100.0)
        val totalDeductions = withdrawalFee + adminFee + taxFee
        val netReceived = amount - totalDeductions

        val config = dao.getPaymentGatewayConfig().firstOrNull() ?: PaymentGatewayConfig()
        val refId = "WD-${if (isManual) "MAN" else "PG"}-${System.currentTimeMillis().toString().takeLast(6)}-${(100..999).random()}"
        
        val isAutoApproved = !isManual && config.autoProcessWithdrawal
        val status = if (isAutoApproved) "SUCCESS" else "PENDING"

        // Deduct gross balance immediately from user wallet
        dao.insertOrUpdateProfile(profile.copy(balance = profile.balance - amount))

        val feeNote = "Tarikan ($accountLabel): Rp ${String.format("%,.0f", amount)} | Admin (${adminFeePct}%): -Rp ${String.format("%,.0f", adminFee)} | Pajak (${taxFeePct}%): -Rp ${String.format("%,.0f", taxFee)} | Biaya WD: -Rp ${String.format("%,.0f", withdrawalFee)} | Net Diterima: Rp ${String.format("%,.0f", netReceived)}"

        val methodLabel = if (isManual) "Tarik Saldo Manual Admin ($bankName - $accountNumber a.n $accountName)" else "$bankName ($accountNumber a.n $accountName)"

        val tx = TransactionRecord(
            type = "WITHDRAWAL",
            amount = amount,
            paymentMethod = methodLabel,
            referenceId = refId,
            status = status,
            timestamp = System.currentTimeMillis(),
            note = "$feeNote | ${if (isAutoApproved) "Auto Transfer via ${config.providerName} Disburse API" else "Penarikan Manual Menunggu Transfer Super Admin"}"
        )

        val txId = dao.insertTransaction(tx)
        return Result.success(tx.copy(id = txId.toInt()))
    }

    // --- INVEST IN PACKAGE ---
    suspend fun investInPackage(
        pkg: InvestmentPackage,
        capitalAmount: Double
    ): Result<UserInvestment> {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        
        // Modal Check configured by Super Admin
        if (capitalAmount < pkg.minCapital) {
            return Result.failure(Exception("Modal minimum untuk ${pkg.name} adalah Rp ${String.format("%,.0f", pkg.minCapital)}"))
        }
        if (capitalAmount > pkg.maxCapital) {
            return Result.failure(Exception("Modal maksimum untuk ${pkg.name} adalah Rp ${String.format("%,.0f", pkg.maxCapital)}"))
        }
        if (profile.balance < capitalAmount) {
            return Result.failure(Exception("Saldo dompet Anda tidak cukup. Silakan Deposit terlebih dahulu."))
        }

        // Deduct balance
        dao.insertOrUpdateProfile(profile.copy(balance = profile.balance - capitalAmount))

        // Create User Investment record
        val inv = UserInvestment(
            packageId = pkg.id,
            packageName = pkg.name,
            accountType = profile.accountType,
            capitalAmount = capitalAmount,
            dailyReturnPct = pkg.dailyReturnPct,
            monthlyReturnPct = pkg.monthlyReturnPct,
            yearlyReturnPct = pkg.yearlyReturnPct,
            accruedProfit = 0.0,
            startDate = System.currentTimeMillis(),
            lastAccrualDate = System.currentTimeMillis(),
            status = "ACTIVE"
        )
        val invId = dao.insertInvestment(inv)

        // Log transaction
        dao.insertTransaction(
            TransactionRecord(
                type = "INVESTMENT",
                amount = capitalAmount,
                paymentMethod = "Saldo Dompet",
                referenceId = "INV-PACK-${invId}",
                status = "SUCCESS",
                timestamp = System.currentTimeMillis(),
                note = "Penempatan Modal Investasi pada ${pkg.name}"
            )
        )

        return Result.success(inv.copy(id = invId.toInt()))
    }

    // --- CLAIM / ACCRUE PROFIT ---
    suspend fun accrueProfitsForInvestment(investment: UserInvestment, daysPassed: Int = 1, period: String = "daily"): Double {
        if (investment.status != "ACTIVE") return 0.0

        val feeConfig = dao.getAdminFeeConfig().firstOrNull() ?: AdminFeeConfig()

        val profitGained = when (period) {
            "monthly" -> investment.capitalAmount * (investment.monthlyReturnPct / 100.0) * (daysPassed / 30.0)
            "yearly" -> investment.capitalAmount * (investment.yearlyReturnPct / 100.0) * (daysPassed / 365.0)
            else -> investment.capitalAmount * (investment.dailyReturnPct / 100.0) * daysPassed
        }

        val updatedInv = investment.copy(
            accruedProfit = investment.accruedProfit + profitGained,
            lastAccrualDate = System.currentTimeMillis()
        )
        dao.updateInvestment(updatedInv)

        // Add profit directly to active user's wallet balance
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfile()
        val updatedProfile = profile.copy(balance = profile.balance + profitGained)
        dao.insertOrUpdateProfile(updatedProfile)

        // Also update the matching investor's account record in the database
        val investorAccount = dao.getUserByEmail(profile.email)
        if (investorAccount != null) {
            dao.updateUserAccount(investorAccount.copy(balance = investorAccount.balance + profitGained))
        }

        // Log profit claim
        dao.insertTransaction(
            TransactionRecord(
                type = "PROFIT_CLAIM",
                amount = profitGained,
                paymentMethod = "Sistem Dividend Yield",
                referenceId = "PRF-${investment.id}-${System.currentTimeMillis().toString().takeLast(4)}",
                status = "SUCCESS",
                timestamp = System.currentTimeMillis(),
                note = "Keuntungan ${if (period == "monthly") "bulanan (${investment.monthlyReturnPct}%)" else if (period == "yearly") "tahunan (${investment.yearlyReturnPct}%)" else "harian (${investment.dailyReturnPct}%)"} dari ${investment.packageName}"
            )
        )

        // IF INVESTOR WAS REFERRED BY SOMEONE -> PAY PROFIT-SHARING COMMISSION TO REFERRER
        if (profile.referredByCode.isNotBlank()) {
            val referrerCode = profile.referredByCode.trim()
            val referrerAccount = dao.getUserByReferralCode(referrerCode)
            if (referrerAccount != null) {
                // Determine percentage based on period
                val commissionPct = when (period) {
                    "monthly" -> feeConfig.referralMonthlyCommissionPct
                    "yearly" -> feeConfig.referralYearlyCommissionPct
                    else -> feeConfig.referralDailyCommissionPct
                }

                val commissionAmount = profitGained * (commissionPct / 100.0)

                if (commissionAmount > 0.0) {
                    // Update referrer's balance in user_accounts table
                    val updatedReferrer = referrerAccount.copy(
                        balance = referrerAccount.balance + commissionAmount,
                        referralBonusesEarned = referrerAccount.referralBonusesEarned + commissionAmount
                    )
                    dao.updateUserAccount(updatedReferrer)

                    // Sync active UserProfile if referrer is the active user
                    val activeProfile = dao.getUserProfile().firstOrNull()
                    if (activeProfile != null && activeProfile.email.equals(referrerAccount.email, ignoreCase = true)) {
                        dao.insertOrUpdateProfile(
                            activeProfile.copy(
                                balance = activeProfile.balance + commissionAmount,
                                referralBonusesEarned = activeProfile.referralBonusesEarned + commissionAmount
                            )
                        )
                    }

                    // Log commission transaction
                    dao.insertTransaction(
                        TransactionRecord(
                            type = "REFERRAL_COMMISSION",
                            amount = commissionAmount,
                            paymentMethod = "Bagi Hasil Investasi",
                            referenceId = "COM-${investment.id}-${System.currentTimeMillis().toString().takeLast(4)}",
                            status = "SUCCESS",
                            timestamp = System.currentTimeMillis(),
                            note = "Komisi pembagian hasil ($commissionPct%) dari keuntungan investasi ${profile.name} (${investment.packageName})"
                        )
                    )

                    // Insert notification for referrer
                    dao.insertNotification(
                        NotificationItem(
                            title = "Komisi Bagi Hasil Masuk!",
                            message = "Anda menerima komisi Rp ${String.format("%,.0f", commissionAmount)} ($commissionPct%) dari keuntungan investasi yang diklaim oleh ${profile.name}.",
                            type = "INFO",
                            amount = commissionAmount,
                            targetAccountType = "ALL"
                        )
                    )
                }
            }
        }

        return profitGained
    }

    // --- REBRANDING & BRAND CONFIG ---
    fun getBrandConfig(): Flow<BrandConfig> {
        return dao.getBrandConfig().map { it ?: BrandConfig() }
    }

    suspend fun updateBrandConfig(config: BrandConfig) {
        dao.insertOrUpdateBrandConfig(config)
    }

    // --- USER ACCOUNTS MANAGEMENT ---
    val allUserAccounts: Flow<List<UserAccount>> = dao.getAllUserAccounts()

    suspend fun updateUserData(user: UserAccount): Result<Unit> {
        dao.updateUserAccount(user)
        // If the updated user is the active logged in user, sync their profile as well
        val activeProfile = dao.getUserProfile().firstOrNull()
        if (activeProfile != null && activeProfile.email.equals(user.email, ignoreCase = true)) {
            dao.insertOrUpdateProfile(
                activeProfile.copy(
                    kycStatus = user.kycStatus,
                    ktpNumber = user.ktpNumber,
                    requestedAccountTier = user.requestedAccountTier,
                    kycTransactionLimit = user.kycTransactionLimit,
                    balance = user.balance
                )
            )
        }
        return Result.success(Unit)
    }

    suspend fun registerUserAccount(user: UserAccount): Result<UserAccount> {
        val existing = dao.getUserByEmail(user.email)
        if (existing != null) {
            return Result.failure(Exception("Email '${user.email}' sudah terdaftar! Gunakan email lain."))
        }

        var finalUser = user

        if (user.referredByCode.isNotBlank()) {
            val referrer = dao.getUserByReferralCode(user.referredByCode.trim())
            if (referrer != null) {
                val feeConfig = dao.getAdminFeeConfig().firstOrNull() ?: AdminFeeConfig()
                val isCompany = user.accountType == "COMPANY"
                val baseReferrerBonus = if (isCompany) feeConfig.companyReferralReferrerBonus else feeConfig.individualReferralReferrerBonus
                val baseReferredBonus = if (isCompany) feeConfig.companyReferralReferredBonus else feeConfig.individualReferralReferredBonus
                val taxRatePct = feeConfig.referralTaxPct

                // Calculate Net Bonus after tax
                val taxReferrer = baseReferrerBonus * (taxRatePct / 100.0)
                val netReferrerBonus = baseReferrerBonus - taxReferrer

                val taxReferred = baseReferredBonus * (taxRatePct / 100.0)
                val netReferredBonus = baseReferredBonus - taxReferred

                // Update referrer account and balance
                val updatedReferrer = referrer.copy(
                    balance = referrer.balance + netReferrerBonus,
                    referralBonusesEarned = referrer.referralBonusesEarned + netReferrerBonus,
                    referredUsersCount = referrer.referredUsersCount + 1
                )
                dao.updateUserAccount(updatedReferrer)

                // Sync active UserProfile if referrer is the active user
                val activeProfile = dao.getUserProfile().firstOrNull()
                if (activeProfile != null && activeProfile.email.equals(referrer.email, ignoreCase = true)) {
                    dao.insertOrUpdateProfile(
                        activeProfile.copy(
                            balance = activeProfile.balance + netReferrerBonus,
                            referralBonusesEarned = activeProfile.referralBonusesEarned + netReferrerBonus,
                            referredUsersCount = activeProfile.referredUsersCount + 1
                        )
                    )
                }

                // Add referrer transaction and notification
                dao.insertTransaction(
                    TransactionRecord(
                        type = "REFERRAL_BONUS",
                        amount = netReferrerBonus,
                        paymentMethod = "Sistem Referral",
                        referenceId = "REF-BONUS-${System.currentTimeMillis()}",
                        status = "SUCCESS",
                        note = "Bonus mengundang teman (${user.fullName}) bergabung di platform (Akun ${if (isCompany) "Perusahaan" else "Individu"}). Bersih setelah pajak $taxRatePct%: Rp ${String.format("%,.0f", netReferrerBonus)} (Potongan Pajak: Rp ${String.format("%,.0f", taxReferrer)})"
                    )
                )

                dao.insertNotification(
                    NotificationItem(
                        title = "Bonus Referral Berhasil!",
                        message = "Selamat! Anda mendapatkan bonus Rp ${String.format("%,.0f", netReferrerBonus)} (bersih setelah pajak $taxRatePct%) karena mengundang ${user.fullName} ke platform.",
                        type = "INFO",
                        amount = netReferrerBonus,
                        targetAccountType = "ALL"
                    )
                )

                // Reward new user with welcome bonus
                finalUser = user.copy(
                    balance = user.balance + netReferredBonus,
                    referralBonusesEarned = 0.0,
                    referredUsersCount = 0
                )

                // Add welcome bonus transaction and notification
                dao.insertTransaction(
                    TransactionRecord(
                        type = "REFERRAL_BONUS",
                        amount = netReferredBonus,
                        paymentMethod = "Sistem Referral",
                        referenceId = "REF-WELCOME-${System.currentTimeMillis()}",
                        status = "SUCCESS",
                        note = "Bonus pendaftaran menggunakan kode referral ${user.referredByCode}. Bersih setelah pajak $taxRatePct%: Rp ${String.format("%,.0f", netReferredBonus)} (Potongan Pajak: Rp ${String.format("%,.0f", taxReferred)})"
                    )
                )

                dao.insertNotification(
                    NotificationItem(
                        title = "Bonus Selamat Datang!",
                        message = "Anda mendapatkan bonus pendaftaran Rp ${String.format("%,.0f", netReferredBonus)} (bersih setelah pajak $taxRatePct%) karena mendaftar menggunakan kode referral ${user.referredByCode}!",
                        type = "INFO",
                        amount = netReferredBonus,
                        targetAccountType = "ALL"
                    )
                )
            }
        }

        val id = dao.insertUserAccount(finalUser)
        val registeredUser = finalUser.copy(id = id.toInt())

        // Sync with active UserProfile
        dao.insertOrUpdateProfile(
            UserProfile(
                id = 1,
                name = registeredUser.fullName,
                email = registeredUser.email,
                accountType = if (registeredUser.accountType == "COMPANY") "COMPANY" else "USER",
                companyName = registeredUser.companyName.ifBlank { "PT " + registeredUser.fullName },
                balance = registeredUser.balance,
                bankName = registeredUser.bankName,
                bankAccountNumber = registeredUser.bankAccountNumber,
                bankAccountName = registeredUser.bankAccountName,
                referralCode = registeredUser.referralCode,
                referredByCode = registeredUser.referredByCode,
                referralBonusesEarned = registeredUser.referralBonusesEarned,
                referredUsersCount = registeredUser.referredUsersCount,
                requestedAccountTier = registeredUser.requestedAccountTier,
                kycTransactionLimit = registeredUser.kycTransactionLimit,
                isEmailVerified = registeredUser.isEmailVerified
            )
        )
        return Result.success(registeredUser)
    }

    suspend fun loginUserAccount(email: String, pass: String): Result<UserAccount> {
        val user = dao.getUserByEmail(email)
            ?: return Result.failure(Exception("Email tidak ditemukan. Silakan lakukan pendaftaran terlebih dahulu."))

        if (user.password != pass) {
            return Result.failure(Exception("Password salah. Silakan periksa kembali."))
        }

        // Sync active UserProfile
        dao.insertOrUpdateProfile(
            UserProfile(
                id = 1,
                name = user.fullName,
                email = user.email,
                accountType = if (user.accountType == "COMPANY") "COMPANY" else "USER",
                companyName = user.companyName.ifBlank { "PT " + user.fullName },
                balance = user.balance,
                bankName = user.bankName,
                bankAccountNumber = user.bankAccountNumber,
                bankAccountName = user.bankAccountName,
                referralCode = user.referralCode,
                referredByCode = user.referredByCode,
                referralBonusesEarned = user.referralBonusesEarned,
                referredUsersCount = user.referredUsersCount,
                requestedAccountTier = user.requestedAccountTier,
                kycTransactionLimit = user.kycTransactionLimit
            )
        )
        return Result.success(user)
    }

    // --- STAFF ACCOUNTS MANAGEMENT ---
    val allStaffAccounts: Flow<List<StaffAccount>> = dao.getAllStaffAccounts()

    suspend fun saveStaffAccount(staff: StaffAccount): Result<StaffAccount> {
        if (staff.id == 0) {
            val existing = dao.getStaffByCodeOrEmail(staff.staffCode)
            if (existing != null) {
                return Result.failure(Exception("Kode Staff / Email '${staff.staffCode}' sudah digunakan!"))
            }
            val id = dao.insertStaffAccount(staff)
            return Result.success(staff.copy(id = id.toInt()))
        } else {
            dao.updateStaffAccount(staff)
            return Result.success(staff)
        }
    }

    suspend fun deleteStaffAccount(id: Int) {
        dao.deleteStaffAccount(id)
    }

    suspend fun loginStaff(codeOrEmail: String, pin: String): Result<StaffAccount> {
        val staff = dao.getStaffByCodeOrEmail(codeOrEmail)
            ?: return Result.failure(Exception("Kode Staff / Email tidak ditemukan."))

        if (!staff.isActive) {
            return Result.failure(Exception("Akun Staff [${staff.staffCode}] sedang dinonaktifkan oleh Super Admin."))
        }

        if (staff.passwordPin != pin) {
            return Result.failure(Exception("PIN / Password Staff salah."))
        }

        return Result.success(staff)
    }

    // --- BANNER SLIDES ---
    val allBanners: Flow<List<BannerSlideItem>> = dao.getAllBanners()

    suspend fun insertBanner(banner: BannerSlideItem): Long {
        return dao.insertBanner(banner)
    }

    suspend fun updateBanner(banner: BannerSlideItem) {
        dao.updateBanner(banner)
    }

    suspend fun deleteBanner(id: Int) {
        dao.deleteBanner(id)
    }

    // --- BROADCAST & AUTOMATED NOTIFICATIONS ---
    suspend fun sendBroadcastNotification(title: String, message: String, targetAccountType: String, amount: Double = 0.0) {
        dao.insertNotification(
            NotificationItem(
                title = title,
                message = message,
                type = "BROADCAST",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                amount = amount,
                targetAccountType = targetAccountType
            )
        )
    }

    // --- LUCKY WHEEL / RODA KEBERUNTUNGAN MANAGEMENT ---
    suspend fun saveLuckyWheelConfig(config: LuckyWheelConfig) {
        dao.insertOrUpdateLuckyWheelConfig(config)
        dao.insertAuditLog(
            AuditLogRecord(
                actionType = "LUCKY_WHEEL_CONFIG_UPDATE",
                adminEmail = "Super Admin",
                description = "Memperbarui konfigurasi Roda Keberuntungan: Pajak ${config.taxPct}%, Kuota Harian ${config.dailyClaimQuota}, Tier 1 Ind (${config.individualTier1ProfitPct}%), Tier 1 Corp (${config.companyTier1ProfitPct}%), Status Fitur: ${if (config.isWheelEnabled) "Aktif" else "Nonaktif"}",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun depositToLuckyWheel(email: String, depositAmount: Double): Result<UserAccount> {
        val user = dao.getUserByEmail(email)
            ?: return Result.failure(Exception("Akun pengguna tidak ditemukan."))

        if (depositAmount <= 0) {
            return Result.failure(Exception("Jumlah deposito harus lebih dari Rp 0."))
        }

        if (user.balance < depositAmount) {
            return Result.failure(Exception("Saldo dompet utama tidak mencukupi (Rp ${String.format("%,.0f", user.balance)}). Silakan deposit terlebih dahulu."))
        }

        // Deduct from main balance, add to non-withdrawable wheel deposit balance ("timpa menimpa")
        val updatedUser = user.copy(
            balance = user.balance - depositAmount,
            wheelDepositBalance = user.wheelDepositBalance + depositAmount
        )

        dao.updateUserAccount(updatedUser)

        // Sync active user profile if email matches
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile != null && currentProfile.email.equals(email, ignoreCase = true)) {
            dao.insertOrUpdateProfile(currentProfile.copy(balance = updatedUser.balance))
        }

        // Record transaction
        val refId = "WHEEL-DEP-${System.currentTimeMillis().toString().takeLast(6)}"
        val tx = TransactionRecord(
            type = "WHEEL_DEPOSIT",
            amount = depositAmount,
            paymentMethod = "Saldo Dompet Utama",
            referenceId = refId,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis(),
            note = "Alokasi Deposito Roda Keberuntungan (Saldo Terkunci Permanen, Tidak Dapat Ditarik Kembali). Total Deposito Roda: Rp ${String.format("%,.0f", updatedUser.wheelDepositBalance)}"
        )
        dao.insertTransaction(tx)

        // Notification
        dao.insertNotification(
            NotificationItem(
                title = "🎡 Deposito Roda Keberuntungan Berhasil!",
                message = "Anda telah menambahkan Rp ${String.format("%,.0f", depositAmount)} ke Roda Keberuntungan. Saldo Roda Keberuntungan Anda sekarang Rp ${String.format("%,.0f", updatedUser.wheelDepositBalance)} (Terkunci Permanen).",
                type = "INFO",
                amount = depositAmount,
                targetAccountType = "ALL"
            )
        )

        return Result.success(updatedUser)
    }

    suspend fun claimLuckyWheelDailyProfit(email: String, bonusMultiplier: Double = 1.0): Result<Double> {
        val user = dao.getUserByEmail(email)
            ?: return Result.failure(Exception("Akun tidak ditemukan."))

        if (user.wheelDepositBalance <= 0) {
            return Result.failure(Exception("Anda belum melakukan Deposito Roda Keberuntungan. Wajib melakukan deposito terlebih dahulu untuk mengikuti Roda Keberuntungan."))
        }

        val config = dao.getLuckyWheelConfig().firstOrNull() ?: LuckyWheelConfig()
        if (!config.isWheelEnabled) {
            return Result.failure(Exception("Fitur Roda Keberuntungan sedang dinonaktifkan oleh Super Admin."))
        }

        // Check daily claim quota reset
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val todayStr = sdf.format(java.util.Date())

        val claimsToday = if (user.lastWheelClaimDate == todayStr) user.wheelClaimsToday else 0

        if (claimsToday >= config.dailyClaimQuota) {
            return Result.failure(Exception("Kuota klaim harian Anda ($claimsToday/${config.dailyClaimQuota}) untuk hari ini sudah habis. Silakan klaim lagi besok!"))
        }

        // Calculate profit percentage based on user account type & wheel deposit tier
        val isCompany = user.accountType == "COMPANY"
        val depBalance = user.wheelDepositBalance

        val profitPct = if (isCompany) {
            when {
                depBalance >= config.companyTier3Min -> config.companyTier3ProfitPct
                depBalance >= config.companyTier2Min -> config.companyTier2ProfitPct
                else -> config.companyTier1ProfitPct
            }
        } else {
            when {
                depBalance >= config.individualTier3Min -> config.individualTier3ProfitPct
                depBalance >= config.individualTier2Min -> config.individualTier2ProfitPct
                else -> config.individualTier1ProfitPct
            }
        }

        // Calculate Gross Profit (with spin bonus multiplier)
        val grossProfit = depBalance * (profitPct / 100.0) * bonusMultiplier
        val taxFee = grossProfit * (config.taxPct / 100.0)
        val netProfit = grossProfit - taxFee

        if (netProfit <= 0) {
            return Result.failure(Exception("Perhitungan keuntungan tidak valid."))
        }

        val updatedUser = user.copy(
            balance = user.balance + netProfit,
            lastWheelClaimDate = todayStr,
            wheelClaimsToday = claimsToday + 1,
            totalWheelProfitClaimed = user.totalWheelProfitClaimed + netProfit
        )

        dao.updateUserAccount(updatedUser)

        // Sync active user profile
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile != null && currentProfile.email.equals(email, ignoreCase = true)) {
            dao.insertOrUpdateProfile(currentProfile.copy(balance = updatedUser.balance))
        }

        // Log transaction record
        val refId = "WHEEL-PRF-${System.currentTimeMillis().toString().takeLast(6)}"
        val tx = TransactionRecord(
            type = "PROFIT_CLAIM",
            amount = netProfit,
            paymentMethod = "Roda Keberuntungan Harian",
            referenceId = refId,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis(),
            note = "Keuntungan Roda Keberuntungan (${String.format("%.2f", profitPct)}% x multiplier ${String.format("%.1f", bonusMultiplier)}x) dari Deposito Rp ${String.format("%,.0f", depBalance)}. Gross: Rp ${String.format("%,.0f", grossProfit)} | Pajak (${config.taxPct}%): -Rp ${String.format("%,.0f", taxFee)} | Net Masuk Saldo: Rp ${String.format("%,.0f", netProfit)}"
        )
        dao.insertTransaction(tx)

        // Notification
        dao.insertNotification(
            NotificationItem(
                title = "🎉 Hasil Roda Keberuntungan Berhasil Diklaim!",
                message = "Selamat! Keuntungan Roda Keberuntungan sebesar Rp ${String.format("%,.0f", netProfit)} (setelah pajak ${config.taxPct}%) telah ditambahkan ke saldo utama Anda.",
                type = "INFO",
                amount = netProfit,
                targetAccountType = "ALL"
            )
        )

        return Result.success(netProfit)
    }

    // --- FITUR PEMULIHAN AKUN (LUPA PASSWORD, PIN, EMAIL, PASSCODE) ---

    suspend fun findUserAccountsForRecovery(query: String): Result<List<UserAccount>> {
        val q = query.trim()
        if (q.isBlank()) return Result.success(emptyList())
        val list = dao.findUserAccountsByKeyword(q)
        return Result.success(list)
    }

    suspend fun findStaffAccountsForRecovery(query: String): Result<List<StaffAccount>> {
        val q = query.trim()
        if (q.isBlank()) return Result.success(emptyList())
        val list = dao.findStaffAccountsByKeyword(q)
        return Result.success(list)
    }

    suspend fun resetUserPassword(emailOrPhone: String, newPass: String): Result<UserAccount> {
        val target = dao.getUserByEmailOrPhone(emailOrPhone.trim())
            ?: return Result.failure(Exception("Akun tidak ditemukan untuk '$emailOrPhone'."))
        
        if (newPass.length < 4) {
            return Result.failure(Exception("Password baru minimal 4 karakter."))
        }

        val updated = target.copy(password = newPass)
        dao.updateUserAccount(updated)

        dao.insertAuditLog(
            AuditLogRecord(
                actionType = "PASSWORD_RESET_SUCCESS",
                adminEmail = target.email,
                description = "Password berhasil diperbarui untuk akun: ${target.email}",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(updated)
    }

    suspend fun resetUserTransactionPin(emailOrPhone: String, newPin: String): Result<UserAccount> {
        val target = dao.getUserByEmailOrPhone(emailOrPhone.trim())
            ?: return Result.failure(Exception("Akun tidak ditemukan untuk '$emailOrPhone'."))

        if (newPin.length != 6 || !newPin.all { it.isDigit() }) {
            return Result.failure(Exception("PIN Transaksi harus terdiri dari 6 angka."))
        }

        val updated = target.copy(transactionPin = newPin)
        dao.updateUserAccount(updated)

        val activeProfile = dao.getUserProfile().firstOrNull()
        if (activeProfile != null && activeProfile.email.equals(target.email, ignoreCase = true)) {
            dao.insertOrUpdateProfile(activeProfile.copy(transactionPin = newPin))
        }

        dao.insertAuditLog(
            AuditLogRecord(
                actionType = "PIN_RESET_SUCCESS",
                adminEmail = target.email,
                description = "PIN Transaksi 6-digit berhasil diperbarui untuk akun: ${target.email}",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(updated)
    }

    suspend fun resetStaffPin(staffCodeOrEmail: String, newPin: String): Result<StaffAccount> {
        val target = dao.getStaffByCodeOrEmail(staffCodeOrEmail.trim())
            ?: return Result.failure(Exception("Akun Staff tidak ditemukan untuk '$staffCodeOrEmail'."))

        if (newPin.length < 4) {
            return Result.failure(Exception("PIN Staff minimal 4 karakter."))
        }

        val updated = target.copy(passwordPin = newPin)
        dao.updateStaffAccount(updated)

        dao.insertAuditLog(
            AuditLogRecord(
                actionType = "STAFF_PIN_RESET",
                adminEmail = target.email,
                description = "PIN Staff berhasil direset untuk kode staff: ${target.staffCode}",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(updated)
    }

    suspend fun resetSuperAdminPasscode(masterKey: String, newPasscode: String): Result<Unit> {
        if (masterKey.trim() != "SUPER2026" && masterKey.trim() != "123456") {
            return Result.failure(Exception("Master Security Key salah! Kunci Keamanan Super Admin diperlukan untuk meriset passcode."))
        }
        if (newPasscode.isBlank()) {
            return Result.failure(Exception("Passcode baru tidak boleh kosong."))
        }

        dao.insertAuditLog(
            AuditLogRecord(
                actionType = "SUPER_ADMIN_PASSCODE_RESET",
                adminEmail = "Super Admin HQ",
                description = "Passcode Super Admin berhasil diperbarui dengan Master Security Key.",
                timestamp = System.currentTimeMillis()
            )
        )

        return Result.success(Unit)
    }
}
