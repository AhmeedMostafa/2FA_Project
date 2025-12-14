package com.data

import com.model.Token
import kotlinx.coroutines.flow.Flow

class TokenRepository(
    private val tokenDao: TokenDao,
    private val cloudTokenService: CloudTokenService? = null,
    private val uidProvider: () -> String? = { null },
    private val isAnonymousProvider: () -> Boolean = { true }
) {

    // Helper to check if we should sync to cloud (only for non-anonymous users)
    private fun shouldSyncToCloud(): Boolean {
        return uidProvider() != null && !isAnonymousProvider()
    }

    // 1. Get all tokens (Used by Member 2 for the Main List)
    val allTokens: Flow<List<Token>> = tokenDao.getAllTokens()

    // 2. Add a new token (Used by Member 3 when scanning QR)
    suspend fun insert(token: Token) {
        tokenDao.insertToken(token)
        if (shouldSyncToCloud()) {
            val uid = uidProvider()!!
            cloudTokenService?.upsertToken(uid, token)
        }
    }

    suspend fun update(token: Token) {
        tokenDao.updateToken(token)
        if (shouldSyncToCloud()) {
            val uid = uidProvider()!!
            cloudTokenService?.upsertToken(uid, token)
        }
    }

    // 3. Delete (Used by Member 2 for Long-Press Delete)
    suspend fun delete(token: Token) {
        tokenDao.deleteToken(token)
        if (shouldSyncToCloud()) {
            val uid = uidProvider()!!
            cloudTokenService?.deleteToken(uid, token.remoteId)
        }
    }

    // Only sync from cloud for non-anonymous users
    suspend fun syncFromCloud(uid: String, isAnonymous: Boolean) {
        if (isAnonymous) return // Don't sync for guest users
        
        val remoteTokens = cloudTokenService?.fetchTokens(uid).orEmpty()
        for (remoteToken in remoteTokens) {
            val insertedId = tokenDao.insertTokenIgnore(remoteToken)
            if (insertedId == -1L) {
                tokenDao.updateByRemoteId(
                    remoteId = remoteToken.remoteId,
                    issuer = remoteToken.issuer,
                    accountName = remoteToken.accountName,
                    secretKey = remoteToken.secretKey,
                    algorithm = remoteToken.algorithm
                )
            }
        }
    }

    // Clear local tokens (useful when logging out)
    suspend fun clearLocalTokens() {
        tokenDao.deleteAllTokens()
    }
}