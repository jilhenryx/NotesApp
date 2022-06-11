package com.example.notesapp

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.notesapp.ui.NoteActivity
import com.example.notesapp.utility.NOTE_ID_INTENT_KEY
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteActivityInstrumentedTest {
    private val notePosition = 3
    private val intent =
        Intent(ApplicationProvider.getApplicationContext(), NoteActivity::class.java)
            .putExtra(NOTE_ID_INTENT_KEY, notePosition)

    @get:Rule
    val activityRule = activityScenarioRule<NoteActivity>(intent)


}