package com.example.notesapp.helpers.undoredohelper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.core.view.isVisible
import com.google.android.material.navigation.NavigationBarView

private const val TAG = "UndoRedoNavMenuController"

class UndoRedoNavMenuController(
    @MenuRes private val undoAllMenuId: Int,
    @MenuRes private val undoMenuId: Int,
    @MenuRes private val redoMenuId: Int,
    @MenuRes private val redoAllMenuId: Int,
    private var navView: NavigationBarView?,
) : NavigationBarView.OnItemSelectedListener {

    private var onEditTextUndoRedoListener: OnEditTextUndoRedoListener? = null
    private var undoState = MenuState.UNDO_STATE
    private var redoState = MenuState.REDO_STATE

    init {
        navView?.setOnItemSelectedListener(this)
        updateNavMenu()
    }

    internal fun attachOnEditTextUndoRedoListener(listener: OnEditTextUndoRedoListener) {
        onEditTextUndoRedoListener = listener
    }

    internal fun updateNavMenuState() {
        Log.d(TAG, "updateNavMenuState: ")
        if (!getUndoState()) {
            setUndoState(true)
        }
        if (getRedoState()) {
            setRedoState(false)
        }
        updateNavMenu()
    }

    internal fun setNavViewVisibility(isVisible: Boolean) {
        //Naive Way to Animate Nav Menu
        if (navView == null) return
        if (isVisible) {
            navView!!.alpha = 0.0f
            navView!!.isVisible = isVisible
            navView!!.animate()
                .alpha(1.0f)
                .setDuration(350)
                .start()
        } else {
            if (navView!!.visibility == View.GONE) return
            navView!!.animate()
                .alpha(0.0f)
                .setDuration(350)
                .setListener(object : AnimatorListenerAdapter() {
                    private var isCancelled = false
                    override fun onAnimationCancel(animation: Animator?) {
                        super.onAnimationCancel(animation)
                        isCancelled = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        if (navView!!.alpha == 0.0f && !isCancelled) {
                            navView!!.isVisible = isVisible
                        }
                    }
                })
                .start()
        }
    }

    internal fun resetNavMenuState() {
        resetMenuState()
    }

    private fun updateNavMenu() {
        navView?.menu?.findItem(undoAllMenuId)?.isEnabled = getUndoState()
        navView?.menu?.findItem(undoMenuId)?.isEnabled = getUndoState()
        navView?.menu?.findItem(redoMenuId)?.isEnabled = getRedoState()
        navView?.menu?.findItem(redoAllMenuId)?.isEnabled = getRedoState()
    }

    private fun updateNavMenuOnItemPressed(
        itemType: NavItemType,
        undoRedoType: EditTextUndoRedoType
    ) {
        when (itemType) {
            NavItemType.UNDO -> {
                setRedoState(true)
                updateNavMenu()
                onEditTextUndoRedoListener?.undoChanges(undoRedoType)
            }
            NavItemType.REDO -> {
                setUndoState(true)
                updateNavMenu()
                onEditTextUndoRedoListener?.redoChanges(undoRedoType)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            undoAllMenuId -> {
                setUndoState(false)
                updateNavMenuOnItemPressed(NavItemType.UNDO, EditTextUndoRedoType.ALL)
            }
            undoMenuId -> {
                updateNavMenuOnItemPressed(NavItemType.UNDO, EditTextUndoRedoType.ONCE)
            }
            redoMenuId -> {
                updateNavMenuOnItemPressed(NavItemType.REDO, EditTextUndoRedoType.ONCE)
            }
            redoAllMenuId -> {
                setRedoState(false)
                updateNavMenuOnItemPressed(NavItemType.REDO, EditTextUndoRedoType.ALL)
            }
        }
        return true
    }

    internal fun setUndoState(isEnabled: Boolean) {
        undoState.isEnabled = isEnabled
    }

    private fun getUndoState(): Boolean = undoState.isEnabled

    internal fun setRedoState(isEnabled: Boolean) {
        redoState.isEnabled = isEnabled
    }

    private fun getRedoState(): Boolean = redoState.isEnabled

    private fun resetMenuState() {
        setRedoState(false)
        setUndoState(false)
        updateNavMenu()
    }


    internal fun clearAllRef() {
        navView = null
        setUndoState(false)
        setRedoState(false)
    }

    enum class NavItemType {
        UNDO,
        REDO
    }

    enum class MenuState(var isEnabled: Boolean) {
        UNDO_STATE(false),
        REDO_STATE(false),
    }
}