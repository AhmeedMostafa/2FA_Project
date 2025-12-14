package com.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.model.Token
import kotlinx.coroutines.tasks.await

class CloudTokenService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun tokensCollection(uid: String) =
        firestore.collection("users").document(uid).collection("tokens")

    suspend fun upsertToken(uid: String, token: Token) {
        val docId = token.remoteId
        if (docId.isBlank()) return

        val data = mapOf(
            "remoteId" to token.remoteId,
            "issuer" to token.issuer,
            "accountName" to token.accountName,
            "secretKey" to token.secretKey,
            "algorithm" to token.algorithm
        )

        tokensCollection(uid)
            .document(docId)
            .set(data, SetOptions.merge())
            .await()
    }

    suspend fun deleteToken(uid: String, remoteId: String) {
        if (remoteId.isBlank()) return
        tokensCollection(uid).document(remoteId).delete().await()
    }

    suspend fun fetchTokens(uid: String): List<Token> {
        val snap = tokensCollection(uid).get().await()
        return snap.documents.mapNotNull { doc ->
            val remoteId = doc.getString("remoteId") ?: doc.id
            val issuer = doc.getString("issuer") ?: return@mapNotNull null
            val accountName = doc.getString("accountName") ?: return@mapNotNull null
            val secretKey = doc.getString("secretKey") ?: return@mapNotNull null
            val algorithm = doc.getString("algorithm") ?: "SHA1"

            Token(
                id = 0,
                remoteId = remoteId,
                issuer = issuer,
                accountName = accountName,
                secretKey = secretKey,
                algorithm = algorithm
            )
        }
    }
}


