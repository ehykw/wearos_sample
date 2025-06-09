package com.example.sotuken.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts // Import this
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat // Import this
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    private lateinit var messageClient: MessageClient

    private var currentHeartRate by mutableStateOf("N/A")
    private var currentAccelerometer by mutableStateOf("N/A")

    private val handler = Handler(Looper.getMainLooper())
    private val sendDataRunnable = object : Runnable {
        override fun run() {
            sendSensorData()
            handler.postDelayed(this, 1000) // 1秒ごとにデータを送信
        }
    }

    // 1. Declare the ActivityResultLauncher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "BODY_SENSORS permission granted.")
                // Permission is granted. Continue the action or workflow in your app.
                initializeSensorsAndDataSending()
            } else {
                Log.w(TAG, "BODY_SENSORS permission denied. Heart rate data cannot be obtained.")
                Toast.makeText(this, "BODY_SENSORS permission is required to obtain heart rate data.", Toast.LENGTH_LONG).show()
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp(
                heartRate = currentHeartRate,
                accelerometer = currentAccelerometer
            )
        }

        // 2. Check and request permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.d(TAG, "BODY_SENSORS permission already granted.")
                initializeSensorsAndDataSending()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.BODY_SENSORS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                // For this example, we'll just log and request.
                Log.i(TAG, "Showing rationale for BODY_SENSORS permission.")
                requestPermissionLauncher.launch(Manifest.permission.BODY_SENSORS)
            }
            else -> {
                // Directly ask for the permission.
                Log.d(TAG, "Requesting BODY_SENSORS permission.")
                requestPermissionLauncher.launch(Manifest.permission.BODY_SENSORS)
            }
        }
    }

    private fun initializeSensorsAndDataSending() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        messageClient = Wearable.getMessageClient(this)

        if (heartRateSensor == null) {
            Log.e(TAG, "Heart rate sensor not found.")
            Toast.makeText(this, "Heart rate sensor not found.", Toast.LENGTH_LONG).show()
        }
        if (accelerometerSensor == null) {
            Log.e(TAG, "Accelerometer sensor not found.")
            Toast.makeText(this, "Accelerometer sensor not found.", Toast.LENGTH_LONG).show()
        }

        // Start listening to sensors and sending data only if permission is granted
        // and sensors are available.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            registerSensorListeners()
            handler.post(sendDataRunnable) // Start data sending
        }
    }

    private fun registerSensorListeners() {
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure permission is still granted before registering listeners,
        // as the user could have revoked it from settings.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            registerSensorListeners()
            handler.post(sendDataRunnable) // Resume data sending if it was paused
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(sendDataRunnable) // Stop data sending
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_HEART_RATE -> {
                    val hr = it.values[0]
                    currentHeartRate = String.format("%.1f", hr)
                    Log.d(TAG, "Heart rate: $hr bpm")
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    val magnitude = sqrt(x * x + y * y + z * z)
                    currentAccelerometer = String.format("X:%.2f, Y:%.2f, Z:%.2f (Mag:%.2f)", x, y, z, magnitude)
                    Log.d(TAG, "Accelerometer: X=$x, Y=$y, Z=$z (Mag: $magnitude)")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: ${sensor?.name}, accuracy: $accuracy")
    }

    private fun sendSensorData() {
        val jsonObject = JSONObject().apply {
            put("heart_rate", currentHeartRate)
            put("accelerometer", currentAccelerometer)
        }
        val data = jsonObject.toString().toByteArray()
        val path = "/sensor_data"

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isEmpty()) {
                Log.w(TAG, "No connected nodes found.")
                return@addOnSuccessListener
            }
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, path, data)
                    .addOnSuccessListener {
                        Log.d(TAG, "Data sent to smartphone: ${node.displayName}, Data: $jsonObject")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to send data: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get connected nodes: ${e.message}")
        }
    }

    // REMOVE this method:
    /*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BODY_SENSORS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "BODY_SENSORS パーミッションが許可されました。")
            } else {
                Log.w(TAG, "BODY_SENSORS パーミッションが拒否されました。心拍数データは取得できません。")
                Toast.makeText(this, "心拍数データ取得にはBODY_SENSORSパーミッションが必要です。", Toast.LENGTH_LONG).show()
            }
        }
    }
    */

    companion object {
        private const val TAG = "WearableSensorSender"
        // private const val REQUEST_BODY_SENSORS = 1 // No longer needed
    }
}

@Composable
fun WearApp(heartRate: String, accelerometer: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = "Heart rate: $heartRate bpm"
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = "Accelerometer: $accelerometer"
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            text = "Sending data to smartphone..."
        )
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
fun WearAppPreview() {
    WearApp(heartRate = "75.5", accelerometer = "X:0.1, Y:0.2, Z:9.8 (Mag:9.82)")
}