package com.swucollector.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swucollector.app.data.db.entity.CollectionEntryEntity
import com.swucollector.app.data.db.model.PlaysetSummary
import com.swucollector.app.data.db.model.PrintingWithCollection
import com.swucollector.app.data.db.model.SetCompletionSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: CollectionEntryEntity)

    @Query("SELECT * FROM collection_entries WHERE printingId = :printingId")
    suspend fun getEntry(printingId: String): CollectionEntryEntity?

    @Query("""
        SELECT p.printingId, p.cardKey, p.setCode, p.number, p.rarity, p.artist, p.variantType,
               ci.name, ci.subtitle, ci.type, ci.unique,
               COALESCE(ce.normalQty, 0) AS normalQty,
               COALESCE(ce.foilQty, 0) AS foilQty
        FROM printings p
        INNER JOIN card_identities ci ON p.cardKey = ci.cardKey
        LEFT JOIN collection_entries ce ON p.printingId = ce.printingId
        ORDER BY p.setCode, CAST(p.number AS INTEGER)
    """)
    fun getPrintingsWithCollection(): Flow<List<PrintingWithCollection>>

    @Query("""
        SELECT ci.cardKey, ci.name, ci.subtitle, ci.type, ci.aspects, ci.unique,
               COALESCE(SUM(COALESCE(ce.normalQty, 0) + COALESCE(ce.foilQty, 0)), 0) AS totalOwned
        FROM card_identities ci
        LEFT JOIN printings p ON ci.cardKey = p.cardKey
        LEFT JOIN collection_entries ce ON p.printingId = ce.printingId
        GROUP BY ci.cardKey
        ORDER BY ci.name, ci.subtitle
    """)
    fun getPlaysetSummary(): Flow<List<PlaysetSummary>>

    @Query("""
        SELECT p.setCode,
               COUNT(DISTINCT p.printingId) AS totalPrintings,
               COUNT(DISTINCT CASE WHEN (COALESCE(ce.normalQty, 0) + COALESCE(ce.foilQty, 0)) > 0
                                   THEN p.printingId END) AS ownedPrintings
        FROM printings p
        LEFT JOIN collection_entries ce ON p.printingId = ce.printingId
        GROUP BY p.setCode
        ORDER BY p.setCode
    """)
    fun getSetCompletion(): Flow<List<SetCompletionSummary>>

    @Query("UPDATE collection_entries SET normalQty = :qty WHERE printingId = :printingId")
    suspend fun updateNormalQty(printingId: String, qty: Int)

    @Query("UPDATE collection_entries SET foilQty = :qty WHERE printingId = :printingId")
    suspend fun updateFoilQty(printingId: String, qty: Int)
}
