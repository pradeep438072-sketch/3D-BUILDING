package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildingPlan
import com.example.ui.AppScreen

@Composable
fun ReportDownloadScreen(
    currentPlan: BuildingPlan,
    exportedObjContent: String?,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copiedReport by remember { mutableStateOf(false) }
    var copiedObj by remember { mutableStateOf(false) }

    val allRooms = currentPlan.floors.flatMap { it.rooms }
    val totalRooms = allRooms.size
    val totalFloorArea = currentPlan.totalArea * currentPlan.floorCount

    // Cost estimations (Estimated industry standard rates: $120/sq ft or $1300/sq m)
    val estimatedStructureCost = (totalFloorArea * 650).toLong()
    val estimatedFinishingCost = (totalFloorArea * 450).toLong()
    val estimatedInteriorCost = (totalFloorArea * 250).toLong()
    val totalEstimatedCost = estimatedStructureCost + estimatedFinishingCost + estimatedInteriorCost

    val reportText = buildString {
        appendLine("==================================================")
        appendLine("3D BUILDING VISUALIZATION SYSTEM - ARCHITECTURAL REPORT")
        appendLine("==================================================")
        appendLine("Project Name: ${currentPlan.name}")
        appendLine("Building Type: ${currentPlan.type}")
        appendLine("Building Style: ${currentPlan.buildingStyle}")
        appendLine("Plot Dimensions: ${currentPlan.plotWidth}m x ${currentPlan.plotLength}m")
        appendLine("Footprint Area: ${currentPlan.totalArea.toInt()} sq m (${(currentPlan.totalArea * 10.764).toInt()} sq ft)")
        appendLine("Number of Floors: ${currentPlan.floorCount}")
        appendLine("Floor Height: ${currentPlan.floorHeight}m")
        appendLine("Total Usable Built Area: ${totalFloorArea.toInt()} sq m")
        appendLine("Total Rooms: $totalRooms")
        appendLine("Roof Style: ${currentPlan.roofStyle}")
        appendLine("--------------------------------------------------")
        appendLine("FLOOR-BY-FLOOR SCHEDULE:")
        currentPlan.floors.forEachIndexed { fIdx, floor ->
            appendLine("[Floor ${fIdx + 1} - ${floor.name}]")
            floor.rooms.forEachIndexed { rIdx, room ->
                appendLine("  ${rIdx + 1}. ${room.name} (${room.type}): ${room.width}m x ${room.length}m = ${(room.width * room.length).toInt()} sq m | Color: ${room.color}")
            }
        }
        appendLine("--------------------------------------------------")
        appendLine("ESTIMATED BUDGET BREAKDOWN:")
        appendLine("  Structure & Civil: $${"%,d".format(estimatedStructureCost)}")
        appendLine("  Finishing & Masonry: $${"%,d".format(estimatedFinishingCost)}")
        appendLine("  Interior & Fitments: $${"%,d".format(estimatedInteriorCost)}")
        appendLine("  Total Estimated: $${"%,d".format(totalEstimatedCost)}")
        appendLine("==================================================")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("report_download_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Project Report & 3D Export",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Download architectural summaries, dimension schedules and export 3D OBJ models",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Architectural Specifications Summary",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = currentPlan.type,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    SpecRow("Plot Footprint", "${currentPlan.plotWidth.toInt()}m × ${currentPlan.plotLength.toInt()}m (${currentPlan.totalArea.toInt()} m²)")
                    SpecRow("Total Built Area", "${totalFloorArea.toInt()} m² (${(totalFloorArea * 10.764).toInt()} sq ft)")
                    SpecRow("Number of Floors", "${currentPlan.floorCount} Levels")
                    SpecRow("Total Rooms", "$totalRooms Rooms")
                    SpecRow("Roof Structure", currentPlan.roofStyle)
                    SpecRow("Est. Total Budget", "$${"%,d".format(totalEstimatedCost)}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Architectural Report", reportText)
                                clipboard.setPrimaryClip(clip)
                                copiedReport = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_copy_report")
                        ) {
                            Icon(
                                if (copiedReport) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (copiedReport) "Copied!" else "Copy Report")
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Architectural Report")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_share_report")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share / PDF")
                        }
                    }
                }
            }
        }

        // 3D OBJ Export Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column {
                            Text(
                                text = "3D Model Export (.OBJ Format)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Compatible with Blender, AutoCAD, Revit, 3ds Max",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (exportedObjContent != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = exportedObjContent.lines().take(8).joinToString("\n") + "\n...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("3D OBJ Model", exportedObjContent)
                                    clipboard.setPrimaryClip(clip)
                                    copiedObj = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_copy_obj")
                            ) {
                                Icon(
                                    if (copiedObj) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (copiedObj) "OBJ Copied!" else "Copy OBJ")
                            }

                            OutlinedButton(
                                onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, exportedObjContent)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Export 3D Model OBJ")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download OBJ")
                            }
                        }
                    } else {
                        Text(
                            text = "To generate the OBJ model, tap 'Export 3D OBJ' inside the 3D Viewer or click below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onNavigate(AppScreen.VISUALIZATION) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open 3D Viewer to Generate OBJ")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
