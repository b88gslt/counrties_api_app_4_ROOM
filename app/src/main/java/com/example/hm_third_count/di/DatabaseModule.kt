package com.example.hm_third_count.di

import android.content.Context
import androidx.room.Room
import com.example.hm_third_count.data.local.AppDatabase
import com.example.hm_third_count.data.local.FavoriteDao
import com.example.hm_third_count.data.local.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "countries_db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
}
