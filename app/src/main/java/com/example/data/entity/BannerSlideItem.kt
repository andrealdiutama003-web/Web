package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banner_slides")
data class BannerSlideItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subtitle: String,
    val badgeText: String = "PROMO",
    val imageUrl: String = "",
    val actionLabel: String = "Lihat Detail",
    val isActive: Boolean = true,
    val displayOrder: Int = 0
)
