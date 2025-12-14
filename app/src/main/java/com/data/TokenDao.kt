package com.example.a2faproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
<<<<<<< Updated upstream
import com.example.authenticator.model.Token
=======
import androidx.room.Update
import com.model.Token
>>>>>>> Stashed changes
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM tokens")
    fun getAllTokens(): Flow<List<Token>> // Flow updates UI automatically!

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertToken(token: Token): Long

    // Used for cloud sync (remoteId is unique)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTokenIgnore(token: Token): Long

    @Update
    suspend fun updateToken(token: Token)

    @Query(
        """
        UPDATE tokens
        SET issuer = :issuer,
            accountName = :accountName,
            secretKey = :secretKey,
            algorithm = :algorithm
        WHERE remoteId = :remoteId
        """
    )
    suspend fun updateByRemoteId(
        remoteId: String,
        issuer: String,
        accountName: String,
        secretKey: String,
        algorithm: String
    )

    @Query("SELECT * FROM tokens WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): Token?

    @Query("DELETE FROM tokens WHERE remoteId = :remoteId")
    suspend fun deleteByRemoteId(remoteId: String)

    @Delete
    suspend fun deleteToken(token: Token)

    @Query("DELETE FROM tokens")
    suspend fun deleteAllTokens()
}