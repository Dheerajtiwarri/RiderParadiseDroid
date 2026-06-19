package com.droid.riderparadise.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "rides")
data class RideEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val groupName: String,
    val title: String,
    val whenLabel: String,
    val difficulty: String,
    val distanceKm: Int,
    val climbM: Int,
    val durationLabel: String,
    val leaderName: String,
    val startLocation: String,
    val status: String,
    val goingCount: Int,
    val maybeCount: Int,
    val myRsvp: String,
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val rideId: String,
    val senderName: String,
    val senderInitials: String,
    val colorHex: String,
    val text: String,
    val mine: Boolean,
    val kind: String,
    val createdAt: Long,
)

@Dao
interface RideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rides: List<RideEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(ride: RideEntity)

    @Query("SELECT * FROM rides ORDER BY status")
    fun observeAll(): Flow<List<RideEntity>>

    @Query("SELECT * FROM rides WHERE id = :id")
    fun observe(id: String): Flow<RideEntity?>

    @Query("SELECT * FROM rides WHERE id = :id")
    suspend fun get(id: String): RideEntity?

    @Query("SELECT COUNT(*) FROM rides")
    suspend fun count(): Int
}

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<ChatMessageEntity>)

    @Query("SELECT * FROM chat_messages WHERE rideId = :rideId ORDER BY createdAt ASC")
    fun observe(rideId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE rideId = :rideId")
    suspend fun count(rideId: String): Int
}
