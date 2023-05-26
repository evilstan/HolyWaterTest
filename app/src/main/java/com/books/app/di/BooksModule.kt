package com.books.app.di

import android.content.Context
import com.books.app.data.repository.MainRepository
import com.books.app.data.repository.impl.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context): MainRepository = MainRepositoryImpl(context)
}