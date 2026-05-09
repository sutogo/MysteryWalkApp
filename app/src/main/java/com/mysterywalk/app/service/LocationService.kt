package com.mysterywalk.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mysterywalk.app.data.location.LocationClient
import com.mysterywalk.app.data.sensor.CompassSensor
import com.mysterywalk.app.domain.manager.NavigationManager
import com.mysterywalk.app.domain.usecase.CalculateNavigationUseCase
import com.mysterywalk.app.domain.usecase.VibrationFeedbackUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject lateinit var locationClient: LocationClient
    @Inject lateinit var compassSensor: CompassSensor
    @Inject lateinit var calculateNavigationUseCase: CalculateNavigationUseCase
    @Inject lateinit var vibrationFeedbackUseCase: VibrationFeedbackUseCase
    @Inject lateinit var navigationManager: NavigationManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mystery Walk")
            .setContentText("ブラインド・ナビゲーション実行中")
            // デフォルトのアイコンで仮置き
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        // GPSは5秒間隔で取得
        val locationFlow = locationClient.getLocationUpdates(5000L).catch { e ->
            e.printStackTrace()
        }
        val azimuthFlow = compassSensor.getAzimuthUpdates().catch { e ->
            e.printStackTrace()
            emit(0f)
        }

        combine(locationFlow, azimuthFlow) { location, azimuth ->
            val target = navigationManager.targetSpot.value ?: return@combine
            
            val navState = calculateNavigationUseCase(
                currentLat = location.latitude,
                currentLon = location.longitude,
                currentAzimuth = azimuth,
                targetLat = target.lat,
                targetLon = target.lon
            )

            navigationManager.updateNavState(navState)
            
            // まだ到着していなければ振動フィードバックを実行
            if (!navigationManager.isArrived.value) {
                vibrationFeedbackUseCase(navState.relativeBearingDegrees)
            }
        }.launchIn(serviceScope)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Navigation Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_ID = "location_service_channel"
    }
}
