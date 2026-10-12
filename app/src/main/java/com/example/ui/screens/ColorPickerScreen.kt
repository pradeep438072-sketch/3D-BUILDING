package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.BuildingPlan
import com.example.ui.AppScreen

@Composable
fun ColorPickerScreen(
    currentPlan: BuildingPlan,
    onUpdateRoomColor: (roomType: String, hexColor: String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val roomTypes = listOf(
        "Bedroom" to "Light Blue (#7dd3fc)",
        "Kitchen" to "Light Orange (#fdba74)",
        "Bathroom" to "Light Green (#86efac)",
        "Living Room" to "Light Yellow (#fde047)",
        "Dining Room" to "Light Pink (#f472b6)",
        "Balcony" to "Light Grey (#cbd5e1)",
        "Corridor" to "Warm Off-White (#e2e8f0)",
        "Staircase" to "Architectural Slate (#94a3b8)"
    )

    // Palette presets for easy selection
    val presetPalette = listOf(
        "#7dd3fc", "#38bdf8", "#0284c7", // blues
        "#fdba74", "#fb923c", "#ea580c", // oranges
        "#86efac", "#4ade80", "#16a34a", // greens
        "#fde047", "#facc15", "#eab308", // yellows
        "#f472b6", "#ec4899", "#db2777", // pinks
        "#cbd5e1", "#94a3b8", "#64748b", // greys
        "#c084fc", "#a855f7", "#7c3aed"  // purples
    )

    var activeCustomHex by remember { mutableStateOf("#7dd3fc") }
    var selectedTypeForCustom by remember { mutableStateOf("Bedroom") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("color_picker_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Room Color Customization",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Assign distinct identifiable colors to each room type in the 3D model",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Room color cards
        items(roomTypes) { (type, description) ->
            val currentColorHex = currentPlan.roomColors[type] ?: "#7dd3fc"
            val currentColor = try {
                Color(android.graphics.Color.parseColor(currentColorHex))
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("color_card_$type"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(currentColor)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = currentColorHex.uppercase(),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Swatches for quick selection
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetPalette) { hex ->
                            val swatchColor = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (e: Exception) {
                                Color.Gray
                            }
                            val isSelected = currentColorHex.equals(hex, ignoreCase = true)

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onUpdateRoomColor(type, hex)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (hex == "#fde047" || hex == "#facc15" || hex == "#86efac") Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom Hex Entry
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Custom Hex Color Code", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = activeCustomHex,
                            onValueChange = { activeCustomHex = it },
                            label = { Text("Hex Code (#RRGGBB)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (activeCustomHex.startsWith("#") && activeCustomHex.length == 7) {
                                    onUpdateRoomColor(selectedTypeForCustom, activeCustomHex)
                                }
                            }
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }

        // Return to 3D Viewer Button
        item {
            Button(
                onClick = { onNavigate(AppScreen.VISUALIZATION) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("return_to_3d_btn"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View in 3D Model", fontWeight = FontWeight.Bold)
            }
        }
    }
}
