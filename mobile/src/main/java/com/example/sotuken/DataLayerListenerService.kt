package com.example.sotuken

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.json.JSONObject

class DataLayerListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        if (messageEvent.path == "/sensor_data") {
            val receivedData = String(messageEvent.data)
            Log.d(TAG, "ウォッチからデータを受信しました: $receivedData")

            try {
                val jsonObject = JSONObject(receivedData)
                val heartRate = jsonObject.optString("heart_rate", "N/A")
                val accelerometer = jsonObject.optString("accelerometer", "N/A")

                // UIを更新 (メインスレッドで実行)
                // この例ではグローバルなMutableStateを直接更新していますが、
                // 実際にはもっと堅牢なメカニズム (例: LocalBroadcastManager, ViewModel) を使用してください。
                runOnUiThread {
                    MainActivity.updateSensorData(heartRate, accelerometer)
                }
            } catch (e: Exception) {
                Log.e(TAG, "JSON解析エラー: ${e.message}")
            }
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        mainLooper.queue.addIdleHandler {
            action()
            false // 一度実行したらIdleHandlerを削除
        }
    }

    companion object {
        private const val TAG = "DataLayerListener"
    }
}