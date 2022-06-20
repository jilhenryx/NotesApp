package com.example.notesapp.helpers.undoredohelper

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

/*
* Helper class that monitors changes to Edit Text Field and stores words as TextItem
* It is lifecycle aware.
* It uses a NavMenuController class to signal what operation to perform. Communication with
* controller is via OnEditTextUndoRedoListener interface
* The state is not stored
* It implements TextWatcher and OnFocusChangeListener to monitor changes
*/

private const val TAG = "UndoRedoTextHelper"

class UndoRedoTextHelper(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val textView: EditText,
    private val undoRedoNavMenuController: UndoRedoNavMenuController,
) :
    TextWatcher,
    View.OnFocusChangeListener, OnEditTextUndoRedoListener,
    DefaultLifecycleObserver {

    private var isNewHelper = true

    /* Handles all Undo/Redo operations. Serves as an intermediary between this class
    * and the other helper classes
    */
    private val undoRedoOperation: UndoRedoOperation

    // Field to keep track of the currently focused editText
    private var focusedEditText: EditText? = null

    /* Field to track if text change was by the undo/redo operation. If change was by undo/redo
    * TextWatcher callback methods are ignored to prevent to prevent error
    */
    private var changeByUndoRedo = false

    /*
    * @Field stores the start index of the word that makes up a TextItem
    */
    private var startIndex = -1

    /*
    * @Field stores the end index of the word that makes up a TextItem
    */
    private var endIndex = 0

    /*
    * @Field stores the word that makes up a TextItem
    */
    private val wordBuffer = mutableListOf<Char>()

    private var currentText: CharSequence? = null

    /*
    * @Field specifies if the word in @field wordBuffer has been saved in TextHistory
    */
    private var hasEntryBeenSaved = false

    //Initialize Listeners and Register Observable
    init {
        textView.onFocusChangeListener = this
        textView.addTextChangedListener(this)
        lifecycleOwner.lifecycle.addObserver(this)
        val hiltEntryPoint = EntryPointAccessors.fromActivity(
            context as Activity,
            EditTextFieldUndoRedoHelperEntryPoint::class.java
        )
        undoRedoOperation = hiltEntryPoint.undoRedoOperation()
    }

    //==============================================================================================
    /*
    * TextWatcher Callback Methods to track changes in Edit Text Field
    * Changes tracked is on a per word basis using the @param Start in the callbacks to monitor the
    * start of a new word or deletion
    */

    /*
    * Tracks Deletion Changes
    */
    override fun beforeTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        // Log.d(TAG, "beforeTextChanged: $text , $start, $before, $count")

        //Do not track changes by Undo or Redo Operations to avoid errors
        if (changeByUndoRedo || text.isNullOrBlank()) return

        val currentCursorPos = start + count
        when {
            // User has started deleting
            (before > count && !undoRedoOperation.userIsDeleting) -> {
                Log.d(TAG, "beforeTextChanged: User is Deleting")
                storeWord(text)
                startIndex = currentCursorPos + 1
                undoRedoOperation.userIsDeleting = true
                undoRedoOperation.clearTextHistory()
            }
            // User is no longer deleting so reset all watchers
            (before < count && undoRedoOperation.userIsDeleting) -> {
                Log.d(TAG, "beforeTextChanged: User is no longer Deleting")
                undoRedoOperation.userIsDeleting = false
                resetWatchers()
            }
        }

        if (undoRedoOperation.userIsDeleting) {
            if (hasEntryBeenSaved) hasEntryBeenSaved = false
            endIndex = currentCursorPos
            if (endIndex < text.length) {
                wordBuffer.add(text[endIndex])
            }
        }
        undoRedoNavMenuController.updateNavMenuState()
    }

    /*
    * Tracks Insertion Changes.
    * Loosely when @param start changes, it signifies the start of a new word and we can
    * store the old one using the lastStartIndex to the current start
    */
    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d(TAG, "onTextChanged: $text, $start, $before, $count")

        // Do not track changes by Undo or Redo Operations to avoid errors
        // Also, delete changes are not tracked here but in beforeTextChanged
        if (changeByUndoRedo || undoRedoOperation.userIsDeleting || text.isNullOrBlank() || isNewHelper) {
            if (isNewHelper) isNewHelper = false
            return
        }

        // Reset stored string used for redoAll as soon as user types
        if (undoRedoOperation.currentStoredString.isNotBlank())
            undoRedoOperation.currentStoredString = ""

        // Reset as soon as user types
        if (hasEntryBeenSaved) hasEntryBeenSaved = false

        trackChanges(start, before, count, text)

        //Update Variables as soon as TextChanges from user but only once
        undoRedoNavMenuController.updateNavMenuState()
    }

    override fun afterTextChanged(text: Editable?) {}
//==================================================================================================

    private fun trackChanges(
        textStartIndex: Int,
        textBeforeIndex: Int,
        count: Int,
        text: CharSequence
    ) {
        //Prev Word saved or cursor position has changed so update watchers
        val currentCursorPos = textStartIndex + textBeforeIndex
        if (startIndex < 0 || endIndex != currentCursorPos) {
            //Save changes to track text updates
            if (!hasEntryBeenSaved) {
                Log.d(TAG, "trackChanges: Saving Changes and Update StartIndex")
                storeWord(text)
            }

            //Update watchers
            startIndex = if (textStartIndex == 0) textStartIndex else currentCursorPos
            //endIndex = currentCursorPos
        }
        endIndex = textStartIndex + count - 1
        Log.d(TAG, "trackChanges: endIndex = $endIndex")
        //Monitor each character update
        currentText = text
        if (endIndex < text.length) {
            wordBuffer.add(text[endIndex])
            //If whitespace is encountered, word is complete. Save word
            if (text[endIndex].isWhitespace()) {
                Log.d(TAG, "trackChanges: Storing from Whitespace")
                ++endIndex
                storeWord(text)
                --endIndex
            }
        }
        ++endIndex
    }

    private fun storeWord(text: CharSequence) {
        if (startIndex < 0 || endIndex < 0) return

        wordBuffer.clear()
        try {
            wordBuffer.addAll(text.subSequence(startIndex, endIndex).toList())
        } catch (e: Exception) {
            Log.d(TAG, "storeWord: Exception encountered: ${e.message}")
            return
        }
        saveEntryAndResetWatchers()
    }

    private fun saveEntryAndResetWatchers() {
        saveEntry()
        resetWatchers()
    }

    private fun saveEntry() {
        if (wordBuffer.isEmpty()) return
        var end = if (startIndex == endIndex) ++endIndex else endIndex

        if (undoRedoOperation.userIsDeleting) {
            wordBuffer.reverse()
            val temp = startIndex
            startIndex = end
            end = temp
        }
        val textItem =
            TextItem(
                text = wordBuffer.joinToString(""),
                range = IntRange(start = startIndex, endInclusive = end),
            )
        Log.d(TAG, "saveEntry: TextItem = $textItem")
        undoRedoOperation.addTextItem(textItem)
        hasEntryBeenSaved = true
    }

    private fun resetWatchers() {
        startIndex = -1
        endIndex = 0
        wordBuffer.clear()
    }

    /*
    * Callback to monitor change in focus of EditText Fields
     */
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view == null) return
        //Reset all states including NavMenuController states when Focus Leaves EditText
        if (!hasFocus) {
            resetWatchers()
            with(undoRedoOperation) {
                currentStoredString = ""
                clearTextHistory()
            }
            with(undoRedoNavMenuController) {
                resetNavMenuState()
                setNavViewVisibility(false)
            }
        }

        //Update State and set OriginalTextString for Undo All Operation
        if (view is EditText && hasFocus) {
            focusedEditText = view
            undoRedoOperation.originalTextString = focusedEditText?.text.toString().trimEnd()

            with(undoRedoNavMenuController) {
                attachOnEditTextUndoRedoListener(this@UndoRedoTextHelper)
                setNavViewVisibility(true)
            }
        }
    }
//==================================================================================================


    /*
    * Callback method for when user taps an undo or a redo button
    * @param amount
    *           Specifies type of operation - ONCE or ALL
    * undo/redo(..) method comes from UndoRedoOperations class
     */
    override fun undoChanges(amount: EditTextUndoRedoType) {
        //Get current text string from Edit Text
        val oldText = focusedEditText?.text.toString()

        if (!hasEntryBeenSaved) {
            if (!undoRedoOperation.userIsDeleting && currentText != null)
                storeWord(currentText!!)
            else saveEntryAndResetWatchers()
        }

        undoRedoOperation.undo(amount, oldText) { newText, index, isSuccess, shouldDeactivate ->
            if (isSuccess) {
                //Log.d(TAG, "undoChanges: Updating Text Field")
                setFocusedEditTextField(newText, index)
            }
            if (shouldDeactivate) {
                Log.d(TAG, "undoChanges: Deactivating Undo Menu")
                undoRedoNavMenuController.setUndoState(false)
                undoRedoNavMenuController.updateNavMenu()
            }
        }
    }

    override fun redoChanges(amount: EditTextUndoRedoType) {
        //Get current text string from Edit Text
        val oldText = focusedEditText?.text.toString()

        undoRedoOperation.redo(amount, oldText) { newText, index, isSuccess, shouldDeactivate ->
            if (isSuccess) {
                //Log.d(TAG, "redoChanges: Updating Text Field")
                setFocusedEditTextField(newText, index)
            }
            if (shouldDeactivate) {
                Log.d(TAG, "redoChanges: Deactivating Redo Menu")
                undoRedoNavMenuController.setRedoState(false)
                undoRedoNavMenuController.updateNavMenu()
            }
        }
    }

    /*
    * Update text in Focused EditText
    * @param text
    *           New text string after undo/redo operation
     */
    private fun setFocusedEditTextField(text: String, index: Int) {
        changeByUndoRedo = true
        focusedEditText?.apply {
            setText(text)
            setSelection(index)
        }
        changeByUndoRedo = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "onDestroy: ")
        clearAllRef()
        super.onDestroy(owner)
    }

    /*
    * Remove all reference to Views and detach listeners to prevent memory leaks
    * Call NavMenuController's clearRef method as well
    */
    private fun clearAllRef() {
        focusedEditText = null
        textView.removeTextChangedListener(this)
        undoRedoNavMenuController.clearAllRef()
        undoRedoOperation.clearTextHistory()
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface EditTextFieldUndoRedoHelperEntryPoint {
        fun undoRedoOperation(): UndoRedoOperation
    }
}

//Interface class to listen for NavMenu Events
interface OnEditTextUndoRedoListener {
    fun undoChanges(amount: EditTextUndoRedoType)
    fun redoChanges(amount: EditTextUndoRedoType)
}