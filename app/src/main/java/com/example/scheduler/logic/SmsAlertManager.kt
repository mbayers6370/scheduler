package com.example.scheduler.logic

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.scheduler.data.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Handles background monitoring for upcoming events and triggers simulated SMS alerts.
 */
class SmsAlertManager(
    private val application: Application,
    private val scope: CoroutineScope,
    private val eventsFlow: StateFlow<List<Event>>,
    private val alertsEnabledFlow: StateFlow<Boolean>
) {

    /**
     * Starts the monitoring loop.
     */
    fun startMonitoring() {
        scope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val threshold = now + (30 * 60 * 1000) // 30 minute window
                
                val hasPermission = ContextCompat.checkSelfPermission(
                    application, 
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED

                if (alertsEnabledFlow.value && hasPermission) {
                    eventsFlow.value.forEach { event ->
                        if (event.timestamp != null && event.timestamp in now..threshold) {
                            Log.i("SmsAlert", "AUTOMATED ALERT: '${event.title}' begins in less than 30 minutes.")
                        }
                    }
                }
                
                delay(60000) // Check every minute
            }
        }
    }
}
