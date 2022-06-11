package com.example.notesapp.helpers.undoredohelper

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notesapp.utility.Constants

@Entity(tableName = "text_items")
data class TextItem(
    @PrimaryKey
    internal var id: Long = 0,
    internal var index: Int,
    @Embedded
    internal var range: TextItemRange,
    internal var text: String,
) {
    internal fun setTextItemID(): TextItem {
        id = Constants.generateEntityID()
        return this
    }
}

data class TextItemRange(
    internal var start: Int,
    internal var end: Int,
)