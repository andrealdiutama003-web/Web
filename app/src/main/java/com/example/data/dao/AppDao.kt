package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import com.example.data.entity.StaffAccount
import com.example.data.entity.UserAccount
import com.example.data.entity.UserInvestment
import com.example.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // User Accounts
    @Query("SELECT * FROM user_accounts ORDER BY id DESC")
    fun getAllUserAccounts(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:emailOrPhone) OR phone = :emailOrPhone LIMIT 1")
    suspend fun getUserByEmailOrPhone(emailOrPhone: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE LOWER(fullName) LIKE '%' || LOWER(:keyword) || '%' OR phone = :keyword OR bankAccountNumber = :keyword OR companyTaxId = :keyword OR LOWER(companyName) LIKE '%' || LOWER(:keyword) || '%' LIMIT 10")
    suspend fun findUserAccountsByKeyword(keyword: String): List<UserAccount>

    @Query("SELECT * FROM staff_accounts WHERE LOWER(staffCode) LIKE '%' || LOWER(:keyword) || '%' OR LOWER(email) = LOWER(:keyword) OR phone = :keyword OR LOWER(fullName) LIKE '%' || LOWER(:keyword) || '%' LIMIT 10")
    suspend fun findStaffAccountsByKeyword(keyword: String): List<StaffAccount>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE UPPER(referralCode) = UPPER(:referralCode) LIMIT 1")
    suspend fun getUserByReferralCode(referralCode: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccount): Long

    @Update
    suspend fun updateUserAccount(user: UserAccount)

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteUserAccount(id: Int)

    // Staff Accounts
    @Query("SELECT * FROM staff_accounts ORDER BY id DESC")
    fun getAllStaffAccounts(): Flow<List<StaffAccount>>

    @Query("SELECT * FROM staff_accounts WHERE LOWER(staffCode) = LOWER(:codeOrEmail) OR LOWER(email) = LOWER(:codeOrEmail) LIMIT 1")
    suspend fun getStaffByCodeOrEmail(codeOrEmail: String): StaffAccount?

    @Query("SELECT * FROM staff_accounts WHERE id = :id LIMIT 1")
    suspend fun getStaffById(id: Int): StaffAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffAccount(staff: StaffAccount): Long

    @Update
    suspend fun updateStaffAccount(staff: StaffAccount)

    @Query("DELETE FROM staff_accounts WHERE id = :id")
    suspend fun deleteStaffAccount(id: Int)

    // Investment Packages
    @Query("SELECT * FROM investment_packages ORDER BY id ASC")
    fun getAllPackages(): Flow<List<InvestmentPackage>>

    @Query("SELECT * FROM investment_packages WHERE accountType = :accountType OR accountType = 'ALL'")
    fun getPackagesByAccountType(accountType: String): Flow<List<InvestmentPackage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: InvestmentPackage): Long

    @Update
    suspend fun updatePackage(pkg: InvestmentPackage)

    @Query("DELETE FROM investment_packages WHERE id = :id")
    suspend fun deletePackage(id: Int)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // User Investments
    @Query("SELECT * FROM user_investments ORDER BY id DESC")
    fun getUserInvestments(): Flow<List<UserInvestment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: UserInvestment): Long

    @Update
    suspend fun updateInvestment(investment: UserInvestment)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionRecord)

    // Payment Gateway Config
    @Query("SELECT * FROM payment_gateway_config WHERE id = 1")
    fun getPaymentGatewayConfig(): Flow<PaymentGatewayConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGatewayConfig(config: PaymentGatewayConfig)

    // Admin Fee Config
    @Query("SELECT * FROM admin_fee_config WHERE id = 1")
    fun getAdminFeeConfig(): Flow<AdminFeeConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFeeConfig(config: AdminFeeConfig)

    // Lucky Wheel Config
    @Query("SELECT * FROM lucky_wheel_config WHERE id = 1")
    fun getLuckyWheelConfig(): Flow<LuckyWheelConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLuckyWheelConfig(config: LuckyWheelConfig)

    // Brand Config Rebranding
    @Query("SELECT * FROM brand_config WHERE id = 1")
    fun getBrandConfig(): Flow<BrandConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBrandConfig(config: BrandConfig)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // Crypto Wallet Config
    @Query("SELECT * FROM crypto_wallet_config WHERE id = 1")
    fun getCryptoWalletConfig(): Flow<CryptoWalletConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCryptoWalletConfig(config: CryptoWalletConfig)

    // Banner Slides
    @Query("SELECT * FROM banner_slides ORDER BY displayOrder ASC, id DESC")
    fun getAllBanners(): Flow<List<BannerSlideItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerSlideItem): Long

    @Update
    suspend fun updateBanner(banner: BannerSlideItem)

    @Query("DELETE FROM banner_slides WHERE id = :id")
    suspend fun deleteBanner(id: Int)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogRecord): Long
}
