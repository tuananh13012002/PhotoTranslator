package com.image.photo.translator.di

import android.content.Context
import android.content.SharedPreferences
import com.image.photo.translator.common.AppSharePreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(AppSharePreference.APP_SHARE_KEY, Context.MODE_PRIVATE)
    }

}