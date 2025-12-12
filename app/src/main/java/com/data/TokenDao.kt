package com.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.model.Token
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM tokens")
    fun getAllTokens(): Flow<List<Token>> // Flow updates UI automatically!

    @Insert
    suspend fun insertToken(token: Token)

    @Delete
    suspend fun deleteToken(token: Token)
}