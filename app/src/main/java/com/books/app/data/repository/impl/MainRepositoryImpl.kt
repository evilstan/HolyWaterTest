package com.books.app.data.repository.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.books.app.R
import com.books.app.data.model.Banner
import com.books.app.data.model.Book
import com.books.app.data.repository.MainRepository
import com.books.app.utils.isNetworkAvailable
import com.books.app.utils.registerNetworkCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    MainRepository {
    override val booksLD: LiveData<List<Book>> by lazy { mBooksLD }
    override val bannersLD: LiveData<List<Banner>> by lazy { mBannersLD }
    override val recommendationsLD: LiveData<List<Book>> by lazy { mRecommendationsLD }
    override val carouselLD: LiveData<List<Book>> by lazy { mCarouselLD }
    override val errorLD: LiveData<String?> by lazy { mErrorLD }

    private val mBooksLD = MutableLiveData<List<Book>>()
    private val mBannersLD = MutableLiveData<List<Banner>>()
    private val mRecommendationsLD = MutableLiveData<List<Book>>()
    private val mCarouselLD = MutableLiveData<List<Book>>()
    private val mErrorLD = MutableLiveData<String?>(null)

    private val remoteConfig = Firebase.remoteConfig

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            fetchMainData()
            fetchDetailsData()
        }
    }

    init {
        // fetch data when network become available and show error if unavailable
        registerNetworkCallback(context, networkCallback)
    }

    override fun fetchMainData() {
        if (!isNetworkAvailable(context)) {
            mErrorLD.postValue(context.getString(R.string.err_no_internet))
            return
        }
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                mErrorLD.postValue(context.getString(R.string.err_server))
                return@addOnCompleteListener
            }
            val jsonValue = remoteConfig.getString(DATA_KEY)
            val gson = Gson()
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val dataMap: Map<String, Any> = gson.fromJson(jsonValue, type)

            val books: List<Book> = gson.fromJson(
                gson.toJsonTree(dataMap[BOOKS_TYPE]).asJsonArray,
                object : TypeToken<List<Book>>() {}.type
            )
            val banners: List<Banner> = gson.fromJson(
                gson.toJsonTree(dataMap[BANNERS_TYPE]).asJsonArray,
                object : TypeToken<List<Banner>>() {}.type
            )
            val favorites: List<Int> = gson.fromJson(
                gson.toJsonTree(dataMap[FAVORITES_TYPE]).asJsonArray,
                object : TypeToken<List<Int>>() {}.type
            )

            mErrorLD.postValue(null)
            mBooksLD.postValue(books)
            mBannersLD.postValue(banners)
            mRecommendationsLD.postValue(books.filter { favorites.contains(it.id) })
            banners.forEach {
                Glide.with(context).load(it.cover)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE).preload()
            }
        }
    }

    override fun fetchDetailsData() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                mErrorLD.postValue(context.getString(R.string.err_server))
                return@addOnCompleteListener
            }
            val jsonValue = remoteConfig.getString(CAROUSEL_KEY)
            val gson = Gson()
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val dataMap: Map<String, Any> = gson.fromJson(jsonValue, type)

            val books: List<Book> = gson.fromJson(
                gson.toJsonTree(dataMap[BOOKS_TYPE]).asJsonArray,
                object : TypeToken<List<Book>>() {}.type
            )

            mErrorLD.postValue( null)
            mCarouselLD.postValue(books)
        }

    }

    companion object {
        const val DATA_KEY = "json_data"
        const val CAROUSEL_KEY = "details_carousel"
        const val BOOKS_TYPE = "books"
        const val BANNERS_TYPE = "top_banner_slides"
        const val FAVORITES_TYPE = "you_will_like_section"
    }
}