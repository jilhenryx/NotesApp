package com.example.notesapp.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.notesapp.data.Category
import com.example.notesapp.data.Note

@Database(
    entities = [Note::class, Category::class],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = NotesAppDatabase.NotesAppAutoMigrationSpec::class
        )
    ]
)
abstract class NotesAppDatabase() : RoomDatabase() {
    abstract fun notesDao(): NoteDao

    @DeleteTable(tableName = "text_items")
    class NotesAppAutoMigrationSpec : AutoMigrationSpec
}