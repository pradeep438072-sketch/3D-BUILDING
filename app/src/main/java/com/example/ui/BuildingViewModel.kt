package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BuildingJsonConverter
import com.example.data.local.UserEntity
import com.example.data.model.BuildingPlan
import com.example.data.model.FloorPlan
import com.example.data.model.RoomPlan
import com.example.data.repository.BuildingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    DASHBOARD,
    PLAN_INPUT,
    FLOOR_CONFIG,
    VISUALIZATION,
    COLOR_PICKER,
    SAVED_PROJECTS,
    REPORT_DOWNLOAD,
    AUTH
}

class BuildingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BuildingRepository
    val savedProjects: StateFlow<List<BuildingPlan>>
    val activeUser: StateFlow<UserEntity?>

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf(AppScreen.HOME)

    private val _currentPlan = MutableStateFlow(BuildingPlan.createSample())
    val currentPlan: StateFlow<BuildingPlan> = _currentPlan.asStateFlow()

    private val _selectedFloorIndex = MutableStateFlow(-1) // -1 = All Floors
    val selectedFloorIndex: StateFlow<Int> = _selectedFloorIndex.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    private val _isDarkTheme = MutableStateFlow(false) // Default to Clean Studio Light theme for max visibility
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    private val _exportedObjContent = MutableStateFlow<String?>(null)
    val exportedObjContent: StateFlow<String?> = _exportedObjContent.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BuildingRepository(database.buildingDao(), database.userDao())

        savedProjects = repository.allBuildings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeUser = repository.activeUser.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Seed with sample if first launch
        viewModelScope.launch {
            savedProjects.collect { list ->
                if (list.isEmpty()) {
                    repository.saveBuilding(BuildingPlan.createSample("Modern Villa Horizon"))
                    repository.saveBuilding(BuildingPlan.createSample("Emerald Commercial Hub").copy(
                        type = "Commercial",
                        plotWidth = 20f,
                        plotLength = 18f,
                        floorCount = 3,
                        roofStyle = "Flat Terrace",
                        floors = listOf(
                            FloorPlan("Ground Floor - Retail", 0, listOf(
                                RoomPlan("Main Lobby", "Living Room", 0f, 0f, 10f, 8f, "#fde047"),
                                RoomPlan("Cafeteria", "Dining Room", -5f, -5f, 6f, 5f, "#f472b6"),
                                RoomPlan("Restrooms", "Bathroom", 5f, -5f, 5f, 4f, "#86efac")
                            )),
                            FloorPlan("First Floor - Offices", 1, listOf(
                                RoomPlan("Executive Suite", "Bedroom", -4f, 3f, 7f, 6f, "#7dd3fc"),
                                RoomPlan("Open Workspace", "Living Room", 3f, 2f, 8f, 7f, "#fde047"),
                                RoomPlan("Meeting Room", "Dining Room", 0f, -4f, 6f, 5f, "#f472b6")
                            )),
                            FloorPlan("Second Floor - Penthouse", 2, listOf(
                                RoomPlan("Conference Hall", "Living Room", 0f, 2f, 12f, 8f, "#fde047"),
                                RoomPlan("Terrace Lounge", "Balcony", 0f, -5f, 12f, 5f, "#cbd5e1")
                            ))
                        )
                    ))
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        return if (screenBackStack.size > 1) {
            screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = screenBackStack.last()
            true
        } else {
            false
        }
    }

    fun setPlan(plan: BuildingPlan) {
        _currentPlan.value = plan
    }

    fun updatePlanMetadata(
        name: String,
        type: String,
        plotWidth: Float,
        plotLength: Float,
        floorCount: Int,
        roofStyle: String,
        buildingStyle: String
    ) {
        val old = _currentPlan.value
        val safeCount = floorCount.coerceIn(1, 6)
        val newFloors = generateFloorsForCount(safeCount, plotWidth, plotLength, old.floors)
        val area = plotWidth * plotLength

        _currentPlan.value = old.copy(
            name = name,
            type = type,
            plotWidth = plotWidth,
            plotLength = plotLength,
            totalArea = area,
            floorCount = safeCount,
            roofStyle = roofStyle,
            buildingStyle = buildingStyle,
            floors = newFloors,
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun generateFloorsForCount(
        count: Int,
        plotW: Float,
        plotL: Float,
        existing: List<FloorPlan>
    ): List<FloorPlan> {
        val list = mutableListOf<FloorPlan>()
        for (i in 0 until count) {
            val floorName = when (i) {
                0 -> "Ground Floor"
                1 -> "First Floor"
                2 -> "Second Floor"
                3 -> "Third Floor"
                4 -> "Fourth Floor"
                else -> "Floor ${i + 1}"
            }
            if (i < existing.size) {
                list.add(existing[i].copy(name = floorName, floorNumber = i))
            } else {
                // Generate layout automatically
                val halfW = plotW / 2f
                val halfL = plotL / 2f
                val rooms = listOf(
                    RoomPlan("Room ${i + 1}A (Living/Bed)", if (i == 0) "Living Room" else "Bedroom", -halfW / 2, halfL / 2, halfW * 0.85f, halfL * 0.85f, if (i == 0) "#fde047" else "#7dd3fc"),
                    RoomPlan("Room ${i + 1}B (Kitchen/Bed)", if (i == 0) "Kitchen" else "Bedroom", halfW / 2, halfL / 2, halfW * 0.85f, halfL * 0.85f, if (i == 0) "#fdba74" else "#7dd3fc"),
                    RoomPlan("Room ${i + 1}C (Dining/Balcony)", if (i == 0) "Dining Room" else "Balcony", halfW / 2, -halfL / 2, halfW * 0.85f, halfL * 0.85f, if (i == 0) "#f472b6" else "#cbd5e1"),
                    RoomPlan("Bath ${i + 1}", "Bathroom", -halfW / 2, -halfL / 2, halfW * 0.6f, halfL * 0.6f, "#86efac")
                )
                list.add(FloorPlan(name = floorName, floorNumber = i, rooms = rooms))
            }
        }
        return list
    }

    fun updateRoomColor(roomType: String, hexColor: String) {
        val current = _currentPlan.value
        val updatedColors = current.roomColors.toMutableMap()
        updatedColors[roomType] = hexColor

        // Also update all rooms of this type
        val updatedFloors = current.floors.map { floor ->
            val updatedRooms = floor.rooms.map { room ->
                if (room.type.equals(roomType, ignoreCase = true)) {
                    room.copy(color = hexColor)
                } else {
                    room
                }
            }
            floor.copy(rooms = updatedRooms)
        }

        _currentPlan.value = current.copy(
            roomColors = updatedColors,
            floors = updatedFloors,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun updateFloorRooms(floorIndex: Int, newRooms: List<RoomPlan>) {
        val current = _currentPlan.value
        val updatedFloors = current.floors.toMutableList()
        if (floorIndex in 0 until updatedFloors.size) {
            updatedFloors[floorIndex] = updatedFloors[floorIndex].copy(rooms = newRooms)
            _currentPlan.value = current.copy(floors = updatedFloors, updatedAt = System.currentTimeMillis())
        }
    }

    fun selectFloor(index: Int) {
        _selectedFloorIndex.value = index
    }

    fun saveCurrentPlan() {
        viewModelScope.launch {
            val plan = _currentPlan.value
            val id = repository.saveBuilding(plan)
            if (plan.id == 0) {
                _currentPlan.value = plan.copy(id = id.toInt())
            }
            _userMessage.emit("Building plan '${plan.name}' saved successfully!")
        }
    }

    fun deletePlan(id: Int) {
        viewModelScope.launch {
            repository.deleteBuilding(id)
            _userMessage.emit("Project deleted.")
        }
    }

    fun duplicatePlan(plan: BuildingPlan) {
        viewModelScope.launch {
            val dup = plan.copy(id = 0, name = "${plan.name} (Copy)", updatedAt = System.currentTimeMillis())
            repository.saveBuilding(dup)
            _userMessage.emit("Project duplicated.")
        }
    }

    fun login(username: String, email: String, fullName: String) {
        viewModelScope.launch {
            repository.loginOrRegister(username, email, fullName)
            _userMessage.emit("Logged in as $fullName")
            _currentScreen.value = AppScreen.DASHBOARD
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _userMessage.emit("Logged out.")
        }
    }

    fun setExportedObj(content: String) {
        _exportedObjContent.value = content
    }

    fun getThreeJsJson(): String {
        return BuildingJsonConverter.toThreeJsJson(_currentPlan.value)
    }
}
