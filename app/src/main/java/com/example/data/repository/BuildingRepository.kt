package com.example.data.repository

import com.example.data.local.BuildingDao
import com.example.data.local.BuildingJsonConverter
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.model.BuildingPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BuildingRepository(
    private val buildingDao: BuildingDao,
    private val userDao: UserDao
) {
    val allBuildings: Flow<List<BuildingPlan>> = buildingDao.getAllBuildings().map { entities ->
        entities.map { BuildingJsonConverter.toModel(it) }
    }

    val activeUser: Flow<UserEntity?> = userDao.getActiveUser()

    suspend fun getBuildingById(id: Int): BuildingPlan? {
        val entity = buildingDao.getBuildingById(id) ?: return null
        return BuildingJsonConverter.toModel(entity)
    }

    suspend fun saveBuilding(plan: BuildingPlan): Long {
        val entity = BuildingJsonConverter.toEntity(plan)
        return if (plan.id == 0) {
            buildingDao.insertBuilding(entity)
        } else {
            buildingDao.updateBuilding(entity)
            plan.id.toLong()
        }
    }

    suspend fun deleteBuilding(id: Int) {
        buildingDao.deleteBuildingById(id)
    }

    suspend fun loginOrRegister(username: String, email: String, fullName: String) {
        userDao.clearUsers()
        userDao.insertUser(UserEntity(username = username, email = email, fullName = fullName))
    }

    suspend fun logout() {
        userDao.clearUsers()
    }
}
