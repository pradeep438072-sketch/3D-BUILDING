package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildingPlan
import com.example.data.model.RoomPlan
import com.example.ui.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorConfigScreen(
    currentPlan: BuildingPlan,
    onUpdateFloorRooms: (floorIndex: Int, newRooms: List<RoomPlan>) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFloorIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    val floors = currentPlan.floors
    val activeFloor = floors.getOrNull(selectedFloorIndex) ?: floors.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("floor_config_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Floor & Room Configuration",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Customize room layouts, names, dimensions and types per floor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Floor selector tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(floors.size) { index ->
                    val floor = floors[index]
                    FilterChip(
                        selected = selectedFloorIndex == index,
                        onClick = { selectedFloorIndex = index },
                        label = { Text(floor.name) },
                        leadingIcon = {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // Floor overview summary
        if (activeFloor != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeFloor.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${activeFloor.rooms.size} Rooms Configured",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.testTag("btn_add_room")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Room")
                        }
                    }
                }
            }

            // Room list for this floor
            itemsIndexed(activeFloor.rooms) { roomIndex, room ->
                RoomConfigCard(
                    room = room,
                    onDelete = {
                        val updated = activeFloor.rooms.toMutableList()
                        updated.removeAt(roomIndex)
                        onUpdateFloorRooms(selectedFloorIndex, updated)
                    }
                )
            }
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onNavigate(AppScreen.VISUALIZATION) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("visualize_in_3d_btn"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View in 3D Model", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Room Dialog
    if (showAddDialog && activeFloor != null) {
        AddRoomDialog(
            plotWidth = currentPlan.plotWidth,
            plotLength = currentPlan.plotLength,
            onDismiss = { showAddDialog = false },
            onConfirm = { newRoom ->
                val updated = activeFloor.rooms.toMutableList()
                updated.add(newRoom)
                onUpdateFloorRooms(selectedFloorIndex, updated)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RoomConfigCard(
    room: RoomPlan,
    onDelete: () -> Unit
) {
    val roomColor = try {
        Color(android.graphics.Color.parseColor(room.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(roomColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = room.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "${room.type} • ${room.width.toInt()}m × ${room.length.toInt()}m (${(room.width * room.length).toInt()} m²)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Room",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomDialog(
    plotWidth: Float,
    plotLength: Float,
    onDismiss: () -> Unit,
    onConfirm: (RoomPlan) -> Unit
) {
    var roomName by remember { mutableStateOf("New Room") }
    var selectedType by remember { mutableStateOf("Bedroom") }
    var width by remember { mutableFloatStateOf(5f) }
    var length by remember { mutableFloatStateOf(5f) }
    var expandedDropdown by remember { mutableStateOf(false) }

    val roomTypes = listOf("Bedroom", "Living Room", "Kitchen", "Dining Room", "Bathroom", "Balcony", "Corridor")

    val defaultColor = when (selectedType) {
        "Bedroom" -> "#7dd3fc"
        "Kitchen" -> "#fdba74"
        "Bathroom" -> "#86efac"
        "Living Room" -> "#fde047"
        "Dining Room" -> "#f472b6"
        "Balcony" -> "#cbd5e1"
        else -> "#e2e8f0"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Room to Floor", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Room Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        roomTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Text(text = "Width: ${width.toInt()} meters", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                Slider(
                    value = width,
                    onValueChange = { width = it },
                    valueRange = 2f..12f,
                    steps = 9
                )

                Text(text = "Length: ${length.toInt()} meters", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                Slider(
                    value = length,
                    onValueChange = { length = it },
                    valueRange = 2f..12f,
                    steps = 9
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        RoomPlan(
                            name = roomName,
                            type = selectedType,
                            x = 0f,
                            z = 0f,
                            width = width,
                            length = length,
                            color = defaultColor
                        )
                    )
                }
            ) {
                Text("Add Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
