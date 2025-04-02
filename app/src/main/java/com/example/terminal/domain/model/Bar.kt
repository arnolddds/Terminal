package com.example.terminal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.Date

@Parcelize
data class Bar(
    val timestamp: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
) : Parcelable {
    val calendar: Calendar
        get() = Calendar.getInstance().apply {
            time = Date(this@Bar.timestamp)
        }
}