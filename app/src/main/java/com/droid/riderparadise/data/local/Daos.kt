package com.droid.riderparadise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OtpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(otp: OtpEntity)

    @Query("SELECT * FROM otp WHERE phone = :phone")
    suspend fun get(phone: String): OtpEntity?

    @Query("UPDATE otp SET attempts = :attempts WHERE phone = :phone")
    suspend fun updateAttempts(phone: String, attempts: Int)

    @Query("DELETE FROM otp WHERE phone = :phone")
    suspend fun delete(phone: String)
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    fun observe(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun get(id: String): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clear()
}

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(groups: List<GroupEntity>)

    @Query("SELECT * FROM groups")
    fun observeAll(): Flow<List<GroupEntity>>

    @Query(
        "SELECT g.* FROM groups g INNER JOIN memberships m ON g.id = m.groupId " +
            "WHERE m.userId = :userId AND m.status = 'JOINED'"
    )
    fun observeJoined(userId: String): Flow<List<GroupEntity>>
}

@Dao
interface MembershipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(membership: MembershipEntity)

    @Query("SELECT * FROM memberships WHERE userId = :userId")
    fun observeForUser(userId: String): Flow<List<MembershipEntity>>

    @Query("SELECT * FROM memberships WHERE userId = :userId AND groupId = :groupId")
    suspend fun get(userId: String, groupId: String): MembershipEntity?
}
