package com.example.hm_third_count.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Delete
    suspend fun delete(entity: FavoriteEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE countryCode = :code)")
    suspend fun isFavorite(code: String): Boolean
}
