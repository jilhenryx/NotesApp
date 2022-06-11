package com.example.notesapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.Category
import com.example.notesapp.databinding.CategoryListItemBinding
import com.example.notesapp.interfaces.OnCategoryClickedListener

class CategoryListAdapter(
    private val categories: List<Category>,
    private val categoryClickedListener: OnCategoryClickedListener
) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListAdapter.CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CategoryListItemBinding.inflate(layoutInflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListAdapter.CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bindData(category)
        holder.setOnCategoryClickListener(category)
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val titleTextView = binding.textViewCategoryListItemTitle
        private val imageView = binding.imageViewCategoryListItem
        private val cardView = binding.cardCategoryListItem

        internal fun bindData(category: Category) {
            val icon = AppCompatResources.getDrawable(itemView.context, category.drawableRes)
            cardView.strokeColor = category.colorRes
            titleTextView.text = category.name
            imageView.setImageDrawable(icon)
        }

        internal fun setOnCategoryClickListener(category: Category) {
            itemView.setOnClickListener {
                categoryClickedListener.onCategoryClicked(category)
            }
        }

    }
}