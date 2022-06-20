package com.example.notesapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.notesapp.data.Category
import com.example.notesapp.data.NoteWithCategory
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.utility.PACKAGE_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository
) : ViewModel() {
    private var previousFilterKey = ""
    private val notesFilterKey = savedStateHandle.getLiveData<String>(NOTES_FILTER_BUNDLE_KEY)
    private var notes = MutableLiveData<List<NoteWithCategory>>()
    private val _displayedNotesMediator = MediatorLiveData<List<NoteWithCategory>>().apply {
        addSource(notesFilterKey) {
            performNotesFiltering()
        }
        addSource(notes) {
            performNotesFiltering()
        }
    }
    internal var isNoteFiltered = false
    internal val displayNotes: LiveData<List<NoteWithCategory>> get() = _displayedNotesMediator

    companion object {
        private const val NOTES_FILTER_BUNDLE_KEY = "${PACKAGE_NAME}.NOTES_FILTER_BUNDLE_KEY"
        private const val TAG = "MainActivityViewModel"
    }

    internal fun filterNotes(filter: String) {
        Log.d(TAG, "filterNotes: ")
        if (filter != previousFilterKey) {
            previousFilterKey = filter
            notesFilterKey.value = filter
//            savedStateHandle[NOTES_FILTER_BUNDLE_KEY] = filter
        }
    }

    internal fun getNotes() {
        Log.d(TAG, "getNotes: from Database")
        viewModelScope.launch {
            val notesList = repository.getNotesWithCategory()
            notes.value = notesList
        }
    }

    internal suspend fun getCategories(): List<Category> {
        return repository.getCategories()
    }

    private fun performNotesFiltering() {
        val filter = notesFilterKey.value ?: ""
        if (filter.isNotBlank()) {
            Log.d(TAG, "onFilterNote: $filter")
            val filteredNotes = notes.value?.filter { it.name == filter }
            isNoteFiltered = true
            _displayedNotesMediator.value = filteredNotes
        } else {
            Log.d(TAG, "No Filter: $filter")
            isNoteFiltered = false
            _displayedNotesMediator.value = notes.value
        }
    }

    override fun onCleared() {
        _displayedNotesMediator.removeSource(notesFilterKey)
        _displayedNotesMediator.removeSource(notes)
        super.onCleared()
    }
}
