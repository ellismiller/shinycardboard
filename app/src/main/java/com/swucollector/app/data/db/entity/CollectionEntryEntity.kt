package com.swucollector.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_entries",
    foreignKeys = [
        ForeignKey(
            entity = PrintingEntity::class,
            parentColumns = ["printingId"],
            childColumns = ["printingId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CollectionEntryEntity(
    @PrimaryKey val printingId: String,
    val normalQty: Int = 0,
    val foilQty: Int = 0
)
