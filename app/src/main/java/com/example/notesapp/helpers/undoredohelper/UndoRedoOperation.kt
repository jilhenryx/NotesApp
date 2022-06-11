package com.example.notesapp.helpers.undoredohelper

import android.util.Log
import javax.inject.Inject

private const val TAG = "UndoRedoOperation"

class UndoRedoOperation @Inject constructor(private val textHistory: TextHistory) {
    /* Field to hold most recent string before first undo phase for redoAll Operation
    * This field is reset each time focus changes or user types with keyboard
    */
    internal var currentStoredString = ""

    /* Field to hold original string for undoAll Operation
    * This field is reset each time focus changes
     */
    internal var originalTextString = ""

    /*
    * Field to signal if user is deleting from the text field
    * If true, undo and redo operations are swapped
    */
    internal var userIsDeleting = false

    /*
    * Perform Undo Operation
    * @params
    *   type: the amount of undo to be performed. It is either undo change once or all changes
    *   oldText: the current test in the EditText view
    *   onUndoDone: callback after operation completes. The callback doesn't fire ir return value
    *               is false
    * @return
    *   Boolean indicating if operation was successful or not
     */
    internal fun undo(
        type: EditTextUndoRedoType,
        oldText: String,
        isDeleting: Boolean = false,
        onComplete: (newText: String, index: Int) -> Unit
    ): Boolean {
        checkCurrentStoredString(oldText)

        //User is Deleting so operation are swapped
        if (userIsDeleting) {
            Log.d(TAG, "undo: User Is Deleting")
            userIsDeleting = false
            return redo(type, oldText, true) { newText, index ->
                userIsDeleting = true
                onComplete(newText, index)
            }
        }

        //Normal Undo Operation
        return when (type) {
            EditTextUndoRedoType.ONCE -> {
                if (oldText == originalTextString && !isDeleting) {
                    false
                } else {
                    Log.d(TAG, "undo: Perform Undo Once")
                    var isSuccess = false
                    textHistory.performUndo(
                        oldText,
                        isDeleting
                    ) { text, endIndex, isSuccessful ->
                        if (isSuccessful) {
                            onComplete(text, endIndex)
                            isSuccess = true
                        }
                    }
                    isSuccess
                }
            }
            EditTextUndoRedoType.ALL -> {
                textHistory.setIndexToMin()
                onComplete(originalTextString, originalTextString.length)
                true
            }
        }
    }

    private fun checkCurrentStoredString(text: String) {
        if (currentStoredString.isBlank()) currentStoredString = text
    }

    /*
    * Perform Redo Operation
    * @params
    *   type: the amount of redo to be performed. It is either redo change once or all changes
    *   oldText: the current test in the EditText view
    *   onRedoDone: callback after operation completes. The callback doesn't fire ir return value
    *               is false
    * @return
    *   Boolean indicating if operation was successful or not
     */
    internal fun redo(
        type: EditTextUndoRedoType,
        oldText: String,
        isDeleting: Boolean = false,
        onComplete: (newText: String, index: Int) -> Unit
    ): Boolean {
        //User is Deleting so operation are swapped
        if (userIsDeleting) {
            userIsDeleting = false
            Log.d(TAG, "redo: User is Deleting")
            return undo(type, oldText, true) { newText, index ->
                userIsDeleting = true
                onComplete(newText, index)
            }
        }
        //Normal Redo Operation
        return when (type) {
            EditTextUndoRedoType.ONCE -> {
                if (oldText == currentStoredString && !isDeleting) {
                    false
                } else {
                    Log.d(TAG, "redo: Performing Redo Once")
                    var isSuccess = false
                    textHistory.performRedo(
                        oldText,
                        isDeleting
                    ) { text, endIndex, isSuccessful ->
                        if (isSuccessful) {
                            onComplete(text, endIndex)
                            isSuccess = true
                        }
                    }
                    isSuccess
                }
            }
            EditTextUndoRedoType.ALL -> {
                textHistory.setIndexToMax()
                onComplete(currentStoredString, currentStoredString.length)
                true
            }
        }
    }

    internal fun removeAllHistoryItems() {
        textHistory.removeAllEntries()
    }

    internal fun addTextItem(item: TextItem) {
        textHistory.add(item)
    }

}

/*
* Class that highlights the type of UNDO REDO operation
* ONCE - undo or redo a single change
* ALL - undo or redo all changes
 */
enum class EditTextUndoRedoType {
    ONCE,
    ALL
}