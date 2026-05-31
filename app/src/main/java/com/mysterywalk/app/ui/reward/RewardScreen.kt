package com.mysterywalk.app.ui.reward

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.mysterywalk.app.ui.theme.*
import kotlin.math.sin

@Composable
fun RewardScreen(
    distanceMeters: Int,
    lat: Double,
    lon: Double,
    name: String?,
    category: String?,
    onFinishClick: () -> Unit,
    onReturnClick: () -> Unit,
    viewModel: RewardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReward(distanceMeters, lat, lon, name, category)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
            .systemBarsPadding()
    ) {
        // ── 背景の流れ星エフェクト ──
        ShootingStarsBackground()

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp)
            }
        } else {
            RewardContent(
                uiState       = uiState,
                onFinishClick = onFinishClick,
                onReturnClick = onReturnClick
            )

            // Confetti Animation（最前面に重ねる）
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("confetti.json"))
            val progress by animateLottieCompositionAsState(composition, iterations = 1)
            LottieAnimation(
                composition = composition,
                progress    = { progress },
                modifier    = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RewardContent(
    uiState: RewardUiState,
    onFinishClick: () -> Unit,
    onReturnClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── タイトル ──
        Text(
            text = "MISSION COMPLETE",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = NeonCyan,
            letterSpacing = 4.sp,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color     = NeonCyan.copy(alpha = 0.7f),
                    offset    = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
        Text(
            text = "目的地に到着しました",
            fontSize = 14.sp,
            color = OnDarkSecondary,
            letterSpacing = 1.sp
        )

        // ── ロケーション情報カード（グラスモーフィズム）──
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.imageUrl != null) {
                    AsyncImage(
                        model            = uiState.imageUrl,
                        contentDescription = "Destination Image",
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale     = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(NeonCyanGlow, Color.Transparent)
                                )
                            )
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector      = Icons.Default.Place,
                            contentDescription = null,
                            modifier         = Modifier.size(64.dp),
                            tint             = NeonCyan
                        )
                    }
                }

                Column(
                    modifier            = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text       = uiState.destinationName ?: "Unknown Spot",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = OnDark,
                        textAlign  = TextAlign.Center
                    )
                    if (uiState.destinationCategory != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = uiState.destinationCategory,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonCyanDim
                        )
                    }
                }
            }
        }

        // ── ステータスバー（距離 & XP）──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBadge(
                label       = "歩いた距離",
                value       = "${uiState.distanceWalkedMeters}m",
                accentColor = NeonCyan
            )
            StatBadge(
                label       = "獲得 XP",
                value       = "+${uiState.earnedXp}",
                accentColor = GoldBright,
                isGold      = true
            )
        }

        // ── レベルアップ表示 ──
        AnimatedVisibility(
            visible = uiState.isLevelUp,
            enter   = fadeIn(tween(800)) + scaleIn(tween(800), initialScale = 0.6f),
            exit    = fadeOut()
        ) {
            LevelUpBanner(level = uiState.newLevel)
        }

        if (!uiState.isLevelUp) {
            Text(
                text  = "Lv.${uiState.newLevel}  ·  Total ${uiState.totalXp} XP",
                style = MaterialTheme.typography.bodySmall,
                color = OnDarkSecondary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── アクションボタン ──
        Button(
            onClick  = onReturnClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, ElectricPurple.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
            shape    = RoundedCornerShape(28.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = ElectricPurple.copy(alpha = 0.2f),
                contentColor   = ElectricPurple
            )
        ) {
            Text("出発地点へ帰る", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ElectricPurple)
        }

        OutlinedButton(
            onClick  = onFinishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape    = RoundedCornerShape(26.dp),
            border   = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = SolidColor(OnDarkSecondary.copy(alpha = 0.5f))
            ),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = OnDarkSecondary)
        ) {
            Text("ホームに戻る", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─────────────────────────────────────────────────────────
//  再利用可能コンポーネント
// ─────────────────────────────────────────────────────────

/** グラスモーフィズムカード */
@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightBlue.copy(alpha = 0.7f))
            .border(
                width  = 1.dp,
                brush  = Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.4f),
                        ElectricPurple.copy(alpha = 0.2f),
                        NeonCyan.copy(alpha = 0.1f)
                    )
                ),
                shape  = RoundedCornerShape(16.dp)
            ),
        content = content
    )
}

/** ステータスバッジ（距離・XP表示用） */
@Composable
private fun StatBadge(
    label: String,
    value: String,
    accentColor: Color,
    isGold: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(label, fontSize = 12.sp, color = OnDarkSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        if (isGold) {
            Text(
                text  = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                style = LocalTextStyle.current.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(GoldBright, GoldDim)
                    ),
                    shadow = Shadow(GoldBright.copy(alpha = 0.6f), blurRadius = 16f)
                )
            )
        } else {
            Text(
                text  = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
    }
}

/** レベルアップバナー */
@Composable
private fun LevelUpBanner(level: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GoldGlow)
            .border(1.dp, GoldBright.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = GoldBright,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text  = "LEVEL UP!  Lv.$level",
            style = LocalTextStyle.current.copy(
                fontSize   = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                brush      = Brush.linearGradient(listOf(GoldBright, Color(0xFFFFEA70))),
                shadow     = Shadow(GoldBright.copy(alpha = 0.8f), blurRadius = 20f)
            )
        )
    }
}

/** 流れ星背景 Canvas */
@Composable
private fun ShootingStarsBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val phase by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val stars = remember {
        List(40) {
            Triple(
                (Math.random() * 1000).toFloat(),
                (Math.random() * 1000).toFloat(),
                (Math.random()).toFloat()
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { (xR, yR, offset) ->
            val x = xR / 1000f * size.width
            val y = yR / 1000f * size.height
            val alpha = ((sin(((phase + offset) * Math.PI * 2).toFloat()) + 1f) / 2f) * 0.4f + 0.05f
            val isLarge = offset > 0.75f
            drawCircle(
                color  = if (isLarge) GoldBright.copy(alpha = alpha * 0.5f) else StarWhite.copy(alpha = alpha),
                radius = if (isLarge) 1.8.dp.toPx() else 0.9.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
