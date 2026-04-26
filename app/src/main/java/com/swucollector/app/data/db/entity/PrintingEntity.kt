package com.swucollector.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "printings",
    foreignKeys = [
        ForeignKey(
            entity = CardIdentityEntity::class,
            parentColumns = ["cardKey"],
            childColumns = ["cardKey"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardKey"), Index("setCode")]
)
data class PrintingEntity(
    @PrimaryKey val printingId: String,
    val cardKey: String,
    val setCode: String,
    val number: String,
    val rarity: String,
    val artist: String?,
    val variantType: String?
)
