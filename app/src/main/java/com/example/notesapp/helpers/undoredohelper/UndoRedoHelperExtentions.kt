package com.example.notesapp.helpers.undoredohelper

import android.content.Context
import android.widget.EditText
import androidx.annotation.MenuRes
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationBarView

/*
* Link EditText Type view with UndoRedo Helper Class
 */
internal fun EditText.setUpWithEditTextFieldUndoRedoHelper(
    lifecycleOwner: LifecycleOwner,
    undoRedoNavController: UndoRedoNavMenuController,
    context: Context
): UndoRedoTextHelper {
    return UndoRedoTextHelper(
        context,
        lifecycleOwner,
        this,
        undoRedoNavController,
    )
}

/*
* Link NavigationBarView Type view with UndoRedo Helper Class NavMenuController
 */
internal fun NavigationBarView.setUpWithNavMenuStateController(
    @MenuRes undoAllMenuId: Int, @MenuRes undoMenuId: Int,
    @MenuRes redoMenuId: Int, @MenuRes redoAllMenuId: Int,
): UndoRedoNavMenuController {
    return UndoRedoNavMenuController(
        undoAllMenuId, undoMenuId, redoMenuId, redoAllMenuId, this,
    )
}
