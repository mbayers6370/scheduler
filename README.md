# Scheduler

An Android productivity application designed for event scheduling, hierarchical collection management, and automated notifications. Built with **Kotlin**, **Jetpack Compose**, and **Room Database**, Scheduler implements MVVM architecture to provide an offline-first scheduling experience.

## Key Features

- **Hierarchical Organization**: Create individual events or group them into "Collections" for major projects or travel itineraries.
- **Chronological Filtering**: Filter schedules through "Today," "This Week," and "This Month" views bounded by calendar logic.
- **Real-Time Search**: Query events and collections with direct navigation to search results.
- **Automated SMS Alerts**: Scheduled background notification monitoring triggers SMS alerts 30 minutes prior to event start times.
- **Secure Persistence**: Local SQLite persistence for user profiles, events, and collection relationships.

## Security & Privacy

- **Credential Hashing**: User passwords are stored as salted PBKDF2 hashes using **PBKDF2WithHmacSHA256** (10,000 iterations, unique per-user salt).
- **Least Privilege Permissions**: SMS permission (`SEND_SMS`) is requested dynamically only when SMS alerts are enabled by the user.

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Database**: Room Persistence Library (SQLite)
- **Architecture**: MVVM with Kotlin Coroutines and StateFlow
- **Annotation Processing**: KSP (Kotlin Symbol Processing)
- **Dependency Management**: Gradle Version Catalog (`libs.versions.toml`)

## Getting Started

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   ```
2. **Open in Android Studio**:
   Open the root project directory in Android Studio (Ladybug or newer).
3. **Build and Run**:
   Build and launch on an Android emulator or physical device running API level 24 or higher.

## Permissions

- **`android.permission.SEND_SMS`**: Required for automated 30-minute event notification alerts.

## Project Architecture & Reflection

### 1. Requirements & User Needs
Scheduler addresses offline time management requirements by offering user authentication, relational event persistence, and automated event notifications.

### 2. User-Centered Design
The interface incorporates high-contrast dark theme styling, structured dashboards, hierarchical collection detail views, and expandable cards to present event information without clutter.

### 3. Architecture & Code Organization
The application adheres to MVVM principles. Jetpack Compose delivers a declarative UI layer powered by `StateFlow` streams from Room DAO queries, maintaining clean separation of concerns across UI, Domain, and Data layers.

### 4. Testing & Verification
Verified via JUnit unit tests and Room instrumented migration tests (`DatabaseMigrationTest`) on connected Android emulators, ensuring database schema transitions and foreign key integrity.

### 5. Collection Content Management
Implements a unified selector pattern for managing collection items, allowing users to reassign or associate events across collections in a single workflow.

### 6. Data & Security Layer
Combines Room foreign key constraints (`ON DELETE CASCADE`) with industry-standard PBKDF2 password hashing to maintain database referential integrity and credential security.
