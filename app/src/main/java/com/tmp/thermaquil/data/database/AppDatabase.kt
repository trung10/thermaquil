package com.tmp.thermaquil.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tmp.thermaquil.data.database.daos.CustomerDao
import com.tmp.thermaquil.data.database.entities.CustomerEntity

@Database(entities = [CustomerEntity::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun customerDao(): CustomerDao
}