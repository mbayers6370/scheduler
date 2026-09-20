package com.example.scheduler

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.scheduler.ui.navigation.NavGraph
import com.example.scheduler.ui.theme.SchedulerTheme
import com.example.scheduler.ui.viewmodel.AuthViewModel
import com.example.scheduler.ui.viewmodel.EventViewModel

/**
 * Main Activity of the Scheduler application.
 * Host for the Compose-based navigation and UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchedulerTheme {
                val authViewModel: AuthViewModel = viewModel()
                val eventViewModel: EventViewModel = viewModel()
                val navController = rememberNavController()
                val context = this
                
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    eventViewModel.smsAlertsEnabled.value = isGranted
                    if (isGranted) Toast.makeText(context, "SMS Alerts Enabled", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(painter = painterResource(id = R.drawable.background_2), contentDescription = null, modifier = Modifier.fillMaxWidth().height(260.dp).align(Alignment.TopStart), contentScale = ContentScale.Crop, alpha = 0.4f)
                        Image(painter = painterResource(id = R.drawable.background_1), contentDescription = null, modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart), contentScale = ContentScale.FillWidth, alpha = 0.5f)
                        
                        NavGraph(navController, context, authViewModel, eventViewModel, launcher)
                    }
                }
            }
        }
    }
}
