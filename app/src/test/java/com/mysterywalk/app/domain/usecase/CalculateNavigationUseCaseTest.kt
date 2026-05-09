package com.mysterywalk.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class CalculateNavigationUseCaseTest {

    private lateinit var useCase: CalculateNavigationUseCase

    @Before
    fun setup() {
        useCase = CalculateNavigationUseCase()
    }

    @Test
    fun `invoke calculates distance and bearing correctly`() {
        // 東京駅
        val tokyoLat = 35.681236
        val tokyoLon = 139.767125
        
        // 有楽町駅 (東京駅から見てほぼ南)
        val yurakuchoLat = 35.675069
        val yurakuchoLon = 139.763328

        // 現在デバイスは真東 (90度) を向いているとする
        val currentAzimuth = 90f

        val result = useCase(tokyoLat, tokyoLon, currentAzimuth, yurakuchoLat, yurakuchoLon)

        // 東京〜有楽町は約700m
        assertTrue(result.distanceMeters > 500 && result.distanceMeters < 1000)

        // 東京から有楽町は南南西なので、絶対角度(targetBearing)は 200度前後のはず
        assertTrue(result.targetBearingDegrees > 180 && result.targetBearingDegrees < 220)

        // デバイスが真東(90度)を向いているなら、相対角度は 200 - 90 = 110度前後のはず (右後方)
        assertTrue(result.relativeBearingDegrees > 90 && result.relativeBearingDegrees < 130)
    }

    @Test
    fun `relative bearing is within -180 to 180`() {
        val currentLat = 0.0
        val currentLon = 0.0
        val targetLat = 1.0
        val targetLon = 0.0 // 真北 (0度)

        // デバイスが真南(180度)を向いている場合
        val currentAzimuth = 180f

        val result = useCase(currentLat, currentLon, currentAzimuth, targetLat, targetLon)

        // 真北(0) - 真南(180) = -180 もしくは +180
        assertEquals(180f, abs(result.relativeBearingDegrees), 0.1f)
    }
}
