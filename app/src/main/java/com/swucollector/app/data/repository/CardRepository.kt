package com.swucollector.app.data.repository

import com.swucollector.app.data.api.SwuApiService
import com.swucollector.app.data.db.dao.CardIdentityDao
import com.swucollector.app.data.db.dao.CollectionDao
import com.swucollector.app.data.db.dao.PrintingDao
import com.swucollector.app.data.db.entity.CardIdentityEntity
import com.swucollector.app.data.db.entity.CollectionEntryEntity
import com.swucollector.app.data.db.entity.PrintingEntity
import com.swucollector.app.data.db.model.PlaysetSummary
import com.swucollector.app.data.db.model.PrintingWithCollection
import com.swucollector.app.data.db.model.SetCompletionSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val api: SwuApiService,
    private val cardIdentityDao: CardIdentityDao,
    private val printingDao: PrintingDao,
    private val collectionDao: CollectionDao
) {
    private val knownSets = listOf("SOR", "SHD", "TWI", "JTL", "OP")

    suspend fun syncAll() {
        knownSets.forEach { setCode ->
            runCatching { syncSet(setCode) }
        }
    }

    private suspend fun syncSet(setCode: String) {
        val cards = api.getCardsBySet(setCode).data
        val identities = mutableListOf<CardIdentityEntity>()
        val printings = mutableListOf<PrintingEntity>()

        for (card in cards) {
            val cardKey = generateCardKey(card.name, card.subtitle)
            identities += CardIdentityEntity(
                cardKey = cardKey,
                name = card.name,
                subtitle = card.subtitle,
                type = card.type,
                aspects = card.aspects?.joinToString(",") ?: "",
                traits = card.traits?.joinToString(",") ?: "",
                unique = card.unique ?: false
            )
            printings += PrintingEntity(
                printingId = "${card.set}-${card.number}",
                cardKey = cardKey,
                setCode = card.set,
                number = card.number,
                rarity = card.rarity,
                artist = card.artist,
                variantType = card.variantType
            )
        }

        cardIdentityDao.upsertAll(identities.distinctBy { it.cardKey })
        printingDao.upsertAll(printings)
    }

    suspend fun isDatabaseEmpty(): Boolean = printingDao.count() == 0

    fun getPrintingsWithCollection(): Flow<List<PrintingWithCollection>> =
        collectionDao.getPrintingsWithCollection()

    fun getPlaysetSummary(): Flow<List<PlaysetSummary>> =
        collectionDao.getPlaysetSummary()

    fun getSetCompletion(): Flow<List<SetCompletionSummary>> =
        collectionDao.getSetCompletion()

    suspend fun setNormalQty(printingId: String, qty: Int) {
        ensureCollectionEntry(printingId)
        collectionDao.updateNormalQty(printingId, qty.coerceAtLeast(0))
    }

    suspend fun setFoilQty(printingId: String, qty: Int) {
        ensureCollectionEntry(printingId)
        collectionDao.updateFoilQty(printingId, qty.coerceAtLeast(0))
    }

    private suspend fun ensureCollectionEntry(printingId: String) {
        if (collectionDao.getEntry(printingId) == null) {
            collectionDao.upsert(CollectionEntryEntity(printingId = printingId))
        }
    }

    companion object {
        fun generateCardKey(name: String, subtitle: String?): String {
            val full = if (subtitle != null) "$name $subtitle" else name
            return full.lowercase()
                .replace(Regex("[^a-z0-9]+"), "-")
                .trim('-')
        }

        fun playsetThreshold(type: String): Int = when (type.lowercase()) {
            "leader", "base" -> 1
            else -> 3
        }
    }
}
