package com.example.notesapp.data

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    internal var id: Int,
    internal var name: String,
    @ColorInt internal var colorRes: Int,
    @DrawableRes internal var drawableRes: Int,
    internal var dateCreated: String,
    internal var dateModified: String
) {
    override fun toString(): String {
        return name
    }
}
