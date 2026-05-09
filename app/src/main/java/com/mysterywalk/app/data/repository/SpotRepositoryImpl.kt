package com.mysterywalk.app.data.repository

import com.mysterywalk.app.data.remote.OverpassApi
import com.mysterywalk.app.domain.model.Spot
import com.mysterywalk.app.domain.repository.SpotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class SpotRepositoryImpl @Inject constructor(
    private val api: OverpassApi
) : SpotRepository {

    override suspend fun getRandomSpot(radiusMeters: Int, currentLat: Double, currentLon: Double): Spot {
        return withContext(Dispatchers.IO) {
            try {
                // amenity (施設) と tourism (観光) のタグを持つ場所を検索
                val query = """
                    [out:json][timeout:15];
                    (
                      nwr["amenity"](around:$radiusMeters,$currentLat,$currentLon);
                      nwr["tourism"](around:$radiusMeters,$currentLat,$currentLon);
                    );
                    out geom;
                """.trimIndent()

                val response = api.getSpots(query)
                val elements = response.elements

                if (elements.isNullOrEmpty()) {
                    return@withContext generateFallbackSpot(radiusMeters, currentLat, currentLon)
                }

                // 見つかったリストからランダムに1つ選択
                val randomElement = elements.random()

                val lat = randomElement.lat ?: randomElement.center?.lat ?: currentLat
                val lon = randomElement.lon ?: randomElement.center?.lon ?: currentLon
                val name = randomElement.tags?.get("name") ?: "名もなき場所"
                val category = randomElement.tags?.get("amenity") ?: randomElement.tags?.get("tourism") ?: "スポット"

                Spot(
                    id = randomElement.id,
                    lat = lat,
                    lon = lon,
                    name = name,
                    category = category
                )
            } catch (e: Exception) {
                // 通信エラー等が発生した場合もフォールバック
                generateFallbackSpot(radiusMeters, currentLat, currentLon)
            }
        }
    }

    /**
     * スポットが見つからなかった場合、指定半径内のランダムな座標を生成する
     */
    private fun generateFallbackSpot(radiusMeters: Int, currentLat: Double, currentLon: Double): Spot {
        // 約111kmを1度とする近似計算
        val radiusInDegrees = radiusMeters / 111000.0
        val u = Random.nextDouble()
        val v = Random.nextDouble()
        val w = radiusInDegrees * sqrt(u)
        val t = 2 * PI * v
        val x = w * cos(t)
        val y = w * sin(t)

        val newLon = x / cos(Math.toRadians(currentLat))

        return Spot(
            id = Random.nextLong(),
            lat = currentLat + y,
            lon = currentLon + newLon,
            name = "秘密の場所",
            category = "未知"
        )
    }
}
