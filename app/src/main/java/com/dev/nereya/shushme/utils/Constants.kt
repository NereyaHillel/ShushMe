package com.dev.nereya.shushme.utils

object Constants {

    object Audio {
        const val SAMPLE_RATE = 44100
        const val DEFAULT_AMPLITUDE_DIVISOR = 300
        const val POLL_INTERVAL_MS = 200L
    }

    object Permissions {
        const val RECORD_AUDIO_REQUEST_CODE = 200
    }

    object UI {
        const val DEFAULT_THRESHOLD = 50
        const val SYSTEM_RESOURCE_PATH = "system_resource"
        const val DEFAULT_USER_NAME = "User"
        const val GUEST_NAME = "Guest"
    }
    object Colors {
        const val SELECTED_STROKE = "#647de6"
        const val UNSELECTED_STROKE = "#e1e6f0"
        const val RECORDING_ACTIVE = "#FF1744"
        const val RECORDING_INACTIVE = "#BBDEFB"
    }
}