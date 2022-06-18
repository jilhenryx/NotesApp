package com.example.notesapp.interfaces

import com.example.notesapp.data.Category

class NotesAppClickListeners {
    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }

    interface OnNoteClickListener {
        fun onNoteClick(noteId: Long)
    }
}