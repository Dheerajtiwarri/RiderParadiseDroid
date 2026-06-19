package com.droid.riderparadise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        OtpEntity::class,
        UserEntity::class,
        GroupEntity::class,
        MembershipEntity::class,
        RideEntity::class,
        ChatMessageEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun otpDao(): OtpDao
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun membershipDao(): MembershipDao
    abstract fun rideDao(): RideDao
    abstract fun chatDao(): ChatDao

    companion object {
        const val NAME = "riderparadise.db"
    }
}
