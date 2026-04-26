package com.swucollector.app.data.repository

import com.swucollector.app.data.db.dao.CardIdentityDao
import com.swucollector.app.data.db.dao.DeckDao
import com.swucollector.app.data.db.entity.CardIdentityEntity
import com.swucollector.app.data.db.entity.DeckCardEntity
import com.swucollector.app.data.db.entity.DeckEntity
import com.swucollector.app.data.db.model.DeckCardWithIdentity
import com.swucollector.app.data.db.model.DeckWithCards
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepository @Inject constructor(
    private val deckDao: DeckDao,
    private val cardIdentityDao: CardIdentityDao
) {
    fun getAllDecks(): Flow<List<DeckEntity>> = deckDao.getAllDecks()

    fun getDeckWithCards(deckId: Long): Flow<DeckWithCards?> =
        deckDao.getDeckWithCards(deckId)

    fun getDeckCardsWithIdentity(deckId: Long): Flow<List<DeckCardWithIdentity>> =
        deckDao.getDeckCardsWithIdentity(deckId)

    suspend fun createDeck(name: String, leaderCardKey: String, baseCardKey: String): Long =
        deckDao.insertDeck(DeckEntity(name = name, leaderCardKey = leaderCardKey, baseCardKey = baseCardKey))

    suspend fun renameDeck(deckId: Long, newName: String) {
        val deck = deckDao.getDeckByIdOnce(deckId) ?: return
        deckDao.updateDeck(deck.copy(name = newName, updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateLeader(deckId: Long, leaderCardKey: String) {
        val deck = deckDao.getDeckByIdOnce(deckId) ?: return
        deckDao.updateDeck(deck.copy(leaderCardKey = leaderCardKey, updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateBase(deckId: Long, baseCardKey: String) {
        val deck = deckDao.getDeckByIdOnce(deckId) ?: return
        deckDao.updateDeck(deck.copy(baseCardKey = baseCardKey, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteDeck(deck: DeckEntity) = deckDao.deleteDeck(deck)

    suspend fun setCardQuantity(deckId: Long, cardKey: String, quantity: Int) {
        if (quantity <= 0) {
            deckDao.removeDeckCardByKey(deckId, cardKey)
        } else {
            deckDao.upsertDeckCard(DeckCardEntity(deckId = deckId, cardKey = cardKey, quantity = quantity))
            val deck = deckDao.getDeckByIdOnce(deckId) ?: return
            deckDao.updateDeck(deck.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun getLeaders(): List<CardIdentityEntity> = cardIdentityDao.getLeaders()

    suspend fun getBases(): List<CardIdentityEntity> = cardIdentityDao.getBases()

    fun searchCards(query: String): Flow<List<CardIdentityEntity>> =
        cardIdentityDao.searchNonCommandCards(query)
}
