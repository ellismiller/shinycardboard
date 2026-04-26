package com.swucollector.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swucollector.app.data.db.dao.CardIdentityDao
import com.swucollector.app.data.db.dao.CollectionDao
import com.swucollector.app.data.db.dao.DeckDao
import com.swucollector.app.data.db.dao.PrintingDao
import com.swucollector.app.data.db.entity.CardIdentityEntity
import com.swucollector.app.data.db.entity.CollectionEntryEntity
import com.swucollector.app.data.db.entity.DeckCardEntity
import com.swucollector.app.data.db.entity.DeckEntity
import com.swucollector.app.data.db.entity.PrintingEntity

@Database(
    entities = [
        CardIdentityEntity::class,
        PrintingEntity::class,
        CollectionEntryEntity::class,
        DeckEntity::class,
        DeckCardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SwuDatabase : RoomDatabase() {
    abstract fun cardIdentityDao(): CardIdentityDao
    abstract fun printingDao(): PrintingDao
    abstract fun collectionDao(): CollectionDao
    abstract fun deckDao(): DeckDao
}
