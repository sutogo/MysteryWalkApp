package com.mysterywalk.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mysterywalk.app.ui.theme.*
import kotlin.math.sin

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
            .systemBarsPadding()
    ) {
        // 背景の微細な星屑エフェクト
        StarfieldBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── オフラインバナー ───
            AnimatedVisibility(
                visible = !isOnline,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3A0A15))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡ オフライン  ナビゲーション継続中",
                        color = NeonRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ─── メインコンテンツ ───
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (uiState) {
                    is NavUiState.Idle -> IdleContent(
                        onStartClick = { radius -> viewModel.findSpotAndStartNavigation(radius) },
                        onHistoryClick = onHistoryClick
                    )
                    is NavUiState.Loading -> LoadingContent()
                    is NavUiState.Navigating -> {
                        if (isArrived) {
                            LaunchedEffect(Unit) {
                                val dest = targetSpot
                                if (dest != null) {
                                    onArrived(
                                        navState?.distanceMeters ?: 0,
                                        dest.lat, dest.lon, dest.name, dest.category
                                    )
                                }
                            }
                            ArrivedContent(onStop = { viewModel.stopNavigation() })
                        } else {
                            NavigatingContent(
                                distance      = navState?.distanceMeters ?: 0,
                                bearing       = navState?.relativeBearingDegrees ?: 0f,
                                isReturnMode  = isReturnMode,
                                onReturn      = { viewModel.enableReturnMode() },
                                onStop        = { viewModel.stopNavigation() }
                            )
                        }
                    }
                    is NavUiState.Error -> ErrorContent(
                        message = (uiState as NavUiState.Error).message,
                        onBack  = { viewModel.stopNavigation() }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
//  サブコンポーネント
// ─────────────────────────────────────────────────────────

@Composable
private fun IdleContent(onStartClick: (radiusMeters: Int) -> Unit, onHistoryClick: () -> Unit) {
    // 選択中の距離（デフォルト: 1km）
    var selectedRadius by remember { mutableIntStateOf(1000) }

    val distanceOptions = listOf(
        Triple(1000, "1 km", "近場"),
        Triple(3000, "3 km", "中距離"),
        Triple(5000, "5 km", "遠出")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // タイトル
        Text(
            text = "MYSTERY WALK",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = NeonCyan,
            letterSpacing = 4.sp,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = NeonCyan.copy(alpha = 0.6f),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
        Text(
            text = "ブラインド・ナビゲーション",
            fontSize = 14.sp,
            color = OnDarkSecondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 静的レーダーロゴ
        IdleRadarIcon()

        Spacer(modifier = Modifier.height(28.dp))

        // ── 距離選択セグメント ──
        Text(
            text = "探索範囲",
            fontSize = 12.sp,
            color = OnDarkSecondary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            distanceOptions.forEach { (radius, label, sublabel) ->
                val isSelected = selectedRadius == radius
                val borderColor = if (isSelected) NeonCyan else OnDarkSecondary.copy(alpha = 0.3f)
                val bgColor    = if (isSelected) NeonCyanGlow else Color.Transparent
                val textColor  = if (isSelected) NeonCyan else OnDarkSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .then(
                            Modifier.clickable { selectedRadius = radius }
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                        Text(
                            text = sublabel,
                            fontSize = 10.sp,
                            color = textColor.copy(alpha = 0.7f),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // スタートボタン
        Button(
            onClick = { onStartClick(selectedRadius) },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(56.dp)
                .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyanGlow,
                contentColor   = NeonCyan
            )
        ) {
            Text(
                "ランダム探索スタート",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }

        // 履歴ボタン
        OutlinedButton(
            onClick = onHistoryClick,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                width = 1.dp,
                brush = SolidColor(OnDarkSecondary)
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDarkSecondary)
        ) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("履歴を見る", fontSize = 14.sp)
        }
    }
}


@Composable
private fun LoadingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            color = NeonCyan,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "目的地を探索中...",
            color = NeonCyan,
            fontSize = 16.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun NavigatingContent(
    distance: Int,
    bearing: Float,
    isReturnMode: Boolean,
    onReturn: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // モードラベル
        val modeLabel = if (isReturnMode) "出発地点まで" else "目的地まで"
        val modeColor = if (isReturnMode) ElectricPurple else NeonCyan

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(modeColor.copy(alpha = 0.15f))
                .border(1.dp, modeColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(modeLabel, color = modeColor, fontSize = 13.sp, letterSpacing = 1.sp)
        }

        // ネオン距離表示
        NeonDistanceText(distance = distance, accentColor = modeColor)

        Spacer(modifier = Modifier.height(32.dp))

        // レーダーコンパス
        RadarCompass(bearing = bearing, accentColor = modeColor)

        Spacer(modifier = Modifier.height(40.dp))

        // アクションボタン
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!isReturnMode) {
                OutlinedButton(
                    onClick = onReturn,
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(ElectricPurple)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricPurple)
                ) {
                    Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("帰還する", fontSize = 13.sp)
                }
            }
            OutlinedButton(
                onClick = onStop,
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = SolidColor(NeonRed.copy(alpha = 0.7f))
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed)
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("中止", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ArrivedContent(onStop: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "🎉  到 着 ！",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GoldBright,
            letterSpacing = 4.sp,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = GoldBright.copy(alpha = 0.7f),
                    offset = Offset(0f, 0f),
                    blurRadius = 24f
                )
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStop,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldGlow,
                contentColor   = GoldBright
            ),
            modifier = Modifier.border(1.dp, GoldBright.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
        ) {
            Text("リワードを確認する", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldBright)
        }
    }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("⚠  エラー", color = NeonRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, color = OnDarkSecondary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) { Text("戻る") }
    }
}

// ─────────────────────────────────────────────────────────
//  カスタムCanvas コンポーネント
// ─────────────────────────────────────────────────────────

/** ネオングロー付き距離テキスト */
@Composable
private fun NeonDistanceText(distance: Int, accentColor: Color) {
    val glowBrush = Brush.linearGradient(
        colors = listOf(accentColor, accentColor.copy(alpha = 0.6f))
    )
    Text(
        text = "$distance m",
        fontSize = 64.sp,
        fontWeight = FontWeight.ExtraBold,
        style = LocalTextStyle.current.copy(
            brush = glowBrush,
            shadow = Shadow(
                color = accentColor.copy(alpha = 0.8f),
                offset = Offset(0f, 0f),
                blurRadius = 30f
            )
        ),
        letterSpacing = (-1).sp
    )
}

/** レーダーコンパス（回転するスキャンライン + 光る矢印） */
@Composable
fun RadarCompass(bearing: Float, accentColor: Color = NeonCyan) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val scanAngle by infiniteTransition.animateFloat(
        initialValue   = 0f,
        targetValue    = 360f,
        animationSpec  = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanAngle"
    )

    Canvas(modifier = Modifier.size(240.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f - 8.dp.toPx()

        // ── 外周リング ──
        drawCircle(
            color  = accentColor.copy(alpha = 0.25f),
            radius = radius,
            center = center,
            style  = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color  = accentColor.copy(alpha = 0.08f),
            radius = radius * 0.67f,
            center = center,
            style  = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color  = accentColor.copy(alpha = 0.06f),
            radius = radius * 0.33f,
            center = center,
            style  = Stroke(width = 1.dp.toPx())
        )

        // ── 十字線 ──
        val crossColor = accentColor.copy(alpha = 0.15f)
        drawLine(crossColor, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 1.dp.toPx())
        drawLine(crossColor, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 1.dp.toPx())

        // ── スキャンライン ──
        rotate(scanAngle, center) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, accentColor.copy(alpha = 0.6f)),
                    start  = center,
                    end    = Offset(center.x, center.y - radius)
                ),
                start       = center,
                end         = Offset(center.x, center.y - radius),
                strokeWidth = 2.dp.toPx()
            )
        }
        // スキャン後光弾き（fading arc風にスキャン -30deg分を薄く描画）
        rotate(scanAngle - 30f, center) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, accentColor.copy(alpha = 0.15f)),
                    start  = center,
                    end    = Offset(center.x, center.y - radius)
                ),
                start       = center,
                end         = Offset(center.x, center.y - radius),
                strokeWidth = 2.dp.toPx()
            )
        }

        // ── 矢印（方角に合わせて回転）──
        rotate(bearing, center) {
            // 矢印グロー
            val arrowPath = Path().apply {
                moveTo(center.x, center.y - radius * 0.72f)      // 頂点
                lineTo(center.x + 22.dp.toPx(), center.y + 28.dp.toPx()) // 右下
                lineTo(center.x, center.y + 14.dp.toPx())              // くぼみ
                lineTo(center.x - 22.dp.toPx(), center.y + 28.dp.toPx()) // 左下
                close()
            }
            // グロー（外側の広い半透明）
            drawPath(
                path  = arrowPath,
                color = accentColor.copy(alpha = 0.25f)
            )
            // 本体（グラデーション）
            drawPath(
                path  = arrowPath,
                brush = Brush.verticalGradient(
                    colors = listOf(accentColor, accentColor.copy(alpha = 0.5f)),
                    startY = center.y - radius * 0.72f,
                    endY   = center.y + 28.dp.toPx()
                )
            )
        }

        // ── 中心ドット ──
        drawCircle(color = accentColor, radius = 5.dp.toPx(), center = center)
        drawCircle(color = accentColor.copy(alpha = 0.3f), radius = 10.dp.toPx(), center = center)
    }
}

/** Idle画面用の静的レーダーアイコン */
@Composable
private fun IdleRadarIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "idleradar")
    val pulse by infiniteTransition.animateFloat(
        initialValue  = 0.7f,
        targetValue   = 1.0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.size(120.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.width / 2f - 4.dp.toPx()

        drawCircle(NeonCyan.copy(alpha = 0.08f * pulse), radius = r * 1.2f, center = center)
        drawCircle(NeonCyan.copy(alpha = 0.15f), radius = r, center = center, style = Stroke(1.5.dp.toPx()))
        drawCircle(NeonCyan.copy(alpha = 0.08f), radius = r * 0.6f, center = center, style = Stroke(1.dp.toPx()))
        // 十字
        drawLine(NeonCyan.copy(0.2f), Offset(center.x, center.y - r), Offset(center.x, center.y + r), 1.dp.toPx())
        drawLine(NeonCyan.copy(0.2f), Offset(center.x - r, center.y), Offset(center.x + r, center.y), 1.dp.toPx())
        // 中心ドット
        drawCircle(NeonCyan.copy(alpha = pulse), radius = 6.dp.toPx(), center = center)
        drawCircle(NeonCyanGlow.copy(alpha = 0.4f * pulse), radius = 14.dp.toPx(), center = center)
    }
}

/** 背景の星屑エフェクト（軽量Canvas描画） */
@Composable
private fun StarfieldBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val twinkle by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    // 星の座標をランダムに固定（remember使用で再コンポーズ時に同じ位置を保持）
    val stars = remember {
        List(60) {
            Triple(
                (Math.random() * 1000).toFloat(),   // x (割合: 1000基準)
                (Math.random() * 1000).toFloat(),   // y
                (Math.random()).toFloat()            // phase offset
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { (xRatio, yRatio, phase) ->
            val x = xRatio / 1000f * size.width
            val y = yRatio / 1000f * size.height
            val alpha = ((sin(((twinkle + phase) * Math.PI * 2).toFloat()) + 1f) / 2f) * 0.5f + 0.1f
            drawCircle(
                color  = StarWhite.copy(alpha = alpha),
                radius = if (phase > 0.8f) 1.5.dp.toPx() else 0.8.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
