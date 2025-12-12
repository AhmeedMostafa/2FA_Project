package com.example.a2faproject.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.authenticator.model.Token

@Database(entities = [Token::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
}