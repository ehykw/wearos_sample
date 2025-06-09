package com.example.sotuken

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sotuken.ui.theme.Mobile_sensor_receiverTheme
import org.json.JSONObject

// 受信したデータを保持するためのグローバルなMutableState (簡易的な例)
// 本番アプリではViewModelやStateFlowなどを使用してください
var receivedHeartRate: MutableState<String> = mutableStateOf("N/A")
var receivedAccelerometer: MutableState<String> = mutableStateOf("N/A")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mobile_sensor_receiverTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SensorDataDisplay(
                        heartRate = receivedHeartRate.value,
                        accelerometer = receivedAccelerometer.value
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "MobileSensorReceiver"

        // DataLayerListenerServiceからデータを更新するための関数
        fun updateSensorData(heartRate: String, accelerometer: String) {
            receivedHeartRate.value = heartRate
            receivedAccelerometer.value = accelerometer
            Log.d(TAG, "UI更新: 心拍数=$heartRate, 加速度計=$accelerometer")
        }
    }
}

@Composable
fun SensorDataDisplay(heartRate: String, accelerometer: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Wear OSからのセンサーデータ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "心拍数: $heartRate bpm",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "加速度計: $accelerometer",
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Mobile_sensor_receiverTheme {
        SensorDataDisplay(heartRate = "80.0", accelerometer = "X:0.01, Y:0.02, Z:9.80 (Mag:9.80)")
    }
}