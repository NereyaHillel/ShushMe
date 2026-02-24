# ShushMe 🤫

**ShushMe** is a native Android application built with Kotlin that actively monitors ambient noise levels and automatically triggers a customizable "shush" sound when a user-defined decibel threshold is exceeded.

It features real-time audio amplitude processing, custom sound recording, and a community-driven cloud library powered by Firebase, allowing users to share and download custom shush sounds.

## 🚀 Features

* **Real-Time Noise Monitoring:** Utilizes Android's `MediaRecorder` API to constantly sample microphone input and calculate ambient noise amplitude in real-time.
* **Dynamic Thresholding:** Users can dynamically adjust the noise tolerance threshold via an intuitive UI slider.
* **Custom Audio Recording:** Includes a built-in recording interface allowing users to create their own personalized audio alerts.
* **Cloud Sound Library:** Integrated with Firebase Storage to allow users to browse, preview, download, and share sounds with the ShushMe community.
* **Secure Authentication:** Powered by Firebase Authentication (supporting Email, Phone, and Google Sign-In) to manage user profiles and securely attribute shared sounds.
* **Resource Management:** Carefully handles audio focus, background states, and hardware resources using `MediaPlayer` and `MediaRecorder` lifecycles to prevent memory leaks and optimize battery life.

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI Architecture:** ViewBinding, standard Android XML Layouts, and `RecyclerView` for dynamic lists.
* **Hardware APIs:** `MediaRecorder` (amplitude detection & audio recording), `MediaPlayer` (local and remote URL playback).
* **Backend & Cloud:**
    * Firebase Authentication (Firebase UI)
    * Firebase Cloud Storage
* **Concurrency:** Android `Handler` and `Looper` for managing real-time UI updates from audio sampling threads.

## 📁 Project Structure

The codebase follows a clean, modular structure separating UI, data management, and utility classes:

* **`model/`**: Contains the `DataManager` singleton handling local file I/O and state, and the `SoundItem` data class utilizing the Builder pattern.
* **`adapters/`**: Houses the `SoundAdapter`, which dynamically switches between local "selection mode" and cloud "download mode" using shared UI components.
* **`utils/`**: Contains core hardware wrappers like `SingleSoundPlayer` and `SoundManager` for robust audio lifecycle management.
* **`interfaces/`**: Clean callback interfaces (`SoundPlayerCallback`, `FirebaseStorageCallback`, etc.) for decoupling UI components from asynchronous background tasks.

## ⚙️ Installation & Setup

To run this project locally, you will need Android Studio and a Firebase project setup.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/NereyaHillel/ShushMe
    ```
2.  **Open the project** in Android Studio.
3.  **Firebase Setup:**
    * Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
    * Register a new Android App with the package name `com.dev.nereya.shushme`.
    * Download the `google-services.json` file and place it in the `app/` directory of the project.
    * Enable **Authentication** (Email/Password, Google, Phone) and **Cloud Storage** in your Firebase console.
4.  **Sync Gradle** and run the app on a physical device (Emulators may not accurately simulate real-time microphone amplitude).

## 📱 Screenshots
*(Add a table of screenshots here showcasing the Main Screen, Recording UI, and Shared Sounds list)*
| Main Screen | Recording Interface | Community Sounds |
| :---: | :---: | :---: |
| <img src="link_to_img" width="200"/> | <img src="link_to_img" width="200"/> | <img src="link_to_img" width="200"/> |

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.