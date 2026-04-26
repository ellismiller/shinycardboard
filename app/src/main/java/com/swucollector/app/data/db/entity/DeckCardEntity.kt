package com.swucollector.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "deck_cards",
    primaryKeys = ["deckId", "cardKey"],
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["deckId"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
data class DeckCardEntity(
    val deckId: Long,
    val cardKey: String,
    val quantity: Int
)
