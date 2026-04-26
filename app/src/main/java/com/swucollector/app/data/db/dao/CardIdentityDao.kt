package com.swucollector.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swucollector.app.data.db.entity.CardIdentityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardIdentityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(cards: List<CardIdentityEntity>)

    @Query("SELECT * FROM card_identities WHERE type = 'Leader' ORDER BY name, subtitle")
    suspend fun getLeaders(): List<CardIdentityEntity>

    @Query("SELECT * FROM card_identities WHERE type = 'Base' ORDER BY name, subtitle")
    suspend fun getBases(): List<CardIdentityEntity>

    @Query("SELECT * FROM card_identities WHERE cardKey = :cardKey")
    suspend fun getByKey(cardKey: String): CardIdentityEntity?

    @Query("""
        SELECT * FROM card_identities
        WHERE type NOT IN ('Leader', 'Base')
        AND (:query = '' OR name LIKE '%' || :query || '%'
             OR subtitle LIKE '%' || :query || '%'
             OR traits LIKE '%' || :query || '%')
        ORDER BY name, subtitle
    """)
    fun searchNonCommandCards(query: String): Flow<List<CardIdentityEntity>>

    @Query("SELECT COUNT(*) FROM card_identities")
    suspend fun count(): Int
}
