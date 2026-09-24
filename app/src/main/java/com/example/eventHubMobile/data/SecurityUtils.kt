package com.example.eventHubMobile.data

import android.util.Patterns

object SecurityUtils {

    fun sanitize(input: String): String {
        return input.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("'", "''") // SQL escape
            .replace("\"", "&quot;")
    }

    fun isSafe(input: String): Boolean {
        val dangerousPatterns = listOf("<script>", "javascript:", "onload=", "onerror=")
        return dangerousPatterns.none { input.contains(it, ignoreCase = true) }
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
