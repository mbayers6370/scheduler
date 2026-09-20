package com.example.scheduler.ui.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.scheduler.logic.formatMonthYear
import com.example.scheduler.ui.screens.*
import com.example.scheduler.ui.state.MainUiState
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.viewmodel.AuthViewModel
import com.example.scheduler.ui.viewmodel.EventViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    context: Context,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Sync the EventViewModel's user context whenever authentication state changes
    LaunchedEffect(currentUser) {
        eventViewModel.setUserId(currentUser?.username)
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(authViewModel, onLoginSuccess = { navController.navigate(Screen.Main.route) { popUpTo(Screen.Login.route) { inclusive = true } } }, onRegisterClick = { navController.navigate(Screen.Register.route) })
        }
        composable(Screen.Register.route) {
            CreateAccountScreen(authViewModel, onAccountCreated = { navController.navigate(Screen.Onboarding.route) }, onBackToLogin = { navController.popBackStack() })
        }
        composable(Screen.Onboarding.route) {
            OnboardingPermissionsScreen(
                onEnableClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        eventViewModel.smsAlertsEnabled.value = true; navController.navigate(Screen.Main.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } }
                    } else { launcher.launch(Manifest.permission.SEND_SMS) }
                },
                onSkipClick = { eventViewModel.smsAlertsEnabled.value = false; navController.navigate(Screen.Main.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }
            )
        }
        composable(Screen.Main.route) {
            val user by authViewModel.currentUser.collectAsState()
            val uiState by eventViewModel.getMainUiState(user?.firstName ?: "Guest").collectAsState()
            val allEvents by eventViewModel.allEvents.collectAsState()

            if (uiState is MainUiState.Success) {
                val state = uiState as MainUiState.Success
                MainScreen(
                    userName = state.userName,
                    events = state.events.filter { it.parentCollectionId.isNullOrBlank() },
                    onCollectionClick = { navController.navigate(Screen.CollectionDetail.createRoute(it.title, it.id)) },
                    onCreateEvent = { navController.navigate(Screen.CreateEvent.createRoute()) },
                    onCreateCollection = { navController.navigate(Screen.CreateCollection.route) },
                    onEditEvent = { navController.navigate(Screen.CreateEvent.createRoute(eventId = it.id)) },
                    onDeleteEvent = { eventViewModel.deleteEvent(it) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onArchiveClick = { navController.navigate(Screen.Archive.route) }
                )
            }
        }
        composable(
            route = Screen.CollectionDetail.route,
            arguments = listOf(navArgument("title") { type = NavType.StringType }, navArgument("id") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Collection"
            val id = backStackEntry.arguments?.getString("id")
            val activeEvents by eventViewModel.allEvents.collectAsState()
            val archivedEvents by eventViewModel.archivedEvents.collectAsState()
            val isArchive = id.isNullOrBlank()

            CollectionDetailScreen(
                title = title,
                collection = activeEvents.find { it.id == id },
                events = if (isArchive) archivedEvents.filter { formatMonthYear(it.timestamp) == title } else activeEvents.filter { it.parentCollectionId == id },
                allManageableEvents = activeEvents.filter { !it.isCollection },
                collectionMap = activeEvents.filter { it.isCollection }.associate { it.id to it.title },
                onBackClick = { navController.popBackStack() },
                onEditEvent = { navController.navigate(Screen.CreateEvent.createRoute(eventId = it.id)) },
                onDeleteEvent = { eventViewModel.deleteEvent(it) },
                onRenameCollection = { col, name -> eventViewModel.updateEvent(col.copy(title = name)) },
                onDeleteCollection = { eventViewModel.deleteEvent(it); navController.popBackStack() },
                onManageCollectionEvents = { selected ->
                    val currentId = id ?: return@CollectionDetailScreen
                    selected.forEach { if (it.parentCollectionId != currentId) eventViewModel.updateEvent(it.copy(parentCollectionId = currentId)) }
                    activeEvents.filter { it.parentCollectionId == currentId }.forEach { if (selected.none { s -> s.id == it.id }) eventViewModel.updateEvent(it.copy(parentCollectionId = null)) }
                }
            )
        }
        composable(Screen.Profile.route) {
            val user by authViewModel.currentUser.collectAsState()
            val smsEnabled by eventViewModel.smsAlertsEnabled.collectAsState()
            ProfileScreen(user, smsEnabled, onBackClick = { navController.popBackStack() }, onLogoutClick = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) } }, onPersonalInfoClick = { navController.navigate(Screen.PersonalInfo.route) }, onSecurityPrivacyClick = { navController.navigate(Screen.Security.route) }, onHelpSupportClick = { navController.navigate(Screen.Help.route) },
                onSmsToggle = { if (it) { if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) eventViewModel.smsAlertsEnabled.value = true else launcher.launch(Manifest.permission.SEND_SMS) } else eventViewModel.smsAlertsEnabled.value = false })
        }
        composable(Screen.PersonalInfo.route) { 
            val user by authViewModel.currentUser.collectAsState()
            PersonalInformationScreen(user, onBackClick = { navController.popBackStack() }, onSaveClick = { navController.popBackStack() }) 
        }
        composable(Screen.Security.route) { SecurityPrivacyScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Help.route) { HelpSupportScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Archive.route) { val archived by eventViewModel.archivedEvents.collectAsState(); ArchiveScreen(archived, onBackClick = { navController.popBackStack() }, onMonthClick = { navController.navigate(Screen.CollectionDetail.createRoute(it, null)) }) }
        composable(
            route = Screen.CreateEvent.route,
            arguments = listOf(navArgument("eventId") { nullable = true }, navArgument("parentId") { nullable = true })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val parentId = backStackEntry.arguments?.getString("parentId")
            val allEvents by eventViewModel.allEvents.collectAsState()
            var conflictingEvent by remember { mutableStateOf<com.example.scheduler.data.model.Event?>(null) }

            CreateEventScreen(
                event = allEvents.find { it.id == eventId },
                parentCollectionId = parentId,
                onBackClick = { navController.popBackStack() },
                onCreateClick = { event ->
                    val conflict = eventViewModel.validateAndSaveEvent(event)
                    if (conflict == null) navController.popBackStack() else conflictingEvent = conflict
                }
            )

            if (conflictingEvent != null) {
                AlertDialog(
                    onDismissRequest = { conflictingEvent = null },
                    title = { Text("Conflict", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
                    text = { Text("Overlaps with '${conflictingEvent?.title}'.", fontFamily = PoppinsFamily) },
                    confirmButton = { TextButton(onClick = { conflictingEvent = null }) { Text("OK", color = com.example.scheduler.ui.theme.RustOrange) } }
                )
            }
        }
        composable(Screen.CreateCollection.route) { CreateCollectionScreen(onBackClick = { navController.popBackStack() }, onCreateClick = { eventViewModel.addEvent(it); navController.popBackStack() }) }
    }
}
