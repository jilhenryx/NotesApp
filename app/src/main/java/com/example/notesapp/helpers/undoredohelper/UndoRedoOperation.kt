package com.example.notesapp.helpers.undoredohelper

import android.util.Log
import javax.inject.Inject

private const val TAG = "UndoRedoOperation"

/*
* This class handles all the operations of the UndoRedoHelper and communicates with the
* Text History class that holds all the previous state of the textView
*/

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
    *   type: the amount of undo to be performed. It is either undo change once or undo all changes
    *   oldText: the current test in the EditText view
    *   isDeleting: boolean indicating if text view state is deleting (used only here when
    *               swapping operations)
    *   onComplete: callback after operation completes.
     */
    internal fun undo(
        type: EditTextUndoRedoType,
        oldText: String,
        isDeleting: Boolean = false,
        onComplete: (newText: String, index: Int, isSuccessful: Boolean, shouldDeactivate: Boolean) -> Unit
    ) {
        checkCurrentStoredString(oldText)

        //User is Deleting so operation are swapped
        if (userIsDeleting) {
            Log.d(TAG, "undo: User Is Deleting")
            userIsDeleting = false
            redo(type, oldText, true) { newText, index, isSucessful, shouldDeactivate ->
                userIsDeleting = true
                onComplete(newText, index, isSucessful, shouldDeactivate)
            }
            return
        }

        //Normal Undo Operation
        when (type) {
            EditTextUndoRedoType.ONCE -> {
                if (oldText == originalTextString && !isDeleting) {
                    onComplete(oldText, oldText.length, false, true)
                } else {
                    Log.d(TAG, "undo: Perform Undo Once")
                    textHistory.performUndo(
                        oldText,
                        isDeleting
                    ) { text, endIndex, isSuccessful, shouldDeactivate ->
                        onComplete(text, endIndex, isSuccessful, shouldDeactivate)
                    }
                }
            }
            EditTextUndoRedoType.ALL -> {
                textHistory.gotoStartNode()
                onComplete(originalTextString, originalTextString.length, true, true)
            }
        }
    }

    private fun checkCurrentStoredString(text: String) {
        if (currentStoredString.isBlank()) currentStoredString = text
    }

    /*
   * Perform Redo Operation
   * @params
   *   type: the amount of redo to be performed. It is either redo change once or redo all changes
   *   oldText: the current test in the EditText view
   *   isDeleting: boolean indicating if text view state is deleting (used only here when
   *               swapping operations)
   *   onComplete: callback after operation completes.
    */
    internal fun redo(
        type: EditTextUndoRedoType,
        oldText: String,
        isDeleting: Boolean = false,
        onComplete: (newText: String, index: Int, isSuccessful: Boolean, shouldDeactivate: Boolean) -> Unit
    ) {
        //User is Deleting so operation are swapped
        if (userIsDeleting) {
            userIsDeleting = false
            Log.d(TAG, "redo: User is Deleting")
            undo(type, oldText, true) { newText, index, isSuccessful, shouldDeactivate ->
                userIsDeleting = true
                onComplete(newText, index, isSuccessful, shouldDeactivate)
            }
            return
        }
        //Normal Redo Operation
        when (type) {
            EditTextUndoRedoType.ONCE -> {
                if (oldText == currentStoredString && !isDeleting) {
                    onComplete(oldText, oldText.length, false, true)
                } else {
                    Log.d(TAG, "redo: Performing Redo Once")
                    textHistory.performRedo(
                        oldText,
                        isDeleting
                    ) { text, endIndex, isSuccessful, shouldDeactivate ->
                        onComplete(text, endIndex, isSuccessful, shouldDeactivate)
                    }
                }
            }
            EditTextUndoRedoType.ALL -> {
                textHistory.gotoEndNode()
                onComplete(currentStoredString, currentStoredString.length, true, true)
            }
        }
    }

    internal fun clearTextHistory() {
        textHistory.clearTextHistory()
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