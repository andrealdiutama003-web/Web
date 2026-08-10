package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TransactionRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenRoute
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: MainViewModel,
    transactions: List<TransactionRecord>,
    onBack: () -> Unit = { viewModel.navigateTo(ScreenRoute.USER_DASHBOARD) }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "DEPOSIT", "WITHDRAWAL", "INVESTMENT", "REFERRAL"
    var selectedSortOrder by remember { mutableStateOf("NEWEST") } // "NEWEST", "OLDEST", "AMOUNT_HIGH", "AMOUNT_LOW"
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedTransactionForDetail by remember { mutableStateOf<TransactionRecord?>(null) }

    // Aggregate statistics from all user transactions
    val totalDeposit = transactions.filter { it.type == "DEPOSIT" && it.status == "SUCCESS" }.sumOf { it.amount }
    val totalWithdrawal = transactions.filter { it.type == "WITHDRAWAL" && it.status == "SUCCESS" }.sumOf { it.amount }
    val totalReturns = transactions.filter { it.type == "PROFIT_CLAIM" && it.status == "SUCCESS" }.sumOf { it.amount }
    val totalReferral = transactions.filter { (it.type == "REFERRAL_BONUS" || it.type == "REFERRAL_COMMISSION") && it.status == "SUCCESS" }.sumOf { it.amount }

    // Filter transactions
    val filteredTransactions = remember(transactions, selectedFilter, searchQuery, selectedSortOrder) {
        var list = transactions.filter { tx ->
            val matchesFilter = when (selectedFilter) {
                "DEPOSIT" -> tx.type == "DEPOSIT"
                "WITHDRAWAL" -> tx.type == "WITHDRAWAL"
                "INVESTMENT" -> tx.type == "INVESTMENT" || tx.type == "PROFIT_CLAIM"
                "REFERRAL" -> tx.type == "REFERRAL_BONUS" || tx.type == "REFERRAL_COMMISSION"
                else -> true
            }

            val matchesSearch = tx.referenceId.contains(searchQuery, ignoreCase = true) ||
                    tx.note.contains(searchQuery, ignoreCase = true) ||
                    tx.paymentMethod.contains(searchQuery, ignoreCase = true) ||
                    tx.type.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }

        // Apply sorting
        list = when (selectedSortOrder) {
            "OLDEST" -> list.sortedBy { tx -> tx.timestamp }
            "AMOUNT_HIGH" -> list.sortedByDescending { tx -> tx.amount }
            "AMOUNT_LOW" -> list.sortedBy { tx -> tx.amount }
            else -> list.sortedByDescending { tx -> tx.timestamp } // NEWEST
        }

        list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- PREMIUM HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkCardSurface)
                        .size(40.dp)
                        .testTag("tx_history_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Riwayat Transaksi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkCardSurface)
                            .size(40.dp)
                            .testTag("tx_history_sort_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Urutkan",
                            tint = PrimaryEmerald
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(DarkCardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Terbaru", color = TextPrimary, fontSize = 13.sp) },
                            onClick = {
                                selectedSortOrder = "NEWEST"
                                showSortMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Terlama", color = TextPrimary, fontSize = 13.sp) },
                            onClick = {
                                selectedSortOrder = "OLDEST"
                                showSortMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Nominal Tertinggi", color = TextPrimary, fontSize = 13.sp) },
                            onClick = {
                                selectedSortOrder = "AMOUNT_HIGH"
                                showSortMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Nominal Terendah", color = TextPrimary, fontSize = 13.sp) },
                            onClick = {
                                selectedSortOrder = "AMOUNT_LOW"
                                showSortMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.TrendingDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            // --- FINANCIAL AGGREGATE CARDS (2x2 Grid) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card 1: Deposit
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Total Deposit", fontSize = 10.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", totalDeposit)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    // Card 2: Penarikan
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Total Penarikan", fontSize = 10.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", totalWithdrawal)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card 3: Hasil Investasi (Profit Claim)
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hasil Investasi", fontSize = 10.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", totalReturns)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        }
                    }

                    // Card 4: Komisi & Bonus (Referrals)
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentGold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Komisi & Bonus", fontSize = 10.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${String.format("%,.0f", totalReferral)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }
                    }
                }
            }

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("tx_history_search_input"),
                placeholder = { Text("Cari ref ID, metode, keterangan...", fontSize = 13.sp, color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryEmerald,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkCardSurface,
                    unfocusedContainerColor = DarkCardSurface
                )
            )

            // --- HORIZONTAL CHIPS FILTER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "Semua",
                    "DEPOSIT" to "Deposit",
                    "WITHDRAWAL" to "Penarikan",
                    "INVESTMENT" to "Investasi & Hasil",
                    "REFERRAL" to "Referral & Komisi"
                )

                filters.forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        onClick = { selectedFilter = key },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryEmerald else DarkCardBorder
                        ),
                        color = if (isSelected) PrimaryEmerald.copy(alpha = 0.15f) else DarkCardSurface,
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("tx_history_chip_$key")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PrimaryEmerald else TextPrimary
                            )
                        }
                    }
                }
            }

            // --- TRANSACTIONS LAZYCOLUMN ---
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak ada transaksi ditemukan.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("tx_history_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTransactions, key = { it.id }) { tx ->
                        TransactionItemRow(
                            tx = tx,
                            onClick = { selectedTransactionForDetail = tx }
                        )
                    }
                }
            }
        }

        // --- TRANSACTION DETAIL DIALOG / BOTTOM SHEET ---
        selectedTransactionForDetail?.let { tx ->
            TransactionDetailDialog(
                tx = tx,
                onDismiss = { selectedTransactionForDetail = null }
            )
        }
    }
}

@Composable
fun TransactionItemRow(
    tx: TransactionRecord,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(tx.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("tx_item_card_${tx.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon Bubble
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (tx.type) {
                                "DEPOSIT" -> SuccessGreen.copy(alpha = 0.15f)
                                "WITHDRAWAL" -> ErrorRed.copy(alpha = 0.15f)
                                "REFERRAL_BONUS", "REFERRAL_COMMISSION" -> AccentGold.copy(alpha = 0.15f)
                                "INVESTMENT" -> PrimaryEmerald.copy(alpha = 0.15f)
                                "PROFIT_CLAIM" -> SuccessGreen.copy(alpha = 0.15f)
                                else -> PrimaryEmerald.copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            "DEPOSIT" -> Icons.Default.ArrowDownward
                            "WITHDRAWAL" -> Icons.Default.ArrowUpward
                            "REFERRAL_BONUS" -> Icons.Default.CardGiftcard
                            "REFERRAL_COMMISSION" -> Icons.Default.GroupAdd
                            "INVESTMENT" -> Icons.Default.TrendingUp
                            "PROFIT_CLAIM" -> Icons.Default.Payments
                            else -> Icons.Default.Receipt
                        },
                        contentDescription = null,
                        tint = when (tx.type) {
                            "DEPOSIT" -> SuccessGreen
                            "WITHDRAWAL" -> ErrorRed
                            "REFERRAL_BONUS", "REFERRAL_COMMISSION" -> AccentGold
                            "INVESTMENT" -> PrimaryEmerald
                            "PROFIT_CLAIM" -> SuccessGreen
                            else -> PrimaryEmerald
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = when (tx.type) {
                            "DEPOSIT" -> "Deposit"
                            "WITHDRAWAL" -> "Penarikan Saldo"
                            "REFERRAL_BONUS" -> "Bonus Referral"
                            "REFERRAL_COMMISSION" -> "Komisi Bagi Hasil"
                            "INVESTMENT" -> "Investasi Baru"
                            "PROFIT_CLAIM" -> "Hasil Investasi"
                            else -> tx.type
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    if (tx.note.isNotBlank()) {
                        Text(
                            text = tx.note,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            softWrap = true
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isPositive = tx.type == "DEPOSIT" || tx.type == "REFERRAL_BONUS" || tx.type == "REFERRAL_COMMISSION" || tx.type == "PROFIT_CLAIM"
                val prefix = if (isPositive) "+" else "-"
                
                Text(
                    text = "$prefix Rp ${String.format("%,.0f", tx.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) SuccessGreen else ErrorRed
                )

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.status.uppercase()) {
                        "SUCCESS" -> SuccessGreen.copy(alpha = 0.15f)
                        "PENDING" -> AccentGold.copy(alpha = 0.15f)
                        else -> ErrorRed.copy(alpha = 0.15f)
                    },
                    border = BorderStroke(
                        0.5.dp,
                        when (tx.status.uppercase()) {
                            "SUCCESS" -> SuccessGreen
                            "PENDING" -> AccentGold
                            else -> ErrorRed
                        }
                    )
                ) {
                    Text(
                        text = when (tx.status.uppercase()) {
                            "SUCCESS" -> "Berhasil"
                            "PENDING" -> "Tertunda"
                            else -> "Gagal"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.status.uppercase()) {
                            "SUCCESS" -> SuccessGreen
                            "PENDING" -> AccentGold
                            else -> ErrorRed
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionDetailDialog(
    tx: TransactionRecord,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
    val dateStr = dateFormat.format(Date(tx.timestamp))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = PrimaryEmerald
                )
                Text(
                    text = "Rincian Transaksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isPositive = tx.type == "DEPOSIT" || tx.type == "REFERRAL_BONUS" || tx.type == "REFERRAL_COMMISSION" || tx.type == "PROFIT_CLAIM"
                DetailRow(label = "Jenis Transaksi", value = when (tx.type) {
                    "DEPOSIT" -> "Deposit Saldo"
                    "WITHDRAWAL" -> "Tarik Saldo"
                    "REFERRAL_BONUS" -> "Bonus Pendaftaran Referral"
                    "REFERRAL_COMMISSION" -> "Komisi Bagi Hasil Investasi"
                    "INVESTMENT" -> "Investasi Baru"
                    "PROFIT_CLAIM" -> "Hasil Investasi"
                    else -> tx.type
                })

                DetailRow(
                    label = "Nominal",
                    value = "${if (isPositive) "+" else "-"} Rp ${String.format("%,.0f", tx.amount)}",
                    valueColor = if (isPositive) SuccessGreen else ErrorRed
                )

                DetailRow(label = "ID Referensi", value = tx.referenceId)

                DetailRow(label = "Metode Pembayaran", value = tx.paymentMethod.ifBlank { "-" })

                DetailRow(label = "Tanggal & Waktu", value = dateStr)

                // Status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Status", fontSize = 12.sp, color = TextSecondary)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (tx.status.uppercase()) {
                            "SUCCESS" -> SuccessGreen.copy(alpha = 0.15f)
                            "PENDING" -> AccentGold.copy(alpha = 0.15f)
                            else -> ErrorRed.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when (tx.status.uppercase()) {
                                "SUCCESS" -> "BERHASIL"
                                "PENDING" -> "PENDING"
                                else -> "GAGAL"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (tx.status.uppercase()) {
                                "SUCCESS" -> SuccessGreen
                                "PENDING" -> AccentGold
                                else -> ErrorRed
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (tx.note.isNotBlank()) {
                    HorizontalDivider(color = DarkCardBorder)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Catatan / Keterangan:", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tx.note,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_close_tx_detail")
            ) {
                Text(text = "Tutup", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkCardSurface
    )
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
