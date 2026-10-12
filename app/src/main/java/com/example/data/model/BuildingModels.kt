package com.example.data.model

data class RoomPlan(
    val name: String,
    val type: String, // Bedroom, Kitchen, Bathroom, Living Room, Dining Room, Balcony, Corridor
    val x: Float = 0f,
    val z: Float = 0f,
    val width: Float = 5f,
    val length: Float = 5f,
    val color: String = "#7dd3fc"
)

data class FloorPlan(
    val name: String,
    val floorNumber: Int,
    val rooms: List<RoomPlan> = emptyList()
)

data class BuildingPlan(
    val id: Int = 0,
    val name: String,
    val type: String = "Residential", // Residential, Commercial, Apartment
    val plotWidth: Float = 16f,
    val plotLength: Float = 14f,
    val totalArea: Float = 224f,
    val floorCount: Int = 2,
    val floorHeight: Float = 3.2f,
    val roofStyle: String = "Flat Terrace", // Flat Terrace, Sloped Pitch
    val buildingStyle: String = "Modern Minimalist",
    val floors: List<FloorPlan> = emptyList(),
    val roomColors: Map<String, String> = defaultRoomColors(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun defaultRoomColors(): Map<String, String> = mapOf(
            "Bedroom" to "#7dd3fc",
            "Kitchen" to "#fdba74",
            "Bathroom" to "#86efac",
            "Living Room" to "#fde047",
            "Dining Room" to "#f472b6",
            "Balcony" to "#cbd5e1",
            "Corridor" to "#e2e8f0",
            "Staircase" to "#94a3b8"
        )

        fun createSample(name: String = "Villa Horizon"): BuildingPlan {
            val groundRooms = listOf(
                RoomPlan("Living Room", "Living Room", -4f, 3f, 8f, 7f, "#fde047"),
                RoomPlan("Kitchen", "Kitchen", 4.5f, 4.5f, 6f, 5f, "#fdba74"),
                RoomPlan("Dining Room", "Dining Room", 4.5f, -1f, 6f, 5f, "#f472b6"),
                RoomPlan("Guest Bath", "Bathroom", -4f, -4f, 4.5f, 4f, "#86efac")
            )
            val firstRooms = listOf(
                RoomPlan("Master Bedroom", "Bedroom", -4f, 3f, 8f, 7f, "#7dd3fc"),
                RoomPlan("Guest Bedroom", "Bedroom", 4.5f, -1f, 6f, 6f, "#7dd3fc"),
                RoomPlan("Master Bath", "Bathroom", 4.5f, 4.5f, 5f, 4f, "#86efac"),
                RoomPlan("Balcony", "Balcony", -4f, -4f, 7f, 4f, "#cbd5e1")
            )
            return BuildingPlan(
                name = name,
                type = "Residential",
                plotWidth = 18f,
                plotLength = 16f,
                totalArea = 288f,
                floorCount = 2,
                floorHeight = 3.2f,
                roofStyle = "Flat Terrace",
                floors = listOf(
                    FloorPlan("Ground Floor", 0, groundRooms),
                    FloorPlan("First Floor", 1, firstRooms)
                ),
                roomColors = defaultRoomColors()
            )
        }
    }
}
