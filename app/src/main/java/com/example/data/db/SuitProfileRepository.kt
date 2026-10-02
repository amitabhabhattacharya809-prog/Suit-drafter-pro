package com.example.data.db

import kotlinx.coroutines.flow.Flow

class SuitProfileRepository(private val suitProfileDao: SuitProfileDao) {

    val allProfiles: Flow<List<SuitProfileEntity>> = suitProfileDao.getAllProfiles()

    suspend fun getProfileById(id: Long): SuitProfileEntity? {
        return suitProfileDao.getProfileById(id)
    }

    suspend fun insertProfile(profile: SuitProfileEntity): Long {
        return suitProfileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: SuitProfileEntity) {
        suitProfileDao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: SuitProfileEntity) {
        suitProfileDao.deleteProfile(profile)
    }

    suspend fun deleteProfileById(id: Long) {
        suitProfileDao.deleteProfileById(id)
    }
}
