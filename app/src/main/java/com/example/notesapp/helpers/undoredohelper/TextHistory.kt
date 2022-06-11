package com.example.notesapp.helpers.undoredohelper

import android.util.Log
import javax.inject.Inject

private const val TAG = "TextHistory"

class TextHistory @Inject constructor() {
    private var history = mutableListOf<TextItem>()
    private var currentHistoryIndex = 0

    internal fun setIndexToMax() {
        currentHistoryIndex = history.size
    }

    internal fun setIndexToMin() {
        currentHistoryIndex = 0
    }

    internal fun add(item: TextItem) {
        if (currentHistoryIndex < history.size) {
            removeEntries(currentHistoryIndex)
        }
        Log.d(TAG, "add: Prev Pos = $currentHistoryIndex and ${history.size}")
        history.add(item)
        reinitializeHistoryIndex()
        Log.d(TAG, "add: Current Pos = $currentHistoryIndex and ${history.size}")
    }

    internal fun performUndo(
        oldText: String,
        isDeleting: Boolean,
        onComplete: (text: String, endIndex: Int, isSuccessful: Boolean) -> Unit
    ) {
        //Check that current history position is valid
        if (!isDeleting) --currentHistoryIndex

        if (history.size == 0 || currentHistoryIndex < 0 || currentHistoryIndex >= history.size) {
            Log.d(TAG, "performUndo: Failed")
            if (!isDeleting) setIndexToMin() else setIndexToMax()
            onComplete(oldText, oldText.length, false)
            return
        }

        //Get TextItem
        val item = history[currentHistoryIndex]
        val range = item.range

        try {
            Log.d(TAG, "performUndo: TextItem = $item ")
            val newText = oldText.replaceRange(range.start, range.end, "")
            onComplete(newText, range.start, true)
        } catch (e: Exception) {
            Log.d(TAG, "performUndo: Exception caught: ${e.message}")
            removeEntry(currentHistoryIndex)
            performUndo(oldText, isDeleting) { text, endIndex, isSuccessful ->
                onComplete(text, endIndex, isSuccessful)
            }
        }
        if (isDeleting) ++currentHistoryIndex
        Log.d(TAG, "performUndo: History Index = $currentHistoryIndex")
    }

    internal fun performRedo(
        oldText: String,
        isDeleting: Boolean,
        onComplete: (text: String, endIndex: Int, isSuccessful: Boolean) -> Unit
    ) {
        if (isDeleting) --currentHistoryIndex
        //Check that the current history position is valid
        if (history.size == 0 || currentHistoryIndex >= history.size || currentHistoryIndex < 0) {
            Log.d(TAG, "performRedo: Failed")
            if (!isDeleting) setIndexToMax() else setIndexToMin()
            onComplete(oldText, oldText.length, false)
            return
        }
        //Get TextItem
        val item = history[currentHistoryIndex]
        val itemIndex = item.index

        try {
            Log.d(TAG, "performRedo: TextItem = $item")
            val newText = oldText.replaceRange(itemIndex, itemIndex, item.text)
            onComplete(newText, item.range.end, true)
        } catch (e: Exception) {
            Log.d(TAG, "performRedo: Exception caught: ${e.message}")

            removeEntry(currentHistoryIndex)
            performRedo(oldText, isDeleting) { text, endIndex, isSuccessful ->
                onComplete(text, endIndex, isSuccessful)
            }
        }
        if (!isDeleting) ++currentHistoryIndex
        Log.d(TAG, "performRedo: History Index = $currentHistoryIndex")
    }

    private fun removeEntry(index: Int) {
        history.removeAt(index)
    }

    private fun removeEntries(startIndex: Int) {
        for (index in startIndex until history.size) {
            Log.d(TAG, "removeEntries: ")
            history.removeLastOrNull()
            reinitializeHistoryIndex()
        }
    }

    internal fun removeAllEntries() {
        history.clear()
    }

    private fun reinitializeHistoryIndex() {
        currentHistoryIndex = history.size
    }
}