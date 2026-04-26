package com.swucollector.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.swucollector.app.data.db.entity.DeckCardEntity
import com.swucollector.app.data.db.entity.DeckEntity
import com.swucollector.app.data.db.model.DeckCardWithIdentity
import com.swucollector.app.data.db.model.DeckWithCards
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity): Long

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)

    @Query("SELECT * FROM decks ORDER BY updatedAt DESC")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE deckId = :deckId")
    suspend fun getDeckByIdOnce(deckId: Long): DeckEntity?

    @Transaction
    @Query("SELECT * FROM decks WHERE deckId = :deckId")
    fun getDeckWithCards(deckId: Long): Flow<DeckWithCards?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeckCard(card: DeckCardEntity)

    @Delete
    suspend fun removeDeckCard(card: DeckCardEntity)

    @Query("DELETE FROM deck_cards WHERE deckId = :deckId AND cardKey = :cardKey")
    suspend fun removeDeckCardByKey(deckId: Long, cardKey: String)

    @Query("""
        SELECT dc.deckId, dc.cardKey, dc.quantity,
               ci.name, ci.subtitle, ci.type, ci.aspects, ci.unique
        FROM deck_cards dc
        INNER JOIN card_identities ci ON dc.cardKey = ci.cardKey
        WHERE dc.deckId = :deckId
        ORDER BY ci.name, ci.subtitle
    """)
    fun getDeckCardsWithIdentity(deckId: Long): Flow<List<DeckCardWithIdentity>>
}
