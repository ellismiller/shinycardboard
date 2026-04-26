package com.swucollector.app.data.db.model

data class PlaysetSummary(
    val cardKey: String,
    val name: String,
    val subtitle: String?,
    val type: String,
    val aspects: String,
    val unique: Boolean,
    val totalOwned: Int
)
