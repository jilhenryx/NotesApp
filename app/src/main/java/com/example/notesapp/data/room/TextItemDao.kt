package com.example.notesapp.data.room

import androidx.room.*
import com.example.notesapp.helpers.undoredohelper.TextItem

@Dao
interface TextItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTextItems(textItems: List<TextItem>)

    @Delete
    suspend fun deleteTextItems(items: List<TextItem>)

    @Delete
    suspend fun deleteTextItem(item: TextItem)

    @Query("DELETE FROM text_items")
    suspend fun deleteAllTextItems()

    @Query("SELECT * FROM text_items")
    suspend fun getAllTextItems(): List<TextItem>
}