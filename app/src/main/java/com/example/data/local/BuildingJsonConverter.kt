package com.example.data.local

import com.example.data.model.BuildingPlan
import com.example.data.model.FloorPlan
import com.example.data.model.RoomPlan
import org.json.JSONArray
import org.json.JSONObject

object BuildingJsonConverter {

    fun toEntity(plan: BuildingPlan): BuildingEntity {
        val floorsArray = JSONArray()
        plan.floors.forEach { floor ->
            val floorObj = JSONObject()
            floorObj.put("name", floor.name)
            floorObj.put("floorNumber", floor.floorNumber)
            val roomsArray = JSONArray()
            floor.rooms.forEach { room ->
                val roomObj = JSONObject()
                roomObj.put("name", room.name)
                roomObj.put("type", room.type)
                roomObj.put("x", room.x.toDouble())
                roomObj.put("z", room.z.toDouble())
                roomObj.put("width", room.width.toDouble())
                roomObj.put("length", room.length.toDouble())
                roomObj.put("color", room.color)
                roomsArray.put(roomObj)
            }
            floorObj.put("rooms", roomsArray)
            floorsArray.put(floorObj)
        }

        val colorsObj = JSONObject()
        plan.roomColors.forEach { (type, color) ->
            colorsObj.put(type, color)
        }

        return BuildingEntity(
            id = plan.id,
            name = plan.name,
            type = plan.type,
            plotWidth = plan.plotWidth,
            plotLength = plan.plotLength,
            totalArea = plan.totalArea,
            floorCount = plan.floorCount,
            floorHeight = plan.floorHeight,
            roofStyle = plan.roofStyle,
            buildingStyle = plan.buildingStyle,
            floorsJson = floorsArray.toString(),
            roomColorsJson = colorsObj.toString(),
            updatedAt = plan.updatedAt
        )
    }

    fun toModel(entity: BuildingEntity): BuildingPlan {
        val floorsList = mutableListOf<FloorPlan>()
        try {
            val floorsArray = JSONArray(entity.floorsJson)
            for (i in 0 until floorsArray.length()) {
                val fObj = floorsArray.getJSONObject(i)
                val fName = fObj.optString("name", "Floor ${i + 1}")
                val fNum = fObj.optInt("floorNumber", i)
                val roomsArray = fObj.optJSONArray("rooms") ?: JSONArray()
                val roomsList = mutableListOf<RoomPlan>()
                for (j in 0 until roomsArray.length()) {
                    val rObj = roomsArray.getJSONObject(j)
                    roomsList.add(
                        RoomPlan(
                            name = rObj.optString("name", "Room"),
                            type = rObj.optString("type", "Bedroom"),
                            x = rObj.optDouble("x", 0.0).toFloat(),
                            z = rObj.optDouble("z", 0.0).toFloat(),
                            width = rObj.optDouble("width", 4.0).toFloat(),
                            length = rObj.optDouble("length", 4.0).toFloat(),
                            color = rObj.optString("color", "#7dd3fc")
                        )
                    )
                }
                floorsList.add(FloorPlan(name = fName, floorNumber = fNum, rooms = roomsList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val colorsMap = mutableMapOf<String, String>()
        try {
            val colorsObj = JSONObject(entity.roomColorsJson)
            val keys = colorsObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                colorsMap[key] = colorsObj.getString(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return BuildingPlan(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            plotWidth = entity.plotWidth,
            plotLength = entity.plotLength,
            totalArea = entity.totalArea,
            floorCount = entity.floorCount,
            floorHeight = entity.floorHeight,
            roofStyle = entity.roofStyle,
            buildingStyle = entity.buildingStyle,
            floors = if (floorsList.isNotEmpty()) floorsList else BuildingPlan.createSample().floors,
            roomColors = if (colorsMap.isNotEmpty()) colorsMap else BuildingPlan.defaultRoomColors(),
            updatedAt = entity.updatedAt
        )
    }

    fun toThreeJsJson(plan: BuildingPlan): String {
        val root = JSONObject()
        root.put("name", plan.name)
        root.put("type", plan.type)
        root.put("plotWidth", plan.plotWidth.toDouble())
        root.put("plotLength", plan.plotLength.toDouble())
        root.put("floorHeight", plan.floorHeight.toDouble())
        root.put("roofStyle", plan.roofStyle)

        val colorsObj = JSONObject()
        plan.roomColors.forEach { (k, v) -> colorsObj.put(k, v) }
        root.put("roomColors", colorsObj)

        val floorsArray = JSONArray()
        plan.floors.forEach { floor ->
            val fObj = JSONObject()
            fObj.put("name", floor.name)
            fObj.put("floorNumber", floor.floorNumber)
            val rArray = JSONArray()
            floor.rooms.forEach { room ->
                val rObj = JSONObject()
                rObj.put("name", room.name)
                rObj.put("type", room.type)
                rObj.put("x", room.x.toDouble())
                rObj.put("z", room.z.toDouble())
                rObj.put("width", room.width.toDouble())
                rObj.put("length", room.length.toDouble())
                val assignedColor = plan.roomColors[room.type] ?: room.color
                rObj.put("color", assignedColor)
                rArray.put(rObj)
            }
            fObj.put("rooms", rArray)
            floorsArray.put(fObj)
        }
        root.put("floors", floorsArray)
        return root.toString()
    }
}
