package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.data.entity.TransactionRecord
import com.example.data.entity.UserInvestment
import com.example.data.entity.StaffAccount
import com.example.data.entity.UserAccount
import com.example.data.entity.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        InvestmentPackage::class,
        UserProfile::class,
        UserInvestment::class,
        TransactionRecord::class,
        PaymentGatewayConfig::class,
        AdminFeeConfig::class,
        BrandConfig::class,
        UserAccount::class,
        StaffAccount::class,
        NotificationItem::class,
        CryptoWalletConfig::class,
        BannerSlideItem::class,
        LuckyWheelConfig::class,
        AuditLogRecord::class
    ],
    version = 21,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "investpro_database"
                )
                .fallbackToDestructiveMigration(true)
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.appDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: AppDao) {
                // Default User Profile
                dao.insertOrUpdateProfile(
                    UserProfile(
                        id = 1,
                        name = "Ahmad Pratama",
                        email = "ahmad@investor.id",
                        accountType = "USER",
                        companyName = "PT Investasi Jaya Mandiri",
                        balance = 25_000_000.0,
                        bankName = "Bank BCA",
                        bankAccountNumber = "8830192837",
                        bankAccountName = "Ahmad Pratama",
                        referralCode = "AHMADPRATAMA",
                        referredByCode = "",
                        referralBonusesEarned = 450000.0,
                        referredUsersCount = 3
                    )
                )

                // Default Payment Gateway Config
                dao.insertOrUpdateGatewayConfig(
                    PaymentGatewayConfig(
                        id = 1,
                        providerName = "Midtrans / Xendit Payment Gateway",
                        apiKey = "SB-Mid-server-x891K2mL9A0zP",
                        merchantId = "M10928374",
                        webhookSecret = "whsec_89123891723912837",
                        autoProcessDeposit = true,
                        autoProcessWithdrawal = true,
                        sandboxMode = true
                    )
                )

                // Default Admin Fee Config (Biaya Penarikan, Biaya Admin %, Potongan Pajak %, Referral)
                dao.insertOrUpdateFeeConfig(
                    AdminFeeConfig(
                        id = 1,
                        userWithdrawalFee = 2500.0,
                        userAdminFeePct = 1.0,
                        userTaxFeePct = 5.0,
                        companyWithdrawalFee = 10000.0,
                        companyAdminFeePct = 0.5,
                        companyTaxFeePct = 2.5,
                        individualReferralReferrerBonus = 150000.0,
                        individualReferralReferredBonus = 50000.0,
                        companyReferralReferrerBonus = 300000.0,
                        companyReferralReferredBonus = 100000.0,
                        referralTaxPct = 10.0,
                        referralDailyCommissionPct = 5.0,
                        referralMonthlyCommissionPct = 3.0,
                        referralYearlyCommissionPct = 2.0
                    )
                )

                // Default Lucky Wheel Config (Roda Keberuntungan)
                dao.insertOrUpdateLuckyWheelConfig(
                    LuckyWheelConfig(
                        id = 1,
                        isWheelEnabled = true,
                        dailyClaimQuota = 3,
                        taxPct = 5.0,
                        individualTier1Min = 1.0,
                        individualTier1Max = 10_000_000.0,
                        individualTier1ProfitPct = 1.0,
                        individualTier2Min = 10_000_001.0,
                        individualTier2Max = 50_000_000.0,
                        individualTier2ProfitPct = 1.8,
                        individualTier3Min = 50_000_001.0,
                        individualTier3Max = 1_000_000_000.0,
                        individualTier3ProfitPct = 2.5,
                        companyTier1Min = 1.0,
                        companyTier1Max = 50_000_000.0,
                        companyTier1ProfitPct = 1.8,
                        companyTier2Min = 50_000_001.0,
                        companyTier2Max = 250_000_000.0,
                        companyTier2ProfitPct = 3.0,
                        companyTier3Min = 250_000_001.0,
                        companyTier3Max = 10_000_000_000.0,
                        companyTier3ProfitPct = 4.5
                    )
                )

                // Default Brand Config (Rebranding & Live Chat WhatsApp)
                dao.insertOrUpdateBrandConfig(
                    BrandConfig(
                        id = 1,
                        appName = "InvestPro",
                        companyName = "PT Investasi Jaya Mandiri",
                        tagline = "Platform Investasi & Manajemen Modal Digital Terpercaya",
                        whatsappNumber = "6281234567890",
                        whatsappGreeting = "Halo CS Admin, saya berkonsultasi mengenai investasi di akun saya.",
                        supportEmail = "cs@investpro.co.id",
                        supportAddress = "Equity Tower Lt. 18, SCBD Jakarta Selatan",
                        isLiveChatEnabled = true
                    )
                )

                // Initial Slide Banners
                dao.insertBanner(BannerSlideItem(title = "🚀 Promo Yield Spesial Agustus", subtitle = "Dapatkan bonus return harian +0.5% untuk semua paket investasi aktif.", badgeText = "HOT PROMO", actionLabel = "Klaim Bonus", displayOrder = 1))
                dao.insertBanner(BannerSlideItem(title = "🔒 Keamanan Terjamin & Resmi", subtitle = "Dilindungi enkripsi end-to-end serta pengawasan ketat Super Admin 24/7.", badgeText = "SECURE", actionLabel = "Pelajari", displayOrder = 2))
                dao.insertBanner(BannerSlideItem(title = "💡 Notifikasi Instant Email & WhatsApp", subtitle = "Semua mutasi deposit & penarikan otomatis terkirim langsung ke akun & WhatsApp Anda.", badgeText = "NEW", actionLabel = "Cek Pengaturan", displayOrder = 3))

                // Initial Investment Packages set by Super Admin
                val defaultPackages = listOf(
                    InvestmentPackage(
                        name = "Paket Starter (Pengguna)",
                        description = "Paket investasi harian fleksibel untuk akun personal.",
                        accountType = "USER",
                        minCapital = 100_000.0,
                        maxCapital = 25_000_000.0,
                        dailyReturnPct = 0.8,
                        monthlyReturnPct = 24.0,
                        yearlyReturnPct = 288.0,
                        durationDays = 30
                    ),
                    InvestmentPackage(
                        name = "Paket Premier (Pengguna)",
                        description = "Modal menengah dengan yield harian & bulanan lebih tinggi.",
                        accountType = "USER",
                        minCapital = 5_000_000.0,
                        maxCapital = 100_000_000.0,
                        dailyReturnPct = 1.2,
                        monthlyReturnPct = 36.0,
                        yearlyReturnPct = 432.0,
                        durationDays = 90
                    ),
                    InvestmentPackage(
                        name = "Paket Korporat Growth (Perusahaan)",
                        description = "Khusus akun Perusahaan dengan alokasi dana institusi.",
                        accountType = "COMPANY",
                        minCapital = 50_000_000.0,
                        maxCapital = 1_000_000_000.0,
                        dailyReturnPct = 1.5,
                        monthlyReturnPct = 45.0,
                        yearlyReturnPct = 540.0,
                        durationDays = 180
                    ),
                    InvestmentPackage(
                        name = "Paket Korporat Platinum (Perusahaan)",
                        description = "Modal institusi skala besar dengan dividen tahunan optimal.",
                        accountType = "COMPANY",
                        minCapital = 250_000_000.0,
                        maxCapital = 10_000_000_000.0,
                        dailyReturnPct = 2.0,
                        monthlyReturnPct = 60.0,
                        yearlyReturnPct = 720.0,
                        durationDays = 365
                    )
                )

                defaultPackages.forEach { dao.insertPackage(it) }

                // Initial Seed Users
                dao.insertUserAccount(
                    UserAccount(
                        fullName = "Ahmad Pratama",
                        email = "ahmad@investor.id",
                        phone = "081299887766",
                        password = "user123",
                        accountType = "INDIVIDUAL",
                        balance = 25_000_000.0,
                        bankName = "Bank BCA",
                        bankAccountNumber = "8830192837",
                        bankAccountName = "Ahmad Pratama",
                        referralCode = "AHMADPRATAMA",
                        referredByCode = "",
                        referralBonusesEarned = 450000.0,
                        referredUsersCount = 3
                    )
                )

                // Seed referred users
                dao.insertUserAccount(
                    UserAccount(
                        fullName = "Dewi Lestari",
                        email = "dewi@investor.id",
                        phone = "081234567801",
                        password = "user123",
                        accountType = "INDIVIDUAL",
                        balance = 10_000_000.0,
                        bankName = "Bank Mandiri",
                        bankAccountNumber = "1092837412",
                        bankAccountName = "Dewi Lestari",
                        referralCode = "DEWI123",
                        referredByCode = "AHMADPRATAMA",
                        kycStatus = "VERIFIED",
                        referralBonusesEarned = 0.0,
                        referredUsersCount = 0
                    )
                )

                dao.insertUserAccount(
                    UserAccount(
                        fullName = "Rian Hidayat",
                        email = "rian@investor.id",
                        phone = "081234567802",
                        password = "user123",
                        accountType = "INDIVIDUAL",
                        balance = 5_000_000.0,
                        bankName = "Bank BNI",
                        bankAccountNumber = "2039485761",
                        bankAccountName = "Rian Hidayat",
                        referralCode = "RIAN456",
                        referredByCode = "AHMADPRATAMA",
                        kycStatus = "VERIFIED",
                        referralBonusesEarned = 0.0,
                        referredUsersCount = 0
                    )
                )

                dao.insertUserAccount(
                    UserAccount(
                        fullName = "Siti Aminah",
                        email = "siti@investor.id",
                        phone = "081234567803",
                        password = "user123",
                        accountType = "INDIVIDUAL",
                        balance = 2_500_000.0,
                        bankName = "Bank BRI",
                        bankAccountNumber = "3049586712",
                        bankAccountName = "Siti Aminah",
                        referralCode = "SITI789",
                        referredByCode = "AHMADPRATAMA",
                        kycStatus = "PENDING",
                        referralBonusesEarned = 0.0,
                        referredUsersCount = 0
                    )
                )

                dao.insertUserAccount(
                    UserAccount(
                        fullName = "Bambang Sujatmiko (PT Investasi Jaya Mandiri)",
                        email = "korporat@ptmandiri.co.id",
                        phone = "081122334455",
                        password = "corp123",
                        accountType = "COMPANY",
                        companyName = "PT Investasi Jaya Mandiri",
                        companyTaxId = "NIB-9120003829102",
                        balance = 150_000_000.0,
                        bankName = "Bank Mandiri",
                        bankAccountNumber = "1370009827361",
                        bankAccountName = "PT Investasi Jaya Mandiri",
                        referralCode = "BAMBANGKORP",
                        referredByCode = "",
                        referralBonusesEarned = 0.0,
                        referredUsersCount = 0
                    )
                )

                dao.insertUserAccount(
                    UserAccount(
                        fullName = "Super Admin Utama",
                        email = "admin@investpro.id",
                        phone = "08110001122",
                        password = "admin123",
                        accountType = "SUPER_ADMIN",
                        referralCode = "ADMINREF",
                        referredByCode = "",
                        referralBonusesEarned = 0.0,
                        referredUsersCount = 0
                    )
                )

                // Initial Seed Staff Accounts
                val seedStaffs = listOf(
                    StaffAccount(
                        staffCode = "STF-001",
                        fullName = "Budi Santoso",
                        email = "budi.staff@investpro.id",
                        phone = "081233445566",
                        passwordPin = "123456",
                        rolePosition = "Customer Service Admin",
                        isActive = true
                    ),
                    StaffAccount(
                        staffCode = "STF-002",
                        fullName = "Siti Rahma",
                        email = "siti.finance@investpro.id",
                        phone = "081277889900",
                        passwordPin = "654321",
                        rolePosition = "Finance & Verifikator Depo/WD",
                        isActive = true
                    ),
                    StaffAccount(
                        staffCode = "STF-003",
                        fullName = "Deni Kurniawan",
                        email = "deni.ops@investpro.id",
                        phone = "081311223344",
                        passwordPin = "112233",
                        rolePosition = "Manager Operasional Staff",
                        isActive = true
                    )
                )
                seedStaffs.forEach { dao.insertStaffAccount(it) }

                // Initial Transaction Sample
                dao.insertTransaction(
                    TransactionRecord(
                        type = "DEPOSIT",
                        amount = 25_000_000.0,
                        paymentMethod = "Midtrans Payment Gateway (QRIS)",
                        referenceId = "INV-PG-20260807-001",
                        status = "SUCCESS",
                        timestamp = System.currentTimeMillis() - 3600000 * 24,
                        note = "Deposit awal berhasil dikonfirmasi otomatis oleh Payment Gateway API."
                    )
                )

                dao.insertTransaction(
                    TransactionRecord(
                        type = "REFERRAL_BONUS",
                        amount = 135000.0,
                        paymentMethod = "Sistem Referral",
                        referenceId = "REF-SEED-001",
                        status = "SUCCESS",
                        timestamp = System.currentTimeMillis() - 3600000 * 18,
                        note = "Bonus referral dari pendaftaran Dewi Lestari (dewi@investor.id). Bersih setelah pajak 10%: Rp 135.000 (Potongan Pajak: Rp 15.000)"
                    )
                )

                dao.insertTransaction(
                    TransactionRecord(
                        type = "REFERRAL_BONUS",
                        amount = 135000.0,
                        paymentMethod = "Sistem Referral",
                        referenceId = "REF-SEED-002",
                        status = "SUCCESS",
                        timestamp = System.currentTimeMillis() - 3600000 * 12,
                        note = "Bonus referral dari pendaftaran Rian Hidayat (rian@investor.id). Bersih setelah pajak 10%: Rp 135.000 (Potongan Pajak: Rp 15.000)"
                    )
                )

                dao.insertTransaction(
                    TransactionRecord(
                        type = "REFERRAL_BONUS",
                        amount = 135000.0,
                        paymentMethod = "Sistem Referral",
                        referenceId = "REF-SEED-003",
                        status = "SUCCESS",
                        timestamp = System.currentTimeMillis() - 3600000 * 6,
                        note = "Bonus referral dari pendaftaran Siti Aminah (siti@investor.id). Bersih setelah pajak 10%: Rp 135.000 (Potongan Pajak: Rp 15.000)"
                    )
                )

                dao.insertTransaction(
                    TransactionRecord(
                        type = "REFERRAL_COMMISSION",
                        amount = 45000.0,
                        paymentMethod = "Sistem Referral",
                        referenceId = "COMM-SEED-001",
                        status = "SUCCESS",
                        timestamp = System.currentTimeMillis() - 3600000 * 2,
                        note = "Komisi bagi hasil referral dari keuntungan paket Deposito oleh investor Dewi Lestari (dewi@investor.id)."
                    )
                )
            }
        }
    }
}
