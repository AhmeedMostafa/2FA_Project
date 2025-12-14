package com.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tokens",
    indices = [
        Index(value = ["remoteId"], unique = true)
    ]
)
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String, // Stable ID for cloud sync (Firestore doc id)
    val issuer: String,       // e.g. "Google"
    val accountName: String,  // e.g. "john@gmail.com"
    val secretKey: String,    // e.g. "JBSWY3DPEHPK3PXP"
    val algorithm: String = "SHA1"
)
