package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.ui.components.CompanyLogoView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AuthScreen(viewModel: MainViewModel) {
    val brandConfig by viewModel.brandConfig.collectAsState()
    val showEmailVerificationDialog by viewModel.showEmailVerificationDialog.collectAsState()
    val pendingRegistrationUser by viewModel.pendingRegistrationUser.collectAsState()
    val verificationOtpCode by viewModel.verificationOtpCode.collectAsState()
    val showAccountRecoveryDialog by viewModel.showAccountRecoveryDialog.collectAsState()
    val recoveryInitialTab by viewModel.recoveryInitialTab.collectAsState()

    var selectedTab by remember { mutableStateOf("INDIVIDUAL") } // "INDIVIDUAL", "COMPANY", "SUPER_ADMIN", "STAFF"
    var isRegisterMode by remember { mutableStateOf(false) }

    // Forms input states
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var fullNameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var referredByInput by remember { mutableStateOf("") }
    var companyNameInput by remember { mutableStateOf("") }
    var companyTaxIdInput by remember { mutableStateOf("") }
    var masterKeyInput by remember { mutableStateOf("") }
    var staffCodeInput by remember { mutableStateOf("") }
    var staffPinInput by remember { mutableStateOf("") }
    var showBiometricAuthDialog by remember { mutableStateOf(false) }

    // Password & credential visibility toggles
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isMasterKeyVisible by remember { mutableStateOf(false) }
    var isStaffPinVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_header_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompanyLogoView(brandConfig = brandConfig, size = 56.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Portal Autentikasi & Akses Peran",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pilih kategori pengguna/peran untuk Masuk atau Mendaftar ke ${brandConfig?.appName ?: "InvestPro"}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Role Category Selector Tabs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "PILIH KATEGORI AKUN / PERAN:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoleTabChip(
                            label = "Individual",
                            icon = Icons.Default.Person,
                            isSelected = selectedTab == "INDIVIDUAL",
                            activeColor = PrimaryEmerald,
                            onClick = {
                                selectedTab = "INDIVIDUAL"
                                isRegisterMode = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_individual")
                        )
                        RoleTabChip(
                            label = "Perusahaan",
                            icon = Icons.Default.Business,
                            isSelected = selectedTab == "COMPANY",
                            activeColor = PrimaryEmerald,
                            onClick = {
                                selectedTab = "COMPANY"
                                isRegisterMode = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_company")
                        )
                        RoleTabChip(
                            label = "Super Admin",
                            icon = Icons.Default.AdminPanelSettings,
                            isSelected = selectedTab == "SUPER_ADMIN",
                            activeColor = AccentGold,
                            onClick = {
                                selectedTab = "SUPER_ADMIN"
                                isRegisterMode = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_super_admin")
                        )
                        RoleTabChip(
                            label = "Staff",
                            icon = Icons.Default.SupportAgent,
                            isSelected = selectedTab == "STAFF",
                            activeColor = AccentGold,
                            onClick = {
                                selectedTab = "STAFF"
                                isRegisterMode = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_staff")
                        )
                    }
                }
            }
        }

        // Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_form_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Mode Title & Toggle (Masuk vs Daftar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (selectedTab) {
                                "INDIVIDUAL" -> if (isRegisterMode) "Daftar Pengguna Individual" else "Login Pengguna Individual"
                                "COMPANY" -> if (isRegisterMode) "Daftar Pengguna Perusahaan" else "Login Pengguna Perusahaan"
                                "SUPER_ADMIN" -> if (isRegisterMode) "Daftar Super Admin Baru" else "Login Super Admin"
                                else -> "Login Menu Staff & Pegawai"
                            },
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (selectedTab != "STAFF") {
                            TextButton(
                                onClick = { isRegisterMode = !isRegisterMode },
                                modifier = Modifier.testTag("toggle_register_mode_btn")
                            ) {
                                Text(
                                    text = if (isRegisterMode) "Ke Menu Login" else "Daftar Akun Baru",
                                    color = AccentGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // RENDER SPECIFIC FORM ACCORDING TO TAB & MODE
                    when (selectedTab) {
                        "INDIVIDUAL" -> {
                            if (isRegisterMode) {
                                OutlinedTextField(
                                    value = fullNameInput,
                                    onValueChange = { fullNameInput = it },
                                    label = { Text("Nama Lengkap") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_individual_name"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text("Nomor Telepon / WhatsApp") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth().testTag("input_individual_phone"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = referredByInput,
                                    onValueChange = { referredByInput = it },
                                    label = { Text("Kode Referral Teman (Opsional)") },
                                    leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentGold) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_individual_referral"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Individual") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth().testTag("input_individual_email"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Lihat Password",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth().testTag("input_individual_password"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (isRegisterMode) {
                                        viewModel.registerUser(
                                            fullName = fullNameInput,
                                            email = emailInput,
                                            phone = phoneInput,
                                            pass = passwordInput,
                                            accountType = "INDIVIDUAL",
                                            referredByCode = referredByInput
                                        )
                                    } else {
                                        viewModel.loginUser(emailInput, passwordInput)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_individual"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                            ) {
                                Icon(
                                    imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.Login,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRegisterMode) "DAFTAR AKUN INDIVIDUAL" else "MASUK AKUN INDIVIDUAL",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!isRegisterMode) {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { showBiometricAuthDialog = true },
                                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_biometric_login_individual"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("LOGIN BIOMETRIK (SIDIK JARI / FACE ID)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = { viewModel.openAccountRecoveryDialog("PASSWORD") },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_forgot_password_individual")
                                ) {
                                    Icon(Icons.Default.LockReset, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lupa Password / PIN / Email?", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        "COMPANY" -> {
                            if (isRegisterMode) {
                                OutlinedTextField(
                                    value = companyNameInput,
                                    onValueChange = { companyNameInput = it },
                                    label = { Text("Nama Perusahaan (PT / CV / Firma)") },
                                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_company_name"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = companyTaxIdInput,
                                    onValueChange = { companyTaxIdInput = it },
                                    label = { Text("Nomor NIB / NPWP Perusahaan") },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_company_tax"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = fullNameInput,
                                    onValueChange = { fullNameInput = it },
                                    label = { Text("Nama Penanggung Jawab / Direktur") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_company_pic_name"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = referredByInput,
                                    onValueChange = { referredByInput = it },
                                    label = { Text("Kode Referral Teman (Opsional)") },
                                    leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentGold) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_company_referral"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Perusahaan") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth().testTag("input_company_email"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password Perusahaan") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Lihat Password Perusahaan",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth().testTag("input_company_password"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = PrimaryEmerald, unfocusedLabelColor = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (isRegisterMode) {
                                        viewModel.registerUser(
                                            fullName = fullNameInput,
                                            email = emailInput,
                                            phone = phoneInput,
                                            pass = passwordInput,
                                            accountType = "COMPANY",
                                            companyName = companyNameInput,
                                            companyTaxId = companyTaxIdInput,
                                            referredByCode = referredByInput
                                        )
                                    } else {
                                        viewModel.loginUser(emailInput, passwordInput)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_company"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald, contentColor = Color.Black)
                            ) {
                                Icon(
                                    imageVector = if (isRegisterMode) Icons.Default.Business else Icons.Default.Login,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRegisterMode) "DAFTAR AKUN PERUSAHAAN" else "MASUK AKUN PERUSAHAAN",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!isRegisterMode) {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { showBiometricAuthDialog = true },
                                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_biometric_login_company"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("LOGIN BIOMETRIK PERUSAHAAN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = { viewModel.openAccountRecoveryDialog("PASSWORD") },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_forgot_password_company")
                                ) {
                                    Icon(Icons.Default.LockReset, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lupa Password / PIN / Email Perusahaan?", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        "SUPER_ADMIN" -> {
                            if (isRegisterMode) {
                                OutlinedTextField(
                                    value = fullNameInput,
                                    onValueChange = { fullNameInput = it },
                                    label = { Text("Nama Lengkap Admin") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_admin_name"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = masterKeyInput,
                                    onValueChange = { masterKeyInput = it },
                                    label = { Text("Master Security Key (Contoh: SUPER2026)") },
                                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TextSecondary) },
                                    trailingIcon = {
                                        IconButton(onClick = { isMasterKeyVisible = !isMasterKeyVisible }) {
                                            Icon(
                                                imageVector = if (isMasterKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Lihat Master Key",
                                                tint = TextSecondary
                                            )
                                        }
                                    },
                                    visualTransformation = if (isMasterKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth().testTag("input_admin_master_key"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Super Admin") },
                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("input_admin_email"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Passcode Super Admin (Default: admin123)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Lihat Passcode",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("input_admin_passcode"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (isRegisterMode) {
                                        viewModel.registerSuperAdmin(
                                            fullName = fullNameInput,
                                            email = emailInput,
                                            pass = passwordInput,
                                            masterSecurityKey = masterKeyInput
                                        )
                                    } else {
                                        viewModel.loginSuperAdmin(emailInput, passwordInput)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_super_admin"),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRegisterMode) "DAFTAR SUPER ADMIN BARU" else "MASUK SEBAGAI SUPER ADMIN",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!isRegisterMode) {
                                TextButton(
                                    onClick = { viewModel.openAccountRecoveryDialog("PASSCODE") },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_forgot_passcode_super_admin")
                                ) {
                                    Icon(Icons.Default.LockReset, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lupa Passcode Admin / Master Key?", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        "STAFF" -> {
                            Text(
                                text = "Akses Khusus Pegawai & Tim Customer Support / Verifikator yang didaftarkan oleh Super Admin.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = staffCodeInput,
                                onValueChange = { staffCodeInput = it },
                                label = { Text("Kode Staff / Email Staff (Contoh: STF-001)") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("input_staff_code"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = staffPinInput,
                                onValueChange = { staffPinInput = it },
                                label = { Text("PIN / Password Staff (Default: 123456)") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TextSecondary) },
                                trailingIcon = {
                                    IconButton(onClick = { isStaffPinVisible = !isStaffPinVisible }) {
                                        Icon(
                                            imageVector = if (isStaffPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Lihat PIN Staff",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (isStaffPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.fillMaxWidth().testTag("input_staff_pin"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold, unfocusedBorderColor = DarkCardBorder, focusedLabelColor = AccentGold, unfocusedLabelColor = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.loginStaff(staffCodeInput, staffPinInput) },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_staff"),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("LOGIN STAFF & PEGAWAI", fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { viewModel.openAccountRecoveryDialog("PASSCODE") },
                                modifier = Modifier.fillMaxWidth().testTag("btn_forgot_pin_staff")
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lupa Kode Staff / PIN Pegawai?", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Quick Demo Fill Helper Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ AKSES SIMULASI DEMO CEPAT (SATU KLIK):",
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedTab = "INDIVIDUAL"
                                emailInput = "ahmad@investor.id"
                                passwordInput = "user123"
                                viewModel.loginUser("ahmad@investor.id", "user123")
                            },
                            modifier = Modifier.weight(1f).testTag("demo_btn_individual"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald)
                        ) {
                            Text("Ahmad (User)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                selectedTab = "COMPANY"
                                emailInput = "korporat@ptmandiri.co.id"
                                passwordInput = "corp123"
                                viewModel.loginUser("korporat@ptmandiri.co.id", "corp123")
                            },
                            modifier = Modifier.weight(1f).testTag("demo_btn_company"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryEmerald)
                        ) {
                            Text("PT Jaya (Corp)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedTab = "SUPER_ADMIN"
                                emailInput = "admin@investpro.id"
                                passwordInput = "admin123"
                                viewModel.loginSuperAdmin("admin@investpro.id", "admin123")
                            },
                            modifier = Modifier.weight(1f).testTag("demo_btn_super_admin"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold)
                        ) {
                            Text("Super Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                selectedTab = "STAFF"
                                staffCodeInput = "STF-001"
                                staffPinInput = "123456"
                                viewModel.loginStaff("STF-001", "123456")
                            },
                            modifier = Modifier.weight(1f).testTag("demo_btn_staff"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold)
                        ) {
                            Text("Staff (STF-001)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showBiometricAuthDialog) {
        com.example.ui.components.HighSecurityBiometricVerificationDialog(
            transactionTitle = "Akses Masuk Cepat Biometrik",
            transactionDetails = "Pindai Sidik Jari atau Wajah untuk Autentikasi Pengguna",
            onVerificationSuccess = {
                showBiometricAuthDialog = false
                if (selectedTab == "COMPANY") {
                    viewModel.loginUser("korporat@ptmandiri.co.id", "corp123")
                } else {
                    viewModel.loginUser("ahmad@investor.id", "user123")
                }
            },
            onDismiss = { showBiometricAuthDialog = false }
        )
    }

    if (showEmailVerificationDialog && pendingRegistrationUser != null) {
        com.example.ui.components.FirebaseEmailVerificationDialog(
            userEmail = pendingRegistrationUser!!.email,
            expectedOtp = verificationOtpCode,
            onVerifySuccess = {
                viewModel.completeEmailVerification()
            },
            onResendEmail = {
                viewModel.resendFirebaseVerificationEmail()
            },
            onDismiss = {
                viewModel.dismissEmailVerificationDialog()
            }
        )
    }

    if (showAccountRecoveryDialog) {
        com.example.ui.components.AccountRecoveryDialog(
            initialTab = recoveryInitialTab,
            viewModel = viewModel,
            onDismiss = {
                viewModel.dismissAccountRecoveryDialog()
            }
        )
    }
}

@Composable
fun RoleTabChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor else DarkCardBorder.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.Black else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = if (isSelected) Color.Black else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
