package com.books.app.ui.fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.books.app.R
import com.books.app.data.model.Book
import com.books.app.databinding.ItemBookBinding
import com.bumptech.glide.Glide

class BookAdapter(
    private val items: List<Book>,
    private val isBlackText: Boolean = false,
    private val onClickListener: (Int) -> Unit = {}
) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = items[position]
        holder.binding.apply {
            tvItemBookName.text = book.name
            if (isBlackText) tvItemBookName.setTextColor(root.context.getColor(R.color.black))
            Glide.with(ivItemBookCover)
                .load(book.coverUrl)
                .into(ivItemBookCover)
            root.setOnClickListener { onClickListener(book.id) }
        }
    }

    inner class BookViewHolder(val binding: ItemBookBinding) : ViewHolder(binding.root)
}
