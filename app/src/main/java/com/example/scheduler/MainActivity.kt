package com.example.scheduler

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduler.ui.screens.*
import com.example.scheduler.ui.theme.SchedulerTheme

/**
 * Entry point Activity for the Scheduler application.
 * Manages top-level navigation state, permission orchestration, and serves 
 * as the host for the Compose-based UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enabling edge-to-edge support for a modern, immersive visual experience
        enableEdgeToEdge()
        
        setContent {
            SchedulerTheme {
                // Initialize the shared ViewModel for data and authentication management
                val viewModel: EventViewModel = viewModel()
                
                // --- Navigation & UI State ---
                var currentScreen by remember { mutableStateOf("login") }
                var selectedCollection by remember { mutableStateOf<String?>(null) }
                var selectedCollectionEvent by remember { mutableStateOf<Event?>(null) }
                var previousScreen by remember { mutableStateOf("main") }
                var eventToEdit by remember { mutableStateOf<Event?>(null) }
                
                // --- Permission & Notification State ---
                var smsPermissionGranted by remember { mutableStateOf(false) }
                val smsAlertsEnabled by viewModel.smsAlertsEnabled.collectAsState()

                // --- Reactive Data Subscriptions ---
                val activeEvents by viewModel.activeEvents.collectAsState()
                val archivedEvents by viewModel.archivedEvents.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()

                // Filter top-level items for the primary dashboard view
                val topLevelActiveEvents = remember(activeEvents) {
                    activeEvents.filter { it.parentCollectionId == null }
                }

                val topLevelArchivedEvents = remember(archivedEvents) {
                    archivedEvents.filter { it.parentCollectionId == null }
                }

                // Prepare datasets for the 'Manage Collection Content' feature
                val allManageableEvents = remember(activeEvents) {
                    activeEvents.filter { !it.isCollection }
                }

                val collectionMap = remember(activeEvents) {
                    activeEvents.filter { it.isCollection }.associate { it.id to it.title }
                }
                
                // --- SMS Permission Launcher ---
                val context = this
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Synchronize the ViewModel state with the user's permission response
                    smsPermissionGranted = isGranted
                    viewModel.smsAlertsEnabled.value = isGranted
                    
                    if (isGranted) {
                        Toast.makeText(context, "SMS Alerts Enabled", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Permission Denied - Alerts will remain inactive", Toast.LENGTH_SHORT).show()
                    }

                    // If we were onboarding, proceed to the main dashboard now that choice is made
                    if (currentScreen == "onboarding_permissions") {
                        currentScreen = "main"
                    }
                }

                // Re-verify current permission status on app initialization
                LaunchedEffect(Unit) {
                    val status = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                    smsPermissionGranted = status == PackageManager.PERMISSION_GRANTED
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Top Background Pattern
                        Image(
                            painter = painterResource(id = R.drawable.background_2),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp) 
                                .align(Alignment.TopStart),
                            contentScale = ContentScale.Crop, 
                            alpha = 0.4f
                        )
                        
                        // Bottom Background Pattern
                        Image(
                            painter = painterResource(id = R.drawable.background_1),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart),
                            contentScale = ContentScale.FillWidth,
                            alpha = 0.5f
                        )

                        when (currentScreen) {
                            "login" -> LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = { currentScreen = "main" },
                                onRegisterClick = { currentScreen = "register" }
                            )
                            "register" -> CreateAccountScreen(
                                viewModel = viewModel,
                                onAccountCreated = { currentScreen = "onboarding_permissions" },
                                onBackToLogin = { currentScreen = "login" }
                            )
                            "onboarding_permissions" -> OnboardingPermissionsScreen(
                                onEnableClick = {
                                    val status = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                                    if (status == PackageManager.PERMISSION_GRANTED) {
                                        viewModel.smsAlertsEnabled.value = true
                                        smsPermissionGranted = true
                                        currentScreen = "main"
                                    } else {
                                        launcher.launch(Manifest.permission.SEND_SMS)
                                    }
                                },
                                onSkipClick = {
                                    viewModel.smsAlertsEnabled.value = false
                                    currentScreen = "main"
                                }
                            )
                            "main" -> MainScreen(
                                userName = currentUser?.firstName ?: "Guest",
                                events = topLevelActiveEvents,
                                onCollectionClick = { collection ->
                                    selectedCollection = collection.title
                                    selectedCollectionEvent = collection
                                    previousScreen = "main"
                                    currentScreen = "collection_detail"
                                },
                                onCreateEvent = {
                                    eventToEdit = null
                                    previousScreen = "main"
                                    currentScreen = "create_event"
                                },
                                onCreateCollection = {
                                    previousScreen = "main"
                                    currentScreen = "create_collection"
                                },
                                onEditEvent = { event ->
                                    eventToEdit = event
                                    previousScreen = "main"
                                    currentScreen = "create_event"
                                },
                                onDeleteEvent = { event ->
                                    viewModel.deleteEvent(event)
                                },
                                onProfileClick = { currentScreen = "profile" },
                                onArchiveClick = { currentScreen = "archive" }
                            )
                            "collection_detail" -> CollectionDetailScreen(
                                title = selectedCollection ?: "Collection",
                                collection = selectedCollectionEvent,
                                events = if (previousScreen == "archive") {
                                    archivedEvents.filter { formatMonthYear(it.timestamp) == selectedCollection }
                                } else {
                                    activeEvents.filter { it.parentCollectionId == selectedCollectionEvent?.id }
                                },
                                allManageableEvents = allManageableEvents,
                                collectionMap = collectionMap,
                                onBackClick = { currentScreen = previousScreen },
                                onCreateEvent = {
                                    eventToEdit = null
                                    previousScreen = "collection_detail"
                                    currentScreen = "create_event"
                                },
                                onCreateCollection = {
                                    previousScreen = "collection_detail"
                                    currentScreen = "create_collection"
                                },
                                onEditEvent = { event ->
                                    eventToEdit = event
                                    previousScreen = "collection_detail"
                                    currentScreen = "create_event"
                                },
                                onDeleteEvent = { event ->
                                    viewModel.deleteEvent(event)
                                },
                                onRenameCollection = { collection, newName ->
                                    viewModel.updateEvent(collection.copy(title = newName))
                                    selectedCollection = newName
                                },
                                onDeleteCollection = { collection ->
                                    viewModel.deleteEvent(collection)
                                },
                                onManageCollectionEvents = { selected ->
                                    val currentId = selectedCollectionEvent?.id ?: return@CollectionDetailScreen
                                    
                                    // 1. Find events to add or move here
                                    selected.forEach { event ->
                                        if (event.parentCollectionId != currentId) {
                                            viewModel.updateEvent(event.copy(parentCollectionId = currentId))
                                        }
                                    }

                                    // 2. Find events that were removed from this collection
                                    val previouslyInCollection = activeEvents.filter { it.parentCollectionId == currentId }
                                    previouslyInCollection.forEach { event ->
                                        if (selected.none { it.id == event.id }) {
                                            viewModel.updateEvent(event.copy(parentCollectionId = null))
                                        }
                                    }
                                }
                            )
                            "profile" -> ProfileScreen(
                                user = currentUser,
                                isSmsEnabled = smsAlertsEnabled,
                                onBackClick = { currentScreen = "main" },
                                onLogoutClick = { currentScreen = "login" },
                                onPersonalInfoClick = { currentScreen = "personal_info" },
                                onSecurityPrivacyClick = { currentScreen = "security_privacy" },
                                onHelpSupportClick = { currentScreen = "help_support" },
                                onSmsToggle = { enabled ->
                                    if (enabled) {
                                        // Task III.A: Check for permission before enabling alerts
                                        val status = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                                        if (status == PackageManager.PERMISSION_GRANTED) {
                                            viewModel.smsAlertsEnabled.value = true
                                            smsPermissionGranted = true
                                        } else {
                                            launcher.launch(Manifest.permission.SEND_SMS)
                                            // Result handled in launcher callback
                                        }
                                    } else {
                                        viewModel.smsAlertsEnabled.value = false
                                    }
                                }
                            )
                            "personal_info" -> PersonalInformationScreen(
                                user = currentUser,
                                onBackClick = { currentScreen = "profile" },
                                onSaveClick = { currentScreen = "profile" }
                            )
                            "security_privacy" -> SecurityPrivacyScreen(
                                onBackClick = { currentScreen = "profile" }
                            )
                            "help_support" -> HelpSupportScreen(
                                onBackClick = { currentScreen = "profile" }
                            )
                            "archive" -> ArchiveScreen(
                                archivedEvents = topLevelArchivedEvents,
                                onBackClick = { currentScreen = "main" },
                                onMonthClick = { title ->
                                    selectedCollection = title
                                    selectedCollectionEvent = null // Not a specific Event in DB
                                    previousScreen = "archive"
                                    currentScreen = "collection_detail"
                                }
                            )
                            "create_event" -> CreateEventScreen(
                                event = eventToEdit,
                                parentCollectionId = if (previousScreen == "collection_detail") selectedCollectionEvent?.id else null,
                                onBackClick = { currentScreen = previousScreen },
                                onCreateClick = { newEvent ->
                                    if (eventToEdit != null) {
                                        viewModel.updateEvent(newEvent)
                                    } else {
                                        viewModel.addEvent(newEvent)
                                    }
                                    currentScreen = previousScreen
                                }
                            )
                            "create_collection" -> CreateCollectionScreen(
                                onBackClick = { currentScreen = previousScreen },
                                onCreateClick = { newCollection ->
                                    viewModel.addEvent(newCollection)
                                    currentScreen = previousScreen
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
