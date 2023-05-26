package com.books.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.books.app.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository): ViewModel() {
    val booksLD get() = repository.booksLD
    val bannersLD get() = repository.bannersLD
    val favoritesLD get() = repository.recommendationsLD
    val carouselLD get() = repository.carouselLD
    val errorLD get() = repository.errorLD


    fun fetchMainData() = repository.fetchMainData()

    fun fetchDetailsData() = repository.fetchDetailsData()
}