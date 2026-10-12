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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildingPlan
import com.example.ui.AppScreen

@Composable
fun DashboardScreen(
    currentPlan: BuildingPlan,
    recentProjects: List<BuildingPlan>,
    onNavigate: (AppScreen) -> Unit,
    onSelectProject: (BuildingPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    // Compute breakdown statistics
    val allRooms = currentPlan.floors.flatMap { it.rooms }
    val totalRooms = allRooms.size
    val bedrooms = allRooms.count { it.type.contains("Bed", ignoreCase = true) }
    val kitchens = allRooms.count { it.type.contains("Kitchen", ignoreCase = true) }
    val bathrooms = allRooms.count { it.type.contains("Bath", ignoreCase = true) }
    val livingRooms = allRooms.count { it.type.contains("Living", ignoreCase = true) }
    val diningRooms = allRooms.count { it.type.contains("Dining", ignoreCase = true) }
    val balconies = allRooms.count { it.type.contains("Balcony", ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Project Header
        item {
            Column {
                Text(
                    text = "Building Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Overview of ${currentPlan.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Active Project Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_active_project_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentPlan.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Type: ${currentPlan.type} • Style: ${currentPlan.buildingStyle}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Domain,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(AppScreen.VISUALIZATION) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_view_3d_btn")
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("3D Preview")
                        }
                        OutlinedButton(
                            onClick = { onNavigate(AppScreen.REPORT_DOWNLOAD) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_view_report_btn")
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Report & Export")
                        }
                    }
                }
            }
        }

        // Metric Statistics Grid
        item {
            Text(
                text = "Key Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Floors",
                    value = "${currentPlan.floorCount}",
                    icon = Icons.Default.Layers,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Total Rooms",
                    value = "$totalRooms",
                    icon = Icons.Default.MeetingRoom,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Total Area",
                    value = "${currentPlan.totalArea.toInt()} m²",
                    icon = Icons.Default.SquareFoot,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Dimensions",
                    value = "${currentPlan.plotWidth.toInt()} × ${currentPlan.plotLength.toInt()} m",
                    icon = Icons.Default.Domain,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Floor Height",
                    value = "${currentPlan.floorHeight} m",
                    icon = Icons.Default.Layers,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Roof Style",
                    value = currentPlan.roofStyle.take(6),
                    icon = Icons.Default.Domain,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Room Distribution Breakdown
        item {
            Text(
                text = "Room Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RoomDistRow(label = "Bedrooms", count = bedrooms, icon = Icons.Default.Bed)
                    RoomDistRow(label = "Kitchens", count = kitchens, icon = Icons.Default.Countertops)
                    RoomDistRow(label = "Bathrooms", count = bathrooms, icon = Icons.Default.Bathtub)
                    RoomDistRow(label = "Living Rooms", count = livingRooms, icon = Icons.Default.MeetingRoom)
                    RoomDistRow(label = "Dining Rooms", count = diningRooms, icon = Icons.Default.MeetingRoom)
                    RoomDistRow(label = "Balconies", count = balconies, icon = Icons.Default.Domain)
                }
            }
        }

        // Recent Projects Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Projects",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "View All (${recentProjects.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onNavigate(AppScreen.SAVED_PROJECTS) }
                        .testTag("dashboard_view_all_projects")
                )
            }
        }

        if (recentProjects.isEmpty()) {
            item {
                Text(
                    text = "No saved projects yet. Save this design to access it later.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recentProjects.take(3)) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectProject(project) },
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${project.floorCount}F",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = project.name, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${project.type} • ${project.totalArea.toInt()} m² • ${project.floors.flatMap { it.rooms }.size} rooms",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RoomDistRow(label: String, count: Int, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
