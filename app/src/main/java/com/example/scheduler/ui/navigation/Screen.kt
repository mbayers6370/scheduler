package com.example.scheduler.ui.navigation

/**
 * Defines all navigation routes within the application.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object Profile : Screen("profile")
    object PersonalInfo : Screen("personal_info")
    object Security : Screen("security")
    object Help : Screen("help")
    object Archive : Screen("archive")
    object CreateEvent : Screen("create_event?eventId={eventId}&parentId={parentId}") {
        fun createRoute(eventId: String? = null, parentId: String? = null): String {
            val base = "create_event"
            val params = mutableListOf<String>()
            if (eventId != null) params.add("eventId=$eventId")
            if (parentId != null) params.add("parentId=$parentId")
            return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
        }
    }
    object CreateCollection : Screen("create_collection")
    object CollectionDetail : Screen("collection?title={title}&id={id}") {
        fun createRoute(title: String, id: String?) = 
            "collection?title=$title&id=${id ?: ""}"
    }
}
