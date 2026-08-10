package com.example.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

data class CurrencyOption(
    val code: String,
    val name: String,
    val symbol: String,
    val flagEmoji: String,
    val rateFromIDR: Double // 1 IDR = X Currency
)

data class LanguageOption(
    val code: String,
    val name: String,
    val flagEmoji: String
)

object GlobalLocaleAndCurrency {

    val SUPPORTED_CURRENCIES = listOf(
        CurrencyOption("IDR", "Indonesian Rupiah", "Rp ", "🇮🇩", 1.0),
        CurrencyOption("USD", "US Dollar", "$ ", "🇺🇸", 0.000062),
        CurrencyOption("EUR", "Euro", "€ ", "🇪🇺", 0.000057),
        CurrencyOption("SGD", "Singapore Dollar", "S$ ", "🇸🇬", 0.000084),
        CurrencyOption("JPY", "Japanese Yen", "¥ ", "🇯🇵", 0.0096),
        CurrencyOption("MYR", "Malaysian Ringgit", "RM ", "🇲🇾", 0.00028),
        CurrencyOption("GBP", "British Pound", "£ ", "🇬🇧", 0.000049),
        CurrencyOption("AUD", "Australian Dollar", "A$ ", "🇦🇺", 0.000095),
        CurrencyOption("SAR", "Saudi Riyal", "﷼ ", "🇸🇦", 0.00023),
        CurrencyOption("CNY", "Chinese Yuan", "¥ ", "🇨🇳", 0.00045),
        CurrencyOption("KRW", "Korean Won", "₩ ", "🇰🇷", 0.083),
        CurrencyOption("AED", "UAE Dirham", "AED ", "🇦🇪", 0.00023),
        CurrencyOption("CAD", "Canadian Dollar", "CA$ ", "🇨🇦", 0.000086),
        CurrencyOption("CHF", "Swiss Franc", "CHF ", "🇨🇭", 0.000055),
        CurrencyOption("INR", "Indian Rupee", "₹ ", "🇮🇳", 0.0052),
        CurrencyOption("THB", "Thai Baht", "฿ ", "🇹🇭", 0.0022),
        CurrencyOption("RUB", "Russian Ruble", "₽ ", "🇷🇺", 0.0056),
        CurrencyOption("BRL", "Brazilian Real", "R$ ", "🇧🇷", 0.00035)
    )

    val SUPPORTED_LANGUAGES = listOf(
        LanguageOption("ID", "Bahasa Indonesia", "🇮🇩"),
        LanguageOption("EN", "English (US)", "🇺🇸"),
        LanguageOption("ES", "Español (Spanish)", "🇪🇸"),
        LanguageOption("ZH", "中文 (Mandarin)", "🇨🇳"),
        LanguageOption("JA", "日本語 (Japanese)", "🇯🇵"),
        LanguageOption("AR", "العربية (Arabic)", "🇸🇦"),
        LanguageOption("DE", "Deutsch (German)", "🇩🇪"),
        LanguageOption("FR", "Français (French)", "🇫🇷"),
        LanguageOption("KO", "한국어 (Korean)", "🇰🇷"),
        LanguageOption("RU", "Русский (Russian)", "🇷🇺"),
        LanguageOption("PT", "Português (Portuguese)", "🇧🇷"),
        LanguageOption("HI", "हिन्दी (Hindi)", "🇮🇳"),
        LanguageOption("TH", "ไทย (Thai)", "🇹🇭")
    )

    fun formatMoney(amountInIDR: Double, targetCurrencyCode: String): String {
        val curr = SUPPORTED_CURRENCIES.find { it.code == targetCurrencyCode } ?: SUPPORTED_CURRENCIES.first()
        val converted = amountInIDR * curr.rateFromIDR
        val formatter = DecimalFormat("#,##0.00")
        return when (curr.code) {
            "IDR" -> "Rp " + DecimalFormat("#,##0").format(amountInIDR)
            "JPY", "KRW" -> "${curr.symbol}${DecimalFormat("#,##0").format(converted)}"
            else -> "${curr.symbol}${formatter.format(converted)}"
        }
    }

    private val DICTIONARY = mapOf(
        "ID" to mapOf(
            "dashboard" to "Dashboard Portofolio",
            "deposit" to "Deposit Modal",
            "withdraw" to "Tarik Saldo",
            "packages" to "Paket Investasi",
            "kyc_title" to "Verifikasi Identitas (KYC)",
            "kyc_verified" to "KYC Terverifikasi",
            "kyc_pending" to "KYC Menunggu Verifikasi",
            "kyc_unverified" to "Belum Verifikasi KYC",
            "biometric_title" to "Keamanan Biometrik",
            "fingerprint" to "Sidik Jari (Fingerprint)",
            "face_recognition" to "Verifikasi Wajah (Face ID)",
            "pin_transaksi" to "PIN Transaksi 6 Digit",
            "enter_pin" to "Masukkan PIN Transaksi Keamanan Anda",
            "login_register" to "Login / Daftar Akun",
            "admin_panel" to "Super Admin",
            "staff_panel" to "Staff CS & Verifikator",
            "balance" to "Total Saldo Investasi",
            "daily_profit" to "Estimasi Profit Harian",
            "select_currency" to "Pilih Mata Uang Dunia",
            "select_language" to "Pilih Bahasa Layanan",
            "nav_home" to "Utama",
            "nav_dashboard" to "Dashboard",
            "nav_login" to "Login/Daftar",
            "nav_staff" to "Staff",
            "nav_admin" to "Admin",
            "nav_wheel" to "Roda Keberuntungan",
            "nav_portal" to "Portal Web",
            "active_access" to "Akses Aktif",
            "language_switch_msg" to "Bahasa berhasil diubah ke Bahasa Indonesia"
        ),
        "EN" to mapOf(
            "dashboard" to "Portfolio Dashboard",
            "deposit" to "Deposit Capital",
            "withdraw" to "Withdraw Funds",
            "packages" to "Investment Packages",
            "kyc_title" to "Identity Verification (KYC)",
            "kyc_verified" to "KYC Verified",
            "kyc_pending" to "KYC Pending Approval",
            "kyc_unverified" to "Unverified KYC",
            "biometric_title" to "Biometric Security",
            "fingerprint" to "Fingerprint Scan",
            "face_recognition" to "Face Recognition (Face ID)",
            "pin_transaksi" to "6-Digit Transaction PIN",
            "enter_pin" to "Enter Your Security Transaction PIN",
            "login_register" to "Login / Register",
            "admin_panel" to "Super Admin",
            "staff_panel" to "Staff CS & Verifier",
            "balance" to "Total Investment Balance",
            "daily_profit" to "Estimated Daily Profit",
            "select_currency" to "Select World Currency",
            "select_language" to "Select Global Language",
            "nav_home" to "Home",
            "nav_dashboard" to "Dashboard",
            "nav_login" to "Login/Register",
            "nav_staff" to "Staff",
            "nav_admin" to "Admin",
            "nav_wheel" to "Lucky Wheel",
            "nav_portal" to "Web Portal",
            "active_access" to "Active Access",
            "language_switch_msg" to "Language successfully changed to English"
        ),
        "ES" to mapOf(
            "dashboard" to "Panel de Portafolio",
            "deposit" to "Depositar Capital",
            "withdraw" to "Retirar Fondos",
            "packages" to "Paquetes de Inversión",
            "kyc_title" to "Verificación de Identidad (KYC)",
            "kyc_verified" to "KYC Verificado",
            "kyc_pending" to "KYC Pendiente de Aprobación",
            "kyc_unverified" to "KYC No Verificado",
            "biometric_title" to "Seguridad Biométrica",
            "fingerprint" to "Huella Dactilar",
            "face_recognition" to "Reconocimiento Facial (Face ID)",
            "pin_transaksi" to "PIN de Transacción de 6 Dígitos",
            "enter_pin" to "Ingrese su PIN de Transacción de Seguridad",
            "login_register" to "Iniciar Sesión / Registro",
            "admin_panel" to "Súper Admin",
            "staff_panel" to "Personal de Atención y Verificación",
            "balance" to "Saldo Total de Inversión",
            "daily_profit" to "Ganancia Diaria Estimada",
            "select_currency" to "Seleccionar Moneda Mundial",
            "select_language" to "Seleccionar Idioma Global"
        ),
        "ZH" to mapOf(
            "dashboard" to "投资组合仪表板",
            "deposit" to "资金充值",
            "withdraw" to "资金提现",
            "packages" to "投资方案组合",
            "kyc_title" to "身份实名认证 (KYC)",
            "kyc_verified" to "KYC 已认证",
            "kyc_pending" to "KYC 审核中",
            "kyc_unverified" to "KYC 未认证",
            "biometric_title" to "生物识别安全",
            "fingerprint" to "指纹识别",
            "face_recognition" to "面部人脸识别 (Face ID)",
            "pin_transaksi" to "6位数交易安全 PIN 码",
            "enter_pin" to "请输入您的交易安全 PIN 码",
            "login_register" to "登录 / 注册账号",
            "admin_panel" to "超级管理员",
            "staff_panel" to "客服与审核员工",
            "balance" to "投资总资产余额",
            "daily_profit" to "每日预估收益",
            "select_currency" to "选择全球法定货币",
            "select_language" to "选择全球服务语言"
        ),
        "JA" to mapOf(
            "dashboard" to "ポートフォリオ ダッシュボード",
            "deposit" to "資金入金",
            "withdraw" to "資金出金",
            "packages" to "投資プラン一覧",
            "kyc_title" to "本人確認認証 (KYC)",
            "kyc_verified" to "KYC 承認済み",
            "kyc_pending" to "KYC 承認待ち",
            "kyc_unverified" to "KYC 未認証",
            "biometric_title" to "生体認証セキュリティ",
            "fingerprint" to "指紋認証 (Fingerprint)",
            "face_recognition" to "顔認証 (Face ID)",
            "pin_transaksi" to "6桁取引 PIN コード",
            "enter_pin" to "セキュリティ取引 PIN を入力してください",
            "login_register" to "ログイン / 新規登録",
            "admin_panel" to "スーパー管理者",
            "staff_panel" to "サポート＆検証スタッフ",
            "balance" to "総投資残高",
            "daily_profit" to "推定日次利益",
            "select_currency" to "世界の通貨を選択",
            "select_language" to "世界言語を選択"
        ),
        "AR" to mapOf(
            "dashboard" to "لوحة المحفظة الاستثمارية",
            "deposit" to "إيداع رأس المال",
            "withdraw" to "سحب الأرباح",
            "packages" to "باقات الاستثمار",
            "kyc_title" to "التحقق من الهوية (KYC)",
            "kyc_verified" to "تم التحقق من الهوية",
            "kyc_pending" to "قيد مراجعة الهوية",
            "kyc_unverified" to "غير متحقق من الهوية",
            "biometric_title" to "الأمان الحيوي",
            "fingerprint" to "بصمة الأصبع",
            "face_recognition" to "التعرف على الوجه (Face ID)",
            "pin_transaksi" to "رمز PIN للمعاملات (6 أرقام)",
            "enter_pin" to "أدخل رمز PIN الأمني للمعاملات",
            "login_register" to "تسجيل الدخول / إنشاء حساب",
            "admin_panel" to "المدير العام (Super Admin)",
            "staff_panel" to "فريق الدعم والتدقيق",
            "balance" to "إجمالي رصيد الاستثمار",
            "daily_profit" to "الأرباح اليومية المتوقعة",
            "select_currency" to "اختر العملة العالمية",
            "select_language" to "اختر اللغة العالمية"
        ),
        "DE" to mapOf(
            "dashboard" to "Portfolio Dashboard",
            "deposit" to "Kapital Einzahlen",
            "withdraw" to "Guthaben Auszahlen",
            "packages" to "Investitionspakete",
            "kyc_title" to "Identitätsprüfung (KYC)",
            "kyc_verified" to "KYC Verifiziert",
            "kyc_pending" to "KYC Prüfung Ausstehend",
            "kyc_unverified" to "KYC Nicht Verifiziert",
            "biometric_title" to "Biometrische Sicherheit",
            "fingerprint" to "Fingerabdruck-Scan",
            "face_recognition" to "Gesichtserkennung (Face ID)",
            "pin_transaksi" to "6-stellige Transaktions-PIN",
            "enter_pin" to "Geben Sie Ihre Sicherheits-Transaktions-PIN ein",
            "login_register" to "Anmelden / Registrieren",
            "admin_panel" to "Super Admin",
            "staff_panel" to "Kundenservice & Verifizierung",
            "balance" to "Gesamt-Investitionsguthaben",
            "daily_profit" to "Geschätzter Tagesgewinn",
            "select_currency" to "Weltwährung Auswählen",
            "select_language" to "Globale Sprache Auswählen"
        ),
        "FR" to mapOf(
            "dashboard" to "Tableau de Bord Portefeuille",
            "deposit" to "Déposer un Capital",
            "withdraw" to "Retirer des Fonds",
            "packages" to "Offres d'Investissement",
            "kyc_title" to "Vérification d'Identité (KYC)",
            "kyc_verified" to "KYC Vérifié",
            "kyc_pending" to "KYC en Attente de Validation",
            "kyc_unverified" to "KYC Non Vérifié",
            "biometric_title" to "Sécurité Biométrique",
            "fingerprint" to "Empreinte Digitale",
            "face_recognition" to "Reconnaissance Faciale (Face ID)",
            "pin_transaksi" to "Code PIN de Transaction à 6 Chiffres",
            "enter_pin" to "Saisissez votre PIN de Sécurité de Transaction",
            "login_register" to "Connexion / Inscription",
            "admin_panel" to "Super Administrateur",
            "staff_panel" to "Personnel Support & Vérification",
            "balance" to "Solde Total d'Investissement",
            "daily_profit" to "Profit Quotidien Estimé",
            "select_currency" to "Sélectionner la Devise",
            "select_language" to "Sélectionner la Langue"
        )
    )

    fun t(key: String, langCode: String = "ID"): String {
        val langMap = DICTIONARY[langCode] ?: DICTIONARY["ID"]!!
        return langMap[key] ?: DICTIONARY["EN"]?.get(key) ?: DICTIONARY["ID"]?.get(key) ?: key
    }
}
