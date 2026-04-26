package com.swucollector.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_identities")
data class CardIdentityEntity(
    @PrimaryKey val cardKey: String,
    val name: String,
    val subtitle: String?,
    val type: String,
    val aspects: String,
    val traits: String,
    val unique: Boolean
)
