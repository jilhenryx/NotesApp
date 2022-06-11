package com.example.notesapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.NoteWithCategory
import com.example.notesapp.databinding.NoteListItemBinding
import com.example.notesapp.ui.NoteActivity
import com.example.notesapp.utility.NOTE_ID_INTENT_KEY
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class NoteListAdapter @Inject constructor(@ActivityContext private val context: Context) :
    ListAdapter<NoteWithCategory, NoteListAdapter.NotesViewHolder>(NOTES_COMPARATOR) {

    companion object {
        private val NOTES_COMPARATOR = object : DiffUtil.ItemCallback<NoteWithCategory>() {
            override fun areItemsTheSame(
                oldItem: NoteWithCategory,
                newItem: NoteWithCategory
            ): Boolean {
                return oldItem.note.id == newItem.note.id
            }

            override fun areContentsTheSame(
                oldItem: NoteWithCategory,
                newItem: NoteWithCategory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NoteListItemBinding.inflate(layoutInflater, parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val noteWithCategory = getItem(position)
        if (noteWithCategory != null) {
            holder.bindData(noteWithCategory)
            holder.setOnClickListener(noteWithCategory.note.id)
        }
    }

    inner class NotesViewHolder(binding: NoteListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val titleTextView = binding.textViewNoteListItemTitle
        private val bodyTextView = binding.textViewNoteListItemText
        private val category = binding.textViewNoteListItemCategory
        private val cardView = binding.cardNoteListItem

        internal fun bindData(noteWithCategory: NoteWithCategory) {
            cardView.strokeColor = noteWithCategory.colorRes
            titleTextView.text = noteWithCategory.note.title
            bodyTextView.text = noteWithCategory.note.text
            category.text = noteWithCategory.name
        }

        internal fun setOnClickListener(noteId: Long) {
            itemView.setOnClickListener {
                val intent = Intent(context, NoteActivity::class.java)
                intent.putExtra(NOTE_ID_INTENT_KEY, noteId)
                context.startActivity(intent)
            }
        }

    }
}