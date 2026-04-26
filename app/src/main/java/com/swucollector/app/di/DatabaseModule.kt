package com.swucollector.app.di

import android.content.Context
import androidx.room.Room
import com.swucollector.app.data.db.SwuDatabase
import com.swucollector.app.data.db.dao.CardIdentityDao
import com.swucollector.app.data.db.dao.CollectionDao
import com.swucollector.app.data.db.dao.DeckDao
import com.swucollector.app.data.db.dao.PrintingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SwuDatabase =
        Room.databaseBuilder(context, SwuDatabase::class.java, "swu_collection.db")
            .build()

    @Provides fun provideCardIdentityDao(db: SwuDatabase): CardIdentityDao = db.cardIdentityDao()
    @Provides fun providePrintingDao(db: SwuDatabase): PrintingDao = db.printingDao()
    @Provides fun provideCollectionDao(db: SwuDatabase): CollectionDao = db.collectionDao()
    @Provides fun provideDeckDao(db: SwuDatabase): DeckDao = db.deckDao()
}
