package com.books.app.ui.fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.books.app.data.model.Book
import com.books.app.databinding.ItemCategoryBinding


class CategoryAdapter() : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private lateinit var items: List<List<Book>>
    private lateinit var onClickListener: (Int) -> Unit

    constructor(items: List<Book>, onClickListener: (Int) -> Unit) : this() {
        this.onClickListener = onClickListener
        this.items = items.groupBy { it.genre }.values.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val books = items[position]
        holder.binding.apply {
            val adapter = BookAdapter(books) { onClickListener(it) }
            tvCategory.text = books.first().genre
            rvBooks.layoutManager =
                LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
            rvBooks.adapter = adapter
        }
    }

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : ViewHolder(binding.root)
}
