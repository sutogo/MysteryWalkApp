package com.mysterywalk.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NavScreen(
    onArrived: (distance: Int, lat: Double, lon: Double, name: String?, category: String?) -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navState by viewModel.navigationManager.navState.collectAsState()
    val isArrived by viewModel.navigationManager.isArrived.collectAsState()
    val targetSpot by viewModel.navigationManager.targetSpot.collectAsState()
    val isReturnMode by viewModel.navigationManager.isReturnMode.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    Surface(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isOnline,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "オフラインですが、ナビゲーションは継続可能です",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            when (uiState) {
                is NavUiState.Idle -> {
                    Text("ブラインド・ナビゲーション", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { viewModel.findSpotAndStartNavigation(1000) }) {
                        Text("1km圏内のランダムな場所へ出発")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onHistoryClick) {
                        Text("履歴＆振り返り")
                    }
                }
                is NavUiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("目的地を探索中...")
                }
                is NavUiState.Navigating -> {
                    if (isArrived) {
                        LaunchedEffect(Unit) {
                            val dest = targetSpot
                            if (dest != null) {
                                onArrived(navState?.distanceMeters ?: 0, dest.lat, dest.lon, dest.name, dest.category)
                            }
                        }
                        Text("🎉 到着しました！", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Green)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { viewModel.stopNavigation() }) {
                            Text("終了する")
                        }
                    } else {
                        val distance = navState?.distanceMeters ?: 0
                        val relativeBearing = navState?.relativeBearingDegrees ?: 0f

                        val titleText = if (isReturnMode) "出発地点まで" else "目的地まで"
                        Text(titleText, fontSize = 20.sp)
                        Text("$distance m", fontSize = 48.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(64.dp))
                        
                        CompassArrow(bearing = relativeBearing)

                        Spacer(modifier = Modifier.height(64.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (!isReturnMode) {
                                Button(
                                    onClick = { viewModel.enableReturnMode() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("中断して帰還する")
                                }
                            }
                            Button(onClick = { viewModel.stopNavigation() }) {
                                Text("ナビゲーションを中止")
                            }
                        }
                    }
                }
                is NavUiState.Error -> {
                    Text("エラー: ${(uiState as NavUiState.Error).message}", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.stopNavigation() }) {
                        Text("戻る")
                    }
                }
            }
        }
    }
}
}

@Composable
fun CompassArrow(bearing: Float) {
    val arrowColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        
        rotate(bearing, center) {
            val path = Path().apply {
                moveTo(center.x, center.y - 80.dp.toPx()) // 頂点
                lineTo(center.x + 40.dp.toPx(), center.y + 60.dp.toPx()) // 右下
                lineTo(center.x, center.y + 40.dp.toPx()) // 下中央のくぼみ
                lineTo(center.x - 40.dp.toPx(), center.y + 60.dp.toPx()) // 左下
                close()
            }
            drawPath(path, color = arrowColor)
        }
    }
}
