package com.mysterywalk.app.domain.usecase

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.abs

class VibrationFeedbackUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var lastVibrateTime = 0L
    private val vibrateIntervalMs = 10000L // 10秒間隔

    /**
     * 目的地への相対角度に基づいて振動フィードバックを行う。
     * インターバル（10秒）内であれば何もしない。
     */
    @Suppress("DEPRECATION")
    operator fun invoke(relativeBearing: Float) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastVibrateTime < vibrateIntervalMs) {
            return // インターバル経過前は振動させない
        }
        lastVibrateTime = currentTime

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (!vibrator.hasVibrator()) return

        val effect = createVibrationEffect(relativeBearing)
        vibrator.vibrate(effect)
    }

    private fun createVibrationEffect(relativeBearing: Float): VibrationEffect {
        val absBearing = abs(relativeBearing)
        
        return when {
            absBearing <= 15f -> {
                // 正面: 短振動1回
                VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            absBearing <= 45f -> {
                // やや左右: 短振動2回
                val timings = longArrayOf(0, 100, 100, 100) // 待機, 振動, 待機, 振動
                val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                VibrationEffect.createWaveform(timings, amplitudes, -1)
            }
            absBearing <= 90f -> {
                // 左右: 長振動1回
                VibrationEffect.createOneShot(500L, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            else -> {
                // 後方: 連続短振動 (3回)
                val timings = longArrayOf(0, 100, 100, 100, 100, 100)
                val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                VibrationEffect.createWaveform(timings, amplitudes, -1)
            }
        }
    }
}
