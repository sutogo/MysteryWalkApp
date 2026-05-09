package com.mysterywalk.app.data.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    /**
     * 指定された間隔で位置情報を継続的に取得するFlowを返す
     */
    fun getLocationUpdates(intervalMs: Long): Flow<Location>

    class LocationException(message: String): Exception(message)
}
