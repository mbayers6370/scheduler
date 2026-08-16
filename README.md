# Scheduler

A modern, intuitive Android productivity application designed for flexible event management and automated notifications. Built with **Jetpack Compose** and **Room Database**, the Scheduler provides a professional experience for organizing personal and professional milestones.

## 🚀 Key Features

- **Hierarchical Organization**: Create individual events or group them into "Collections" (folders) for major projects or trips.
- **Dynamic Chronological Filtering**: View your schedule through "Today," "This Week," or "This Month" lenses, strictly following standard calendar boundaries.
- **Advanced Search**: Real-time querying of events and collections with direct navigation to results.
- **Automated SMS Alerts**: Stay on track with automated notifications triggered 30 minutes before any scheduled event.
- **Secure Persistence**: All data, including user profiles and events, is stored locally in a secure SQLite database.

## 🛡️ Security & Privacy

- **Salted Hashing**: User passwords are never stored in plaintext. The app uses the **PBKDF2 with HMAC-SHA256** algorithm with a unique per-user salt to ensure top-tier credential protection.
- **Dynamic Permissions**: The app follows the principle of least privilege, requesting SMS permissions only when the user explicitly enables alerts.

## 🛠️ Technical Stack

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Database**: Room Persistence Library (SQLite)
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines and StateFlow
- **Dependency Management**: Gradle Version Catalog (libs.versions.toml)

## 📱 Getting Started

1.  **Clone the Repository**:
    ```bash
    git clone [repository-url]
    ```
2.  **Open in Android Studio**:
    Open the root folder in Android Studio (Ladybug or newer recommended).
3.  **Build and Run**:
    Select an emulator (API 24+) or a physical device and click the **Run** button.

## 📜 Permissions

- **`android.permission.SEND_SMS`**: Required for the automated notification feature. The app remains fully functional if this permission is denied.

## 🎓 Project Reflection

### 1. Requirements & User Needs
The Scheduler was designed to address the user need for a centralized, reliable, and offline-capable time management tool. The core goals were to provide secure user authentication, persistent storage for individual and grouped events, and proactive notifications via SMS to ensure users never miss critical milestones.

### 2. User-Centered Design
The app utilizes a series of intuitive screens, including a personalized Dashboard, hierarchical Collection views, and an Advanced Search overlay, to reduce cognitive load. The UI keeps users in mind through a high-contrast dark theme for readability and expandable card components that provide detail without cluttering the primary view. The success of the design lies in its ability to balance power (complex organization) with simplicity (clear chronological filtering).

### 3. Coding Strategy & Techniques
Development followed the **MVVM (Model-View-ViewModel)** architectural pattern. Strategies included using **Jetpack Compose** for a modern declarative UI and **Room with Kotlin Flow** for a reactive data layer that updates the UI automatically when the database changes. These techniques ensure the codebase is modular and maintainable, a strategy directly applicable to any large-scale professional software project.

### 4. Testing & Functional Verification
Testing was conducted iteratively via the Android Emulator and Logcat analysis. This process was vital for ensuring the persistent database correctly survived app restarts and that the automated SMS triggers fired accurately within their 30-minute windows. Testing specifically revealed the need for more robust logic when calculating calendar boundaries for "This Week" and "This Month" filters.

### 5. Innovation in Challenges
A significant design challenge was the user experience of organizing events into folders. I innovated by creating a unified **"Manage Content"** interface. Instead of separate screens for adding and removing items, I implemented a universal selector that shows all active events and their current locations, allowing users to "re-home" or "steal" events from other folders in a single, streamlined interaction.

### 6. Demonstration of Skills
I was particularly successful in the **Data & Security Layer**. Implementing a Room database that handles relational data alongside professional **salted PBKDF2 password hashing** demonstrates my ability to build mobile applications that are not only functional but also secure and compliant with industry best practices.

---
*Developed as part of the CS-360 Mobile Architecture & Programming curriculum.*
