package com.example.authenticator.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val issuer: String,       // e.g. "Google"
    val accountName: String,  // e.g. "john@gmail.com"
    val secretKey: String,    // e.g. "JBSWY3DPEHPK3PXP"
    val algorithm: String = "SHA1"
)