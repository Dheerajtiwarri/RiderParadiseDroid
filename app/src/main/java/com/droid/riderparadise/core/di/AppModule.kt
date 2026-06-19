package com.droid.riderparadise.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    /** Injectable time source; tests can supply a fixed lambda. */
    @Provides
    @Singleton
    fun provideClock(): () -> Long = { System.currentTimeMillis() }
}
