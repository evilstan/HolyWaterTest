package com.books.app.data.model

import com.google.gson.annotations.SerializedName

data class Banner(
    val id: Int,
    @SerializedName("book_id")
    val bookId: Int,
    val cover: String
)
