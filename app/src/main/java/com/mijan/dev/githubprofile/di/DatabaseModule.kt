package com.mijan.dev.githubprofile.di

import android.app.Application
import androidx.room.Room
import com.mijan.dev.githubprofile.Constants
import com.mijan.dev.githubprofile.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application) =
        Room.databaseBuilder(application, AppDatabase::class.java, Constants.DATABASE_NAME).build()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.getUserDao()
}