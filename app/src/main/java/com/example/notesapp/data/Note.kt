package com.example.notesapp.data

import androidx.annotation.ColorInt
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notesapp.utility.Constants

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    internal var id: Long = 0,
    internal var title: String,
    internal var text: String,
    internal var categoryId: Int,
    internal var dateCreated: String,
    internal var dateModified: String,
) {
    internal fun setNoteId(): Note {
        id = Constants.generateEntityID()
        return this
    }
}

data class NoteWithCategory(
    @Embedded
    internal var note: Note,
    internal var name: String,
    @ColorInt internal var colorRes: Int,
)