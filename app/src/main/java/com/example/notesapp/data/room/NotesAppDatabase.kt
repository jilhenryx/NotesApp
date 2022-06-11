package com.example.notesapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notesapp.data.Category
import com.example.notesapp.data.Note
import com.example.notesapp.helpers.undoredohelper.TextItem

@Database(
    entities = [Note::class, Category::class, TextItem::class],
    version = 1,
    exportSchema = false
)
abstract class NotesAppDatabase() : RoomDatabase() {
    abstract fun notesDao(): NoteDao
    abstract fun textItemDao(): TextItemDao
}