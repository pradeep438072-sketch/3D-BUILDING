package com.example.ui.screens

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildingPlan
import com.example.ui.AppScreen
import com.example.ui.components.BuildingWebView

@Composable
fun VisualizationScreen(
    buildingPlan: BuildingPlan,
    buildingJson: String,
    onSavePlan: () -> Unit,
    onExportReady: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var selectedRoomInfo by remember { mutableStateOf<Triple<String, String, Int>?>(null) }
    var isCutawayActive by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("visualization_screen")
    ) {
        // Interactive Three.js WebGL WebView
        BuildingWebView(
            buildingJson = buildingJson,
            modifier = Modifier.fillMaxSize(),
            onWebViewCreated = { webViewRef = it },
            onRoomSelected = { name, type, floor ->
                selectedRoomInfo = Triple(name, type, floor)
            },
            onExportReady = onExportReady
        )

        // Top Floating Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = buildingPlan.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${buildingPlan.floorCount} Floors • ${buildingPlan.type} • 3D Real-Time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            isCutawayActive = !isCutawayActive
                            webViewRef?.evaluateJavascript("window.toggleCutaway();", null)
                        },
                        modifier = Modifier.testTag("btn_cutaway_toggle")
                    ) {
                        Icon(
                            imageVector = if (isCutawayActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Cutaway Interior",
                            tint = if (isCutawayActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.COLOR_PICKER) },
                        modifier = Modifier.testTag("btn_color_picker_nav")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Color Picker",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onSavePlan,
                        modifier = Modifier.testTag("btn_save_plan")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Project",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.REPORT_DOWNLOAD) },
                        modifier = Modifier.testTag("btn_report_nav")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Project Report & Export",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Room Inspection Popup Card (When a user taps a room)
        selectedRoomInfo?.let { (roomName, roomType, floorIdx) ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
                    .testTag("room_inspection_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = roomName, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Type: $roomType • Floor ${floorIdx + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { selectedRoomInfo = null }) {
                        Text("✕", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Floating OBJ Export FAB
        FloatingActionButton(
            onClick = {
                webViewRef?.evaluateJavascript("window.triggerExportOBJ();", null)
                onNavigate(AppScreen.REPORT_DOWNLOAD)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_export_obj"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = "Export OBJ")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export 3D OBJ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
