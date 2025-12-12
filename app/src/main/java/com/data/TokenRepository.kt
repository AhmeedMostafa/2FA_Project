package com.data

import com.model.Token
import kotlinx.coroutines.flow.Flow

class TokenRepository(private val tokenDao: TokenDao) {

    // 1. Get all tokens (Used by Member 2 for the Main List)
    val allTokens: Flow<List<Token>> = tokenDao.getAllTokens()

    // 2. Add a new token (Used by Member 3 when scanning QR)
    suspend fun insert(token: Token) {
        tokenDao.insertToken(token)
    }

    // 3. Delete (Used by Member 2 for Long-Press Delete)
    suspend fun delete(token: Token) {
        tokenDao.deleteToken(token)
    }
}