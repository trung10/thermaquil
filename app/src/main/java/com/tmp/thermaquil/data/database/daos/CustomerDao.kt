package com.tmp.thermaquil.data.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.tmp.thermaquil.data.database.entities.CustomerEntity

@Dao
interface CustomerDao {

    @Query("select * from customer")
    fun getAll(): List<CustomerEntity>

}