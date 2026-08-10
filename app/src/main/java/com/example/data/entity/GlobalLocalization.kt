package com.example.data.entity

object GlobalLocalization {

    fun getString(key: String, langCode: String): String {
        val normalizedLang = langCode.uppercase()
        return when (key) {
            "app_title" -> when (normalizedLang) {
                "EN" -> "InvestPro Global"
                "ES" -> "InvestPro Global"
                "ZH" -> "InvestPro 全球投资"
                "AR" -> "إنفيستبرو العالمية"
                "JA" -> "InvestPro グローバル"
                "DE" -> "InvestPro Global"
                "FR" -> "InvestPro Global"
                "KO" -> "인베스트프로 글로벌"
                "PT" -> "InvestPro Global"
                "HI" -> "इन्वेस्टप्रो ग्लोबल"
                "RU" -> "InvestPro Глобал"
                "VI" -> "InvestPro Toàn Cầu"
                "TR" -> "InvestPro Küresel"
                else -> "InvestPro Global"
            }
            "deposit" -> when (normalizedLang) {
                "EN" -> "Deposit Funds"
                "ES" -> "Depositar Fondos"
                "ZH" -> "存入资金"
                "AR" -> "إيداع الأموال"
                "JA" -> "入金する"
                "DE" -> "Guthaben Einzahlen"
                "FR" -> "Déposer des fonds"
                "KO" -> "자금 입금"
                "PT" -> "Depositar Fundos"
                "HI" -> "फंड जमा करें"
                "RU" -> "Пополнить баланс"
                "VI" -> "Nạp Tiền"
                "TR" -> "Para Yatır"
                else -> "Deposit Saldo"
            }
            "withdraw" -> when (normalizedLang) {
                "EN" -> "Withdraw Balance"
                "ES" -> "Retirar Saldo"
                "ZH" -> "提取余额"
                "AR" -> "سحب الرصيد"
                "JA" -> "出金する"
                "DE" -> "Guthaben Auszahlen"
                "FR" -> "Retirer le solde"
                "KO" -> "잔액 출금"
                "PT" -> "Sacar Saldo"
                "HI" -> "निकासी करें"
                "RU" -> "Вывести средства"
                "VI" -> "Rút Tiền"
                "TR" -> "Para Çek"
                else -> "Penarikan Saldo"
            }
            "crypto_manual" -> when (normalizedLang) {
                "EN" -> "Crypto Wallet (Manual Worldwide)"
                "ES" -> "Billetera Crypto (Manual Mundial)"
                "ZH" -> "加密货币钱包 (全球手动)"
                "AR" -> "محفظة تشفير (يدوي عالمي)"
                "JA" -> "暗号資産ウォレット (世界手動)"
                "DE" -> "Krypto-Wallet (Manuell Weltweit)"
                "FR" -> "Portefeuille Crypto (Manuel mondial)"
                "KO" -> "암호화폐 지갑 (전 세계 수동)"
                "PT" -> "Carteira Crypto (Manual Mundial)"
                "HI" -> "क्रिप्टो वॉलेट (ग्लोबल मैनुअल)"
                "RU" -> "Криптокошелек (Глобальный ручной)"
                "VI" -> "Ví Tiền Điện Tử (Thủ Công Toàn Cầu)"
                "TR" -> "Kripto Cüzdanı (Küresel Manuel)"
                else -> "Crypto Wallet (Manual Worldwide)"
            }
            "individual_account" -> when (normalizedLang) {
                "EN" -> "Individual Account"
                "ES" -> "Cuenta Individual"
                "ZH" -> "个人账户"
                "AR" -> "حساب فردي"
                "JA" -> "個人アカウント"
                "DE" -> "Einzelkonto"
                "FR" -> "Compte Individuel"
                "KO" -> "개인 계정"
                "PT" -> "Conta Individual"
                "HI" -> "व्यक्तिगत खाता"
                "RU" -> "Личный счет"
                "VI" -> "Tài Khoản Cá Nhân"
                "TR" -> "Bireysel Hesap"
                else -> "Akun Pengguna (Personal)"
            }
            "company_account" -> when (normalizedLang) {
                "EN" -> "Corporate / Company Account"
                "ES" -> "Cuenta Corporativa / Empresa"
                "ZH" -> "企业/公司账户"
                "AR" -> "حساب الشركات"
                "JA" -> "法人・企业アカウント"
                "DE" -> "Unternehmenskonto"
                "FR" -> "Compte Entreprise"
                "KO" -> "법인 / 기업 계정"
                "PT" -> "Conta Empresarial"
                "HI" -> "कॉर्पोरेट खाता"
                "RU" -> "Корпоративный счет"
                "VI" -> "Tài Khoản Doanh Nghiệp"
                "TR" -> "Kurumsal Hesap"
                else -> "Akun Perusahaan (Corporate)"
            }
            "super_admin" -> when (normalizedLang) {
                "EN" -> "Super Admin Command Center"
                "ES" -> "Centro de Comando Super Admin"
                "ZH" -> "超级管理员控制中心"
                "AR" -> "مركز قيادة المسؤول الفائق"
                "JA" -> "スーパー管理者コントロールセンター"
                "DE" -> "Super Admin Kommandozentrale"
                "FR" -> "Centre de commandement Super Admin"
                "KO" -> "슈퍼 관리자 제어 센터"
                "PT" -> "Centro de Comando Super Admin"
                "HI" -> "सुपर एडमिन कंट्रोल सेंटर"
                "RU" -> "Панель Суперадминистратора"
                "VI" -> "Trung Tâm Quản Trị Cấp Cao"
                "TR" -> "Süper Yönetici Kontrol Merkezi"
                else -> "Super Admin Command Center"
            }
            "staff_panel" -> when (normalizedLang) {
                "EN" -> "Staff Operator Portal"
                "ES" -> "Portal de Operadores de Personal"
                "ZH" -> "员工操作员门户"
                "AR" -> "بوابة الموظفين"
                "JA" -> "スタッフポータル"
                "DE" -> "Personal-Operator-Portal"
                "FR" -> "Portail du personnel"
                "KO" -> "직원 운영자 포털"
                "PT" -> "Portal do Operador"
                "HI" -> "स्टाफ ऑपरेटर पोर्टल"
                "RU" -> "Портал Оператора"
                "VI" -> "Cổng Nhân Viên Bận Hành"
                "TR" -> "Personel Portalı"
                else -> "Portal Staff Operator"
            }
            else -> key
        }
    }
}
