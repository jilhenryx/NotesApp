package com.example.notesapp.data

import android.util.Log
import com.example.notesapp.data.room.NoteDao
import com.example.notesapp.utility.Constants
import com.example.notesapp.viewmodels.NoteActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "NotesRepository"

@Singleton
class NotesRepository @Inject constructor(private val notesDao: NoteDao) {

    internal suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) { notesDao.getCategories() }
    }

    internal suspend fun getNotesWithCategory(): List<NoteWithCategory> {
        Log.d(TAG, "getNotesWithCategory: ")
        return withContext(Dispatchers.IO) {
            notesDao.getAllNotesWithCategory()
        }
    }

    internal suspend fun getNote(noteId: Long): Note {
        val dateTime = Constants.getDateTime()

        return withContext(Dispatchers.IO) {
            notesDao.getNote(noteId) ?: Note(
                title = "",
                text = "",
                categoryId = 0,
                dateCreated = dateTime,
                dateModified = dateTime
            ).setNoteId()
        }
    }

    internal suspend fun saveNote(
        noteValues: Map<String, String>,
        selectedNote: Note,
        saveToManager: Boolean
    ) {
        selectedNote.title =
            noteValues.getOrDefault(NoteActivityViewModel.NOTE_TITLE_KEY, "")
        selectedNote.text =
            noteValues.getOrDefault(NoteActivityViewModel.NOTE_TEXT_KEY, "")
        selectedNote.categoryId =
            noteValues.getOrDefault(NoteActivityViewModel.NOTE_CATEGORY_ID_KEY, "0").toInt()
        selectedNote.dateModified = Constants.getDateTime()

        withContext(Dispatchers.IO) {
            if (saveToManager) {
                Log.d(TAG, "saveNote: Note Saved to Database with id = ${selectedNote.id}")
                notesDao.insertNote(selectedNote)
            } else {
                Log.d(TAG, "saveNote: Note Updated! with id = ${selectedNote.id}")
                notesDao.updateNote(selectedNote)
            }
        }
    }

    internal suspend fun deleteNote(selectedNote: Note) {
        Log.d(TAG, "deleteNote: ")
        withContext(Dispatchers.IO) { notesDao.deleteNote(selectedNote) }
    }
}