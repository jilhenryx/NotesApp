package com.example.notesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.R
import com.example.notesapp.data.Note
import com.example.notesapp.databinding.ActivityNoteMainBinding
import com.example.notesapp.helpers.undoredohelper.setUpWithEditTextFieldUndoRedoHelper
import com.example.notesapp.helpers.undoredohelper.setUpWithNavMenuStateController
import com.example.notesapp.utility.NOTE_ID_INTENT_KEY
import com.example.notesapp.viewmodels.NoteActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "NoteActivity"

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {
    private lateinit var textViewNoteDate: TextView
    private lateinit var autoCompleteNoteCategory: AutoCompleteTextView
    private lateinit var editTextNoteTitle: TextInputEditText
    private lateinit var editTextNoteText: TextInputEditText
    private lateinit var bottomNav: BottomNavigationView
    private var _binding: ActivityNoteMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNoteMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.allowEnterTransitionOverlap = true
        // Initializing Views
        val toolbar = binding.toolbarActivityNote
        textViewNoteDate = binding.activityNoteContent.textViewActivityNoteDate
        autoCompleteNoteCategory = binding.activityNoteContent.autocompleteActivityNoteCategory
        editTextNoteTitle = binding.activityNoteContent.editTextActivityNoteTitle
        editTextNoteText = binding.activityNoteContent.editTextActivityNoteText
        bottomNav = binding.bottomNavActivityNoteMain

        //Setting Up Toolbar
        setSupportActionBar(toolbar)

        // Check for Restored State or Intent
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: Restoring SavedInstanceSate")
            viewModel.originalStateRestored = true
            viewModel.restoreNoteActivityState()
        } else {
            // Accessing Intent
            viewModel.noteId = intent.getLongExtra(NOTE_ID_INTENT_KEY, -1)
            // Setting this field here instead of in the ViewModel because note id will be updated
            // on activity recreation but isNewNote should be updated in relation to
            // Note Id only when the activity is created newly.
            viewModel.isNewNote = (viewModel.noteId < 0)
        }
        title =
            if (viewModel.isNewNote) getString(R.string.activity_note_new_title)
            else getString(R.string.activity_note_title)
        initializeView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: Saving SavedInstanceSate")
        viewModel.saveNoteActivityState()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: Saving Note")
        saveNote()
        super.onPause()
    }

    private fun initializeView() {
        //Set up Category Dropdown List
        lifecycleScope.launchWhenCreated {
            val categories = viewModel.getNoteCategories()
            val listAdapter =
                ArrayAdapter(this@NoteActivity, android.R.layout.simple_list_item_1, categories)
            autoCompleteNoteCategory.setAdapter(listAdapter)

            getAndDisplayNote()
        }
        //Setting Up BottomNavigation with UndoRedoHelper Class
        val bottomNavController = bottomNav.setUpWithNavMenuStateController(
            undoAllMenuId = R.id.bottom_nav_menu_undo_all,
            undoMenuId = R.id.bottom_nav_menu_undo,
            redoAllMenuId = R.id.bottom_nav_menu_redo_all,
            redoMenuId = R.id.bottom_nav_menu_redo,
        )
        //Setting Up EditText Views with UndoRedoHelper Class
        editTextNoteTitle.setUpWithEditTextFieldUndoRedoHelper(
            this,
            bottomNavController,
            this
        )
        editTextNoteText.setUpWithEditTextFieldUndoRedoHelper(
            this,
            bottomNavController,
            this
        )
        //Hide Soft Keyboard if focus is on AutoCompleteView since it is being used as a spinner
        autoCompleteNoteCategory.setOnFocusChangeListener { view, hasFocus ->
            if (view != null && hasFocus) {
                val imm =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun storeOriginalValues() {
        if (!viewModel.originalStateRestored) viewModel.storeNoteOriginalValues()
    }

    private fun getAndDisplayNote() {
        lifecycleScope.launchWhenCreated {
            val selectedNote = viewModel.getNote()
            Log.d(TAG, "displayNote: called, Note ID = ${selectedNote.id}")
            displayNote(selectedNote)
            storeOriginalValues()
        }
    }

    private fun displayNote(note: Note) {
        if (viewModel.editModeActivated) enterEditMode() else exitEditMode()

        val categoryName = viewModel.getCategoryName(note.categoryId)
        autoCompleteNoteCategory.setText(categoryName, false)

        textViewNoteDate.text =
            if (viewModel.isNewNote) getString(R.string.tv_note_date_created, note.dateCreated)
            else getString(R.string.tv_note_date_modified, note.dateModified)

        editTextNoteTitle.apply {
            setText(note.title)
            setSelection(note.title.length)
        }
        editTextNoteText.apply {
            setText(note.text)
            setSelection(note.text.length)
        }
    }

    //Function called when ignoreChanges menu item is clicked
    private fun displayOriginalNote() {
        val categoryId = viewModel.originalNoteValues.getOrDefault(
            NoteActivityViewModel.NOTE_CATEGORY_ID_KEY,
            "0"
        ).toInt()
        val title = viewModel.originalNoteValues.getOrDefault(
            NoteActivityViewModel.NOTE_TITLE_KEY,
            ""
        )
        val text = viewModel.originalNoteValues.getOrDefault(
            NoteActivityViewModel.NOTE_TEXT_KEY,
            ""
        )

        val categoryName = viewModel.getCategoryName(categoryId)
        autoCompleteNoteCategory.setText(categoryName, false)

        editTextNoteTitle.apply {
            setText(title)
            setSelection(title.length)
        }
        editTextNoteText.apply {
            setText(text)
            setSelection(text.length)
        }
    }

    // Edit Mode Determines if Note Text Field should be enabled or not.
    private fun enterEditMode() {
        Log.d(TAG, "enterEditMode: called")
        viewModel.editModeActivated = true
        editTextNoteText.isFocusableInTouchMode = true
        editTextNoteText.isFocusable = true
        invalidateOptionsMenu()
    }

    private fun exitEditMode() {
        Log.d(TAG, "exitEditMode: Called")
        viewModel.editModeActivated = false
        editTextNoteText.isFocusableInTouchMode = false
        editTextNoteText.isFocusable = false
        editTextNoteText.clearFocus()
        invalidateOptionsMenu()
    }

    private fun saveNote() {
        Log.d(TAG, "saveNote: ")
        val noteValues = getEditTextsValues()
        viewModel.saveNoteMediator(noteValues)
    }

    //Get Values from TextFields
    private fun getEditTextsValues(): Map<String, String> {
        val categoryName = autoCompleteNoteCategory.text.toString()
        val categoryId =
            viewModel.categories.firstOrNull { it.name == categoryName }?.id ?: "0"
        return mapOf(
            NoteActivityViewModel.NOTE_CATEGORY_ID_KEY to categoryId.toString(),
            NoteActivityViewModel.NOTE_TITLE_KEY to editTextNoteTitle.text.toString(),
            NoteActivityViewModel.NOTE_TEXT_KEY to editTextNoteText.text.toString()
        )
    }

    private fun discardNoteChanges() {
        Log.d(TAG, "discardNoteChanges: Called")
        displayOriginalNote()
    }

    private fun deleteNote() {
        Log.d(TAG, "deleteNote: Called")
        viewModel.deleteNote()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_note_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (viewModel.editModeActivated) {
            menu?.findItem(R.id.activity_note_menu_read)?.isVisible = true
            menu?.findItem(R.id.activity_note_menu_edit)?.isVisible = false
        } else {
            menu?.findItem(R.id.activity_note_menu_read)?.isVisible = false
            menu?.findItem(R.id.activity_note_menu_edit)?.isVisible = true
        }
        if (viewModel.isNewNote) {
            menu?.findItem(R.id.activity_note_menu_delete)?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.activity_note_menu_read -> exitEditMode()
            R.id.activity_note_menu_edit -> enterEditMode()
            R.id.activity_note_menu_save -> {
                saveNote()
                //displayNote()
                viewModel.isNewNote = false
                invalidateOptionsMenu()
            }
            R.id.activity_note_menu_ignore -> discardNoteChanges()
            R.id.activity_note_menu_delete -> {
                deleteNote()
                finishAfterTransition()
            }
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}