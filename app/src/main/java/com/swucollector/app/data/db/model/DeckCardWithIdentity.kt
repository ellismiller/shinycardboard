package com.swucollector.app.data.db.model

data class DeckCardWithIdentity(
    val deckId: Long,
    val cardKey: String,
    val quantity: Int,
    val name: String,
    val subtitle: String?,
    val type: String,
    val aspects: String,
    val unique: Boolean
)
