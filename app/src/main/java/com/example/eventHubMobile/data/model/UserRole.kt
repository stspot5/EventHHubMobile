package com.example.eventHubMobile.data.model

import androidx.annotation.StringRes
import com.example.eventHubMobile.R

enum class UserRole(val code: String, @StringRes val labelRes: Int) {
    PARTICIPANT("participant", R.string.role_participant),
    ORGANIZER("organizer", R.string.role_organizer);

    companion object {
        fun fromCode(code: String?): UserRole {
            return entries.find { it.code.lowercase() == code?.lowercase() } ?: PARTICIPANT
        }
    }
}
