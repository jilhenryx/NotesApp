package com.example.notesapp.helpers.undoredohelper

data class TextItem(
    internal var text: String,
    internal var range: IntRange,
)