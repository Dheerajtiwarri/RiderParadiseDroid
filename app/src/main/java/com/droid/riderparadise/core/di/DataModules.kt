package com.droid.riderparadise.core.di

import android.content.Context
import androidx.room.Room
import com.droid.riderparadise.data.local.AppDatabase
import com.droid.riderparadise.data.local.GroupDao
import com.droid.riderparadise.data.local.MembershipDao
import com.droid.riderparadise.data.local.OtpDao
import com.droid.riderparadise.data.local.UserDao
import com.droid.riderparadise.data.repository.GroupRepositoryImpl
import com.droid.riderparadise.data.repository.OtpRepositoryImpl
import com.droid.riderparadise.data.repository.UserRepositoryImpl
import com.droid.riderparadise.domain.repository.GroupRepository
import com.droid.riderparadise.domain.repository.OtpRepository
import com.droid.riderparadise.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
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
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideOtpDao(db: AppDatabase): OtpDao = db.otpDao()
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideGroupDao(db: AppDatabase): GroupDao = db.groupDao()
    @Provides fun provideMembershipDao(db: AppDatabase): MembershipDao = db.membershipDao()
    @Provides fun provideRideDao(db: AppDatabase): com.droid.riderparadise.data.local.RideDao = db.rideDao()
    @Provides fun provideChatDao(db: AppDatabase): com.droid.riderparadise.data.local.ChatDao = db.chatDao()
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindOtpRepository(impl: OtpRepositoryImpl): OtpRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Binds
    @Singleton
    abstract fun bindRideRepository(impl: com.droid.riderparadise.data.repository.RideRepositoryImpl): com.droid.riderparadise.domain.repository.RideRepository

    @Binds
    @Singleton
    abstract fun bindRiderRepository(impl: com.droid.riderparadise.data.repository.RiderRepositoryImpl): com.droid.riderparadise.domain.repository.RiderRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(impl: com.droid.riderparadise.data.repository.FeedbackRepositoryImpl): com.droid.riderparadise.domain.repository.FeedbackRepository
}
