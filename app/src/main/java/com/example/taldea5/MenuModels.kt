package com.example.taldea5

import androidx.annotation.DrawableRes

enum class MenuSection { Platerak, Edariak, Postreak }

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val section: MenuSection,
    val shortInfo: String,
    val ingredientsText: String,
    @DrawableRes val imageRes: Int,
    val isPlatera: Boolean = false
)
