package com.example.notesapp.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapters.NoteListAdapter
import com.example.notesapp.databinding.ActivityMainContentBinding
import com.example.notesapp.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "NoteListFragment"

@AndroidEntryPoint
class NoteListFragment : Fragment() {
    private var layoutManagerSpan = 2
    private var _binding: ActivityMainContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var notesAdapter: NoteListAdapter
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMainContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        //Setting Fab to Create New Note
        val fab = binding.fabActivityMain
        fab.visibility = View.VISIBLE
        fab.setOnClickListener {
            startActivity(Intent(requireActivity(), NoteActivity::class.java))
        }
        layoutManagerSpan =
            if (requireActivity().resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE
            ) 3
            else 2

        recyclerView = binding.recyclerView
//        notesAdapter = NoteListAdapter(requireActivity())
        staggeredLayoutManager =
            StaggeredGridLayoutManager(layoutManagerSpan, StaggeredGridLayoutManager.VERTICAL)
        displayNotes()
    }

    private fun displayNotes() {
        Log.d(TAG, "displayNotes: called")
        if (recyclerView.adapter == null) recyclerView.adapter = notesAdapter
        if (recyclerView.layoutManager == null) recyclerView.layoutManager = staggeredLayoutManager
        viewModel.displayNotes.observe(viewLifecycleOwner) { notes ->
            if (notes != null) {
                Log.d(TAG, "displayNotes: Updating Recycler Notes - ${notes.size}")
                notesAdapter.submitList(notes)
                notesAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_notes_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.fragment_note_list) {
            viewModel.filterNotes("")
            requireActivity().invalidateOptionsMenu()
        }
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Log.d(tag, "onPrepareOptionsMenu: ")
        menu.findItem(R.id.fragment_note_list).isVisible = viewModel.isNoteFiltered
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}