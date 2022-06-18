package com.example.notesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.adapters.CategoryListAdapter
import com.example.notesapp.data.Category
import com.example.notesapp.databinding.ActivityMainContentBinding
import com.example.notesapp.interfaces.NotesAppClickListeners.OnCategoryClickListener
import com.example.notesapp.viewmodels.MainActivityViewModel

private const val TAG = "CategoryListFragment"

class CategoryListFragment : Fragment(), OnCategoryClickListener {
    private lateinit var binding: ActivityMainContentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
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
        binding = ActivityMainContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            val categories = viewModel.getCategories()
            recyclerView = binding.recyclerView
            categoryAdapter = CategoryListAdapter(categories, this@CategoryListFragment)
            gridLayoutManager = GridLayoutManager(requireActivity(), 2)
            displayCategories()
        }
    }

    private fun displayCategories() {
        Log.d(TAG, "displayCategories: ")
        if (recyclerView.layoutManager == null) recyclerView.layoutManager = gridLayoutManager
        if (recyclerView.adapter == null) recyclerView.adapter = categoryAdapter
    }

    override fun onCategoryClick(category: Category) {
        Log.d(TAG, "onCategoryClicked: ")
        viewModel.filterNotes(category.name)
        findNavController().navigate(R.id.action_fragment_category_list_to_fragment_note_list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_category_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.fragment_note_list) {
            findNavController().navigate(R.id.action_fragment_category_list_to_fragment_note_list)
            true
        } else false
    }

}