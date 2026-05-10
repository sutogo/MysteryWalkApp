package com.mysterywalk.app.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.mysterywalk.app.data.local.HistoryEntity
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyList by viewModel.historyList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "戻る")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("履歴＆振り返り", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Map View (osmdroid)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            OsmMapView(historyList = historyList)
        }

        // History List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (historyList.isEmpty()) {
                Text(
                    "まだ履歴がありません。",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList) { history ->
                        HistoryCard(history)
                    }
                }
            }
        }
    }
}

@Composable
fun OsmMapView(historyList: List<HistoryEntity>) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(12.0)
                
                // Set initial center (e.g., Tokyo or the last visited spot)
                if (historyList.isNotEmpty()) {
                    val lastSpot = historyList.first() // Assuming ordered by timestamp DESC
                    controller.setCenter(GeoPoint(lastSpot.lat, lastSpot.lon))
                } else {
                    controller.setCenter(GeoPoint(35.6895, 139.6917)) // Tokyo as default fallback
                }
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            historyList.forEach { history ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(history.lat, history.lon)
                marker.title = history.name
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                val dateStr = sdf.format(Date(history.timestamp))
                marker.snippet = "$dateStr - ${history.distanceMeters}m"
                mapView.overlays.add(marker)
            }
            
            if (historyList.isNotEmpty()) {
                val lastSpot = historyList.first()
                mapView.controller.animateTo(GeoPoint(lastSpot.lat, lastSpot.lon))
            }
            mapView.invalidate()
        }
    )
}

@Composable
fun HistoryCard(history: HistoryEntity) {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(history.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = dateStr, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = history.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "カテゴリー: ${history.category}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "歩いた距離: ${history.distanceMeters}m", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
