package com.example.notesapp.data.room

import androidx.room.*
import com.example.notesapp.data.Category
import com.example.notesapp.data.Note
import com.example.notesapp.data.NoteWithCategory

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query(
        "SELECT notes.id, notes.title, notes.text, notes.categoryId, " +
                "notes.dateCreated, notes.dateModified, categories.name, categories.colorRes " +
                "FROM notes INNER JOIN categories ON notes.categoryId = categories.id "
    )
    suspend fun getAllNotesWithCategory(): List<NoteWithCategory>

    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<Category>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNote(noteId: Long): Note?
}