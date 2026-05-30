package com.anix.app.core.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatTimestamp(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val now = Instant.now()
        val duration = Duration.between(instant, now)

        when {
            duration.seconds < 60 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            duration.toDays() < 30 -> "${duration.toDays() / 7}w ago"
            duration.toDays() < 365 -> "${duration.toDays() / 30}mo ago"
            else -> {
                val formatter = DateTimeFormatter
                    .ofPattern("dd MMM yyyy")
                    .withZone(ZoneId.systemDefault())
                formatter.format(instant)
            }
        }
    } catch (e: Exception) {
        isoString
    }
}
