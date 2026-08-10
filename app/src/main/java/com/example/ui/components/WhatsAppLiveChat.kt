package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.BrandConfig

val WhatsAppGreen = Color(0xFF25D366)
val WhatsAppDarkGreen = Color(0xFF128C7E)
val WhatsAppBg = Color(0xFF0B141A)
val WhatsAppBubble = Color(0xFF1F2C34)

@Composable
fun WhatsAppLiveChatFab(
    brandConfig: BrandConfig?,
    accountType: String = "USER",
    modifier: Modifier = Modifier
) {
    if (brandConfig?.isLiveChatEnabled == false) return

    val context = LocalContext.current
    var isDialogOpen by remember { mutableStateOf(false) }

    val cleanWaNum = (brandConfig?.whatsappNumber ?: "6281234567890")
        .replace("+", "")
        .replace(" ", "")
        .replace("-", "")

    val companyName = brandConfig?.companyName ?: "PT Investasi Jaya Mandiri"
    val defaultGreeting = brandConfig?.whatsappGreeting ?: "Halo CS Admin, saya ingin berkonsultasi mengenai investasi."

    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = { isDialogOpen = true },
            containerColor = WhatsAppGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = "WhatsApp Live Chat CS",
                    modifier = Modifier.size(28.dp)
                )
                // Badge Notification
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }

    if (isDialogOpen) {
        WhatsAppChatDialog(
            companyName = companyName,
            waNumber = cleanWaNum,
            defaultGreeting = defaultGreeting,
            accountType = accountType,
            onDismiss = { isDialogOpen = false },
            onOpenWhatsApp = { text ->
                try {
                    val encoded = Uri.encode(text)
                    val url = "https://api.whatsapp.com/send?phone=$cleanWaNum&text=$encoded"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Tidak dapat membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                isDialogOpen = false
            }
        )
    }
}

@Composable
fun WhatsAppChatDialog(
    companyName: String,
    waNumber: String,
    defaultGreeting: String,
    accountType: String,
    onDismiss: () -> Unit,
    onOpenWhatsApp: (String) -> Unit
) {
    var messageText by remember { mutableStateOf(defaultGreeting) }

    val quickOptions = if (accountType == "COMPANY") {
        listOf(
            "Halo Admin, saya ingin bertanya seputar Paket Investasi Korporat Perusahaan.",
            "Saya ingin pengajuan Deposit Saldo Perusahaan via Transfer Bank / Gateway.",
            "Mohon info terkait dividen & return tahunan akun perusahaan.",
            "Saya butuh bantuan penarikan saldo perusahaan."
        )
    } else {
        listOf(
            "Halo Admin, saya mau tanya paket investasi akun personal.",
            "Saya butuh panduan deposit saldo & metode pembayaran.",
            "Bagaimana cara klaim profit harian ke dompet?",
            "Saya butuh bantuan penarikan saldo ke bank/e-wallet."
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WhatsAppBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Live Chat Official CS",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(WhatsAppGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$companyName (Online 24/7)",
                                    fontSize = 10.sp,
                                    color = WhatsAppGreen
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chat bubble preview
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = WhatsAppBubble,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Customer Service $companyName",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhatsAppGreen
                        )
                        Text(
                            text = "Halo! Silakan pilih topik pertanyaan atau tulis pesan langsung di bawah untuk memulai chat WhatsApp dengan Customer Service kami.",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick options
                Text(
                    text = "Pilih Topik Bantuan Cepat:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickOptions.forEach { option ->
                        Surface(
                            onClick = { messageText = option },
                            shape = RoundedCornerShape(10.dp),
                            color = if (messageText == option) WhatsAppDarkGreen else Color(0xFF182229),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (messageText == option) WhatsAppGreen else Color(0xFF222D34)
                            )
                        ) {
                            Text(
                                text = option,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input message
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ketik pesan untuk CS...", fontSize = 12.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhatsAppGreen,
                        unfocusedBorderColor = Color(0xFF222D34),
                        focusedContainerColor = Color(0xFF182229),
                        unfocusedContainerColor = Color(0xFF182229),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Launch WhatsApp Button
                Button(
                    onClick = { onOpenWhatsApp(messageText) },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mulai Chat CS WhatsApp (+${waNumber})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
