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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BuildingPlan
import com.example.ui.AppScreen

@Composable
fun PlanInputScreen(
    currentPlan: BuildingPlan,
    onSaveMetadata: (
        name: String,
        type: String,
        plotWidth: Float,
        plotLength: Float,
        floorCount: Int,
        roofStyle: String,
        buildingStyle: String
    ) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(currentPlan) { mutableStateOf(currentPlan.name) }
    var selectedType by remember(currentPlan) { mutableStateOf(currentPlan.type) }
    var plotWidth by remember(currentPlan) { mutableFloatStateOf(currentPlan.plotWidth) }
    var plotLength by remember(currentPlan) { mutableFloatStateOf(currentPlan.plotLength) }
    var floorCount by remember(currentPlan) { mutableIntStateOf(currentPlan.floorCount) }
    var roofStyle by remember(currentPlan) { mutableStateOf(currentPlan.roofStyle) }
    var buildingStyle by remember(currentPlan) { mutableStateOf(currentPlan.buildingStyle) }

    val totalArea = plotWidth * plotLength

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("plan_input_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Building Plan & Requirements",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Configure building dimensions, floors, style, and architectural parameters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Building Name
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Project Name", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_building_name"),
                        placeholder = { Text("e.g. Skyline Residence") },
                        singleLine = true
                    )
                }
            }
        }

        // 2. Building Type
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Building Type", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BuildingTypeChip(
                            title = "Residential",
                            icon = Icons.Default.Home,
                            selected = selectedType == "Residential",
                            onClick = { selectedType = "Residential" },
                            modifier = Modifier.weight(1f)
                        )
                        BuildingTypeChip(
                            title = "Commercial",
                            icon = Icons.Default.Business,
                            selected = selectedType == "Commercial",
                            onClick = { selectedType = "Commercial" },
                            modifier = Modifier.weight(1f)
                        )
                        BuildingTypeChip(
                            title = "Apartment",
                            icon = Icons.Default.Apartment,
                            selected = selectedType == "Apartment",
                            onClick = { selectedType = "Apartment" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Plot Dimensions & Computed Area
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Plot Dimensions", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Total Area: ${totalArea.toInt()} m² (${(totalArea * 10.764).toInt()} sq ft)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Width: ${plotWidth.toInt()} meters", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = plotWidth,
                        onValueChange = { plotWidth = it },
                        valueRange = 10f..40f,
                        steps = 29,
                        modifier = Modifier.testTag("slider_plot_width"),
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Length: ${plotLength.toInt()} meters", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = plotLength,
                        onValueChange = { plotLength = it },
                        valueRange = 10f..40f,
                        steps = 29,
                        modifier = Modifier.testTag("slider_plot_length"),
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        // 4. Number of Floors
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Number of Floors", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = "$floorCount Floors",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (f in 1..5) {
                            FilterChip(
                                selected = floorCount == f,
                                onClick = { floorCount = f },
                                label = { Text(if (f == 1) "1 (Ground)" else "$f Floors") },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Roof Style & Architecture Style
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Roof Style", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = roofStyle == "Flat Terrace",
                            onClick = { roofStyle = "Flat Terrace" },
                            label = { Text("Flat Terrace (Parapet)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = roofStyle == "Sloped Pitch",
                            onClick = { roofStyle = "Sloped Pitch" },
                            label = { Text("Sloped Pitch (Gable)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Building Style", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (style in listOf("Modern Minimalist", "Glass Facade", "Classic Stone")) {
                            FilterChip(
                                selected = buildingStyle == style,
                                onClick = { buildingStyle = style },
                                label = { Text(style) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                }
            }
        }

        // Submit & Generate 3D Model Button
        item {
            Button(
                onClick = {
                    onSaveMetadata(
                        name.ifBlank { "Custom Building" },
                        selectedType,
                        plotWidth,
                        plotLength,
                        floorCount,
                        roofStyle,
                        buildingStyle
                    )
                    onNavigate(AppScreen.VISUALIZATION)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_3d_model_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate 3D Building Model", fontWeight = FontWeight.Bold)
            }
        }

        item {
            OutlinedButton(
                onClick = {
                    onSaveMetadata(
                        name.ifBlank { "Custom Building" },
                        selectedType,
                        plotWidth,
                        plotLength,
                        floorCount,
                        roofStyle,
                        buildingStyle
                    )
                    onNavigate(AppScreen.FLOOR_CONFIG)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("configure_rooms_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Customize Floor & Rooms First")
            }
        }
    }
}

@Composable
fun BuildingTypeChip(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
