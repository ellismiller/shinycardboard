package com.swucollector.app.data.db.model

data class PrintingWithCollection(
    val printingId: String,
    val cardKey: String,
    val setCode: String,
    val number: String,
    val rarity: String,
    val artist: String?,
    val variantType: String?,
    val name: String,
    val subtitle: String?,
    val type: String,
    val unique: Boolean,
    val normalQty: Int,
    val foilQty: Int
)
