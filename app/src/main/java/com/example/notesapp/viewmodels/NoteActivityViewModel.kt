package com.example.notesapp.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.Category
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.utility.PACKAGE_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//Keys for SavedStateHandle
private const val NOTE_ID_BUNDLE_KEY = "${PACKAGE_NAME}.NOTE_ID_BUNDLE_KEY"
private const val NOTE_TITLE_BUNDLE_KEY = "${PACKAGE_NAME}.NOTE_TITLE_BUNDLE_KEY"
private const val NOTE_TEXT_BUNDLE_KEY = "${PACKAGE_NAME}.NOTE_TEXT_BUNDLE_KEY"
private const val NOTE_CATEGORY_ID_BUNDLE_KEY = "${PACKAGE_NAME}.NOTE_CATEGORY_BUNDLE_KEY"
private const val NOTE_TYPE_BUNDLE_KEY = "${PACKAGE_NAME}.NOTE_TYPE_BUNDLE_KEY"
private const val NOTE_MODE_BUNDLE_KEY = "${PACKAGE_NAME}.NOTE_MODE_BUNDLE_KEY"
private const val TAG = "NoteActivityViewModel"
private const val NO_NOTE_ID = -1L

@HiltViewModel
class NoteActivityViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository
) : ViewModel() {

    internal var originalStateRestored = false
    internal var editModeActivated = false
    internal var isNewNote = false
        set(value) {
            editModeActivated = value
            field = value
        }
    internal var noteId = NO_NOTE_ID
    internal val originalNoteValues = hashMapOf<String, String>()
    internal lateinit var categories: List<Category>
    private lateinit var selectedNote: Note


    companion object {
        //Keys for NoteValues
        internal const val NOTE_ID_KEY = "id"
        internal const val NOTE_CATEGORY_ID_KEY = "category"
        internal const val NOTE_TITLE_KEY = "title"
        internal const val NOTE_TEXT_KEY = "text"
    }

    internal suspend fun getNote(): Note {
        Log.d(TAG, "getNote: With ID = $noteId")
        selectedNote = repository.getNote(noteId)
        Log.d(TAG, "getNote: Returning with ID = ${selectedNote.id}")
        return selectedNote
    }

    internal suspend fun getNoteCategories(): List<Category> {
        //Categories are static and should only be gotten if uninitialized
        if (!this::categories.isInitialized)
            categories = repository.getCategories()

        return categories
    }

    internal fun getCategoryName(categoryId: Int): String {
        //Alternatively, Note can be gotten with Category from the database
        // but since category is a constant list of 4 finding the right category for the dropdown
        // like does not generate extra overhead
        val category = categories.firstOrNull { it.id == categoryId }
        return category?.name ?: "Work"
    }

    internal fun storeNoteOriginalValues() {
        originalNoteValues[NOTE_ID_KEY] = selectedNote.id.toString()
        originalNoteValues[NOTE_CATEGORY_ID_KEY] = selectedNote.categoryId.toString()
        originalNoteValues[NOTE_TITLE_KEY] = selectedNote.title
        originalNoteValues[NOTE_TEXT_KEY] = selectedNote.text
    }

    internal fun saveNoteActivityState() {
        //Storing Only Original Note Details because Activity Destruction saves note changes
        savedStateHandle[NOTE_ID_BUNDLE_KEY] =
            originalNoteValues.getOrDefault(NOTE_ID_KEY, NO_NOTE_ID.toString())

        savedStateHandle[NOTE_CATEGORY_ID_BUNDLE_KEY] =
            originalNoteValues.getOrDefault(NOTE_CATEGORY_ID_KEY, "0")

        savedStateHandle[NOTE_TITLE_BUNDLE_KEY] =
            originalNoteValues.getOrDefault(NOTE_TITLE_KEY, "")

        savedStateHandle[NOTE_TEXT_BUNDLE_KEY] =
            originalNoteValues.getOrDefault(NOTE_TEXT_KEY, "")

        savedStateHandle[NOTE_TYPE_BUNDLE_KEY] = isNewNote

        savedStateHandle[NOTE_MODE_BUNDLE_KEY] = editModeActivated
    }

    internal fun restoreNoteActivityState() {
        originalNoteValues[NOTE_ID_KEY] =
            savedStateHandle[NOTE_ID_BUNDLE_KEY] ?: NO_NOTE_ID.toString()

        originalNoteValues[NOTE_CATEGORY_ID_KEY] =
            savedStateHandle[NOTE_CATEGORY_ID_BUNDLE_KEY] ?: "0"

        originalNoteValues[NOTE_TITLE_KEY] =
            savedStateHandle[NOTE_TITLE_BUNDLE_KEY] ?: ""

        originalNoteValues[NOTE_TEXT_KEY] =
            savedStateHandle[NOTE_TEXT_BUNDLE_KEY] ?: ""

        isNewNote = savedStateHandle[NOTE_TYPE_BUNDLE_KEY] ?: false

        editModeActivated = savedStateHandle[NOTE_MODE_BUNDLE_KEY] ?: false

        noteId = originalNoteValues.getOrDefault(NOTE_ID_KEY, NO_NOTE_ID.toString()).toLong()
    }

    /** Checks if note should be written to the database
     *  This is necessary to prevent unnecessary writes to the database
     *  as note changes gets saved to the database during onPause
     */
    internal fun saveNoteMediator(noteValues: Map<String, String>) {
        val noteIsBlank = noteIsBlank(noteValues)
        val canSaveNote = noteChanged(noteValues) && !noteIsBlank
        if (!canSaveNote) {
            //Delete New Note that was saved during activity reconfigurations
            // if user makes no changes
            if (isNewNote && noteIsBlank) deleteNote()
            Log.d(TAG, "saveNoteMediator: Saving Note Skipped")
            return
        }
        saveNote(noteValues)
    }

    //Saves notes to the database
    private fun saveNote(noteValues: Map<String, String>) {
        Log.d(TAG, "saveNote: Sent to Repository")
        viewModelScope.launch {
            repository.saveNote(noteValues, selectedNote, (isNewNote && !originalStateRestored))
            //New Note Saved. Update ID
        }
    }

    /*
    * Note Changed is true if Note Category, Title or Text Changes from
    * its initial values when activity is created
     */
    private fun noteChanged(editTextsValues: Map<String, String>): Boolean {
        return (editTextsValues[NOTE_TITLE_KEY] != selectedNote.title ||
                editTextsValues[NOTE_TEXT_KEY] != selectedNote.text ||
                editTextsValues[NOTE_CATEGORY_ID_KEY]?.toInt() != selectedNote.categoryId
                )
    }

    private fun noteIsBlank(editTextsValues: Map<String, String>): Boolean {
        return editTextsValues[NOTE_TITLE_KEY]!!.isBlank() &&
                editTextsValues[NOTE_TEXT_KEY]!!.isBlank()

    }

    internal fun deleteNote() {
        Log.d(TAG, "deleteNote: ")
        viewModelScope.launch { repository.deleteNote(selectedNote) }
    }
}