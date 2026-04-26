package com.swucollector.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swucollector.app.data.db.entity.PrintingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(printings: List<PrintingEntity>)

    @Query("SELECT DISTINCT setCode FROM printings ORDER BY setCode")
    fun getDistinctSets(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM printings")
    suspend fun count(): Int
}
