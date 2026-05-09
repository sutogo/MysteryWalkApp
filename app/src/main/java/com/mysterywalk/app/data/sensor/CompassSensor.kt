package com.mysterywalk.app.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface CompassSensor {
    /**
     * デバイスが向いている方位角（真北を0度とした時計回りの角度 0〜360）をFlowで返す
     */
    fun getAzimuthUpdates(): Flow<Float>
}

class DefaultCompassSensor @Inject constructor(
    context: Context
) : CompassSensor {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // TYPE_ROTATION_VECTORは地磁気と加速度を組み合わせた精度の高い回転ベクトル
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    override fun getAzimuthUpdates(): Flow<Float> = callbackFlow {
        if (rotationSensor == null) {
            // エミュレータ等でセンサーがない場合はクラッシュさせず、常に0度を返す
            trySend(0f)
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    
                    // orientationAngles[0] は方位角 (ラジアン: -π 〜 π)
                    var azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    if (azimuthDegrees < 0) {
                        azimuthDegrees += 360f
                    }
                    trySend(azimuthDegrees)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // 初期値として0度を送信（combineがブロックされるのを防ぐため）
        trySend(0f)

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
