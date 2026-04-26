package com.swucollector.app.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.swucollector.app.data.db.entity.DeckCardEntity
import com.swucollector.app.data.db.entity.DeckEntity

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(parentColumn = "deckId", entityColumn = "deckId")
    val cards: List<DeckCardEntity>
)
