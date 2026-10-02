package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SuitProfileDao {

    @Query("SELECT * FROM suit_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<SuitProfileEntity>>

    @Query("SELECT * FROM suit_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): SuitProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SuitProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: SuitProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: SuitProfileEntity)

    @Query("DELETE FROM suit_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)
}
