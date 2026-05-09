package com.mysterywalk.app.domain.usecase

import android.location.Location
import javax.inject.Inject

data class NavigationState(
    val distanceMeters: Int,
    val targetBearingDegrees: Float,   // 真北から目的地への絶対角度
    val relativeBearingDegrees: Float  // 現在のデバイスの向きから目的地への相対角度（0が正面、+が右、-が左）
)

class CalculateNavigationUseCase @Inject constructor() {

    /**
     * 現在地、デバイスの向き(方位角)、目的地からナビゲーション情報を計算する
     *
     * @param currentLat 現在地の緯度
     * @param currentLon 現在地の経度
     * @param currentAzimuth 現在デバイスが向いている方位角 (0-360)
     * @param targetLat 目的地の緯度
     * @param targetLon 目的地の経度
     * @return 目的地までの距離と相対角度
     */
    operator fun invoke(
        currentLat: Double,
        currentLon: Double,
        currentAzimuth: Float,
        targetLat: Double,
        targetLon: Double
    ): NavigationState {
        
        val results = FloatArray(3)
        Location.distanceBetween(currentLat, currentLon, targetLat, targetLon, results)
        
        val distanceMeters = results[0].toInt()
        var targetBearing = results[1] // -180 ~ 180
        if (targetBearing < 0) {
            targetBearing += 360f
        }
        
        // デバイスの向きから目的地方向への相対角度を計算 (-180 ~ 180)
        var relativeBearing = targetBearing - currentAzimuth
        if (relativeBearing > 180f) {
            relativeBearing -= 360f
        } else if (relativeBearing < -180f) {
            relativeBearing += 360f
        }

        return NavigationState(
            distanceMeters = distanceMeters,
            targetBearingDegrees = targetBearing,
            relativeBearingDegrees = relativeBearing
        )
    }
}
