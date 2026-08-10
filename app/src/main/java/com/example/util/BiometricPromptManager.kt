package com.example.util

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

sealed class BiometricResult {
    object HardwareUnavailable : BiometricResult()
    object FeatureUnavailable : BiometricResult()
    object NoBiometricsEnrolled : BiometricResult()
    object AuthenticationSuccess : BiometricResult()
    data class AuthenticationError(val error: String) : BiometricResult()
    object AuthenticationFailed : BiometricResult()
}

class BiometricPromptManager(private val activity: FragmentActivity) {

    private val resultChannel = Channel<BiometricResult>()
    val promptResults = resultChannel.receiveAsFlow()

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(activity)
        val authenticators = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
        return manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        title: String = "Otentikasi Biometrik Keamanan",
        subtitle: String = "Verifikasi Sidik Jari atau Pemindaian Wajah (Face ID)",
        description: String = "Diperlukan untuk otorisasi transaksi keamanan tinggi & akses akun",
        negativeButtonText: String = "Gunakan PIN Transaksi"
    ) {
        val manager = BiometricManager.from(activity)
        val authenticators = BIOMETRIC_STRONG or BIOMETRIC_WEAK

        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.NoBiometricsEnrolled)
                return
            }
            else -> Unit
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                resultChannel.trySend(BiometricResult.AuthenticationSuccess)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                resultChannel.trySend(BiometricResult.AuthenticationFailed)
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)

        prompt.authenticate(promptInfoBuilder.build())
    }
}
