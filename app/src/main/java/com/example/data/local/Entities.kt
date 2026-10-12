package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buildings")
data class BuildingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val plotWidth: Float,
    val plotLength: Float,
    val totalArea: Float,
    val floorCount: Int,
    val floorHeight: Float,
    val roofStyle: String,
    val buildingStyle: String,
    val floorsJson: String,
    val roomColorsJson: String,
    val userId: String = "guest",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val fullName: String,
    val createdAt: Long = System.currentTimeMillis()
)
