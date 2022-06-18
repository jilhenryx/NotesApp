package com.example.notesapp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val toolbarTitleBundleKey = "${TAG}.toolbarTitleBundleKey"
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: Toolbar
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate:")
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NotesAppGlobal_NotesAppBase_NotesApp)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.allowEnterTransitionOverlap = true
        //Set up toolbar
        toolbar = binding.toolbarActivityMain
        setSupportActionBar(toolbar)
        title = savedInstanceState?.getString(toolbarTitleBundleKey)
            ?: getString(R.string.fragment_note_list_title)

        // Setting Up Toolbar with NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.fragment_note_list, R.id.fragment_category_list))
        toolbar.setupWithNavController(navController, appBarConfiguration)

        initializeApp()
    }

    private fun initializeApp() {
        //Make DB requests
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(toolbarTitleBundleKey, toolbar.title.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //Retrieving notes each time the user returns to MainActivity
        viewModel.getNotes()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}