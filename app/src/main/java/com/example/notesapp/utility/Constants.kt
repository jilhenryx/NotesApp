package com.example.notesapp.utility

import java.text.SimpleDateFormat
import java.util.*

const val PACKAGE_NAME = "COM.EXAMPLE.NOTESAPP"
const val NOTE_ID_INTENT_KEY = "${PACKAGE_NAME}NOTE_POSITION_KEY"

object Constants {
    fun getDateTime(): String {
        val c = Calendar.getInstance().time
        val formatter =
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) //or use getDateInstance()
        return formatter.format(c)
    }

    fun generateEntityID(): Long {
        return System.currentTimeMillis()
    }
}