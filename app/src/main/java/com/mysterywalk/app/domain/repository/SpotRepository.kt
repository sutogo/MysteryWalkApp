package com.mysterywalk.app.domain.repository

import com.mysterywalk.app.domain.model.Spot

interface SpotRepository {
    /**
     * 指定された半径内からランダムなスポットを取得する。
     * 該当スポットがない場合や通信エラー時はフォールバック座標を生成する。
     */
    suspend fun getRandomSpot(radiusMeters: Int, currentLat: Double, currentLon: Double): Spot
}
