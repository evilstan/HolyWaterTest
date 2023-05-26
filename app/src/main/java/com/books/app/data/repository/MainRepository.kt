package com.books.app.data.repository

import androidx.lifecycle.LiveData
import com.books.app.data.model.Banner
import com.books.app.data.model.Book

interface MainRepository {
    val booksLD: LiveData<List<Book>>
    val bannersLD: LiveData<List<Banner>>
    val recommendationsLD: LiveData<List<Book>>
    val carouselLD: LiveData<List<Book>>
    val errorLD: LiveData<String?>
    fun fetchMainData()
    fun fetchDetailsData()
}