package com.mysterywalk.app.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.mysterywalk.app.data.local.HistoryEntity
import com.mysterywalk.app.ui.theme.*
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
    ) {
        // 微細な背景グラデーション
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NeonCyanGlow.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // ── カスタムトップバー ──
            NeonTopBar(onBackClick = onBackClick)

            // ── マップ（ダークフィルター付き）──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                OsmMapView(historyList = historyList)

                // マップ上部グラデーションオーバーレイ（自然な境界）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(Brush.verticalGradient(listOf(DeepSlate, Color.Transparent)))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── 履歴リスト ──
            if (historyList.isEmpty()) {
                EmptyHistoryPlaceholder()
            } else {
                LazyColumn(
                    modifier        = Modifier
                        .fillMaxWidth()
                        .weight(0.55f),
                    contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyList) { history ->
                        HistoryCard(history)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
//  サブコンポーネント
// ─────────────────────────────────────────────────────────

@Composable
private fun NeonTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector      = Icons.Default.ArrowBack,
                contentDescription = "戻る",
                tint             = NeonCyan
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector      = Icons.Default.History,
            contentDescription = null,
            tint             = NeonCyan,
            modifier         = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text       = "履歴  &  振り返り",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = OnDark,
            letterSpacing = 1.sp
        )
    }

    // タイトル下の区切り線（シアングロー）
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        NeonCyan.copy(alpha = 0.4f),
                        NeonCyan.copy(alpha = 0.6f),
                        NeonCyan.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun EmptyHistoryPlaceholder() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector      = Icons.Default.Place,
            contentDescription = null,
            tint             = NeonCyan.copy(alpha = 0.3f),
            modifier         = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text  = "まだ履歴がありません",
            color = OnDarkSecondary,
            fontSize = 16.sp
        )
        Text(
            text  = "最初の探索に出発しましょう！",
            color = OnDarkSecondary.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
    }
}

@Composable
fun OsmMapView(historyList: List<HistoryEntity>) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory  = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(12.0)
                if (historyList.isNotEmpty()) {
                    val last = historyList.first()
                    controller.setCenter(GeoPoint(last.lat, last.lon))
                } else {
                    controller.setCenter(GeoPoint(35.6895, 139.6917)) // 東京
                }
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            historyList.forEach { history ->
                val marker    = Marker(mapView)
                marker.position = GeoPoint(history.lat, history.lon)
                marker.title  = history.name
                val sdf       = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                marker.snippet = "${sdf.format(Date(history.timestamp))} · ${history.distanceMeters}m"
                mapView.overlays.add(marker)
            }
            if (historyList.isNotEmpty()) {
                val last = historyList.first()
                mapView.controller.animateTo(GeoPoint(last.lat, last.lon))
            }
            mapView.invalidate()
        }
    )
}

@Composable
fun HistoryCard(history: HistoryEntity) {
    val sdf     = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(history.timestamp))

    // グラスモーフィズムカード
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MidnightBlue.copy(alpha = 0.75f))
            .border(
                width  = 1.dp,
                brush  = Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.3f),
                        ElectricPurple.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                shape  = RoundedCornerShape(14.dp)
            )
    ) {
        // 左側アクセントライン
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(80.dp)  // カード内容の高さに合わせて調整
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(listOf(NeonCyan, ElectricPurple.copy(alpha = 0.5f)))
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
        ) {
            // 日付
            Text(
                text  = dateStr,
                fontSize = 11.sp,
                color = OnDarkSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // スポット名
            Text(
                text       = history.name,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = OnDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            // タグ行（カテゴリ + 距離）
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeonTag(text = history.category, color = NeonCyan)
                NeonTag(text = "${history.distanceMeters}m", color = GoldDim)
            }
        }
    }
}

/** 小さなネオンタグバッジ */
@Composable
private fun NeonTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.1f))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
