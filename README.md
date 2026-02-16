# DJSortApp 🎵📍

**DJSortApp** is a smart Android application designed to bridge the gap between **DJs** and the **Crowd** in clubs and parties. It allows real-time song requests, queue management, and location sharing for DJs.

## ✨ Features

### 🎧 For DJs (Admin)
-   **Dashboard**: View incoming song requests in real-time.
-   **Queue Management**: Accept or Decline requests. Accepted songs move to a sorted "Queue" tab.
-   **Location Sharing**: Update your current location using GPS. The app converts coordinates to a readable address (e.g., "Tel Aviv, Israel").
-   **Profile**: Manage your status and location.

### 🕺 For The Crowd (Users)
-   **Song Search**: Search for songs using the **Deezer API**.
-   **Request Songs**: Send requests directly to the DJ.
-   **DJ List**: Browse a list of active DJs.
    -   **Real-Time Location**: See exactly where DJs are playing (e.g., "King George St, Tel Aviv").
    -   **Google Maps**: Click to view the DJ's location on an interactive map.
-   **Request Status**: See your request history (Pending/Accepted/Declined).

## 🛠 Tech Stack
-   **Language**: Java
-   **Platform**: Android (Min SDK 24, Target SDK 35)
-   **Backend**: Firebase (Firestore, Authentication)
-   **APIs**:
    -   **Deezer API** (Song Search)
    -   **Google Maps SDK** (Location Visualization)
    -   **Android Location Services** (GPS & Geocoding)
-   **Architecture**: MVVM / Clean Architecture principles
-   **Build System**: Gradle (Kotlin DSL)

## 🚀 Setup & Installation

1.  **Clone the Repo**:
    ```bash
    git clone https://github.com/YOUR_USERNAME/DJSortApp.git
    ```
2.  **Open in Android Studio**.
3.  **Firebase Setup**:
    -   Add your `google-services.json` to the `app/` folder.
    -   Enable **Authentication** (Email/Password).
    -   Enable **Firestore Database**.
4.  **Google Maps Setup**:
    -   Get an API Key from Google Cloud Console.
    -   Open `AndroidManifest.xml` and replace `YOUR_API_KEY` with your actual key.
5.  **Build & Run**:
    -   Sync Gradle and run on an Emulator or Physical Device.

## 📱 Screenshots
*(Add screenshots here later)*

## 📄 License
This project is for educational and portfolio purposes.
