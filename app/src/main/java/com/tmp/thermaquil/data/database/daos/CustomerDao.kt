package com.tmp.thermaquil.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tmp.thermaquil.data.database.entities.CustomerEntity
import com.tmp.thermaquil.data.models.SubmissionData
import com.tmp.thermaquil.data.models.Treatment

@Dao
interface CustomerDao {

    @Query("select * from treatment")
    fun getAllTreatment(): List<Treatment>

    @Query("select * from submission")
    fun getAllSubmission(): List<SubmissionData>

    @Insert
    fun addTreatment(vararg values: Treatment)

    @Delete
    fun deleteTreatment(value: Treatment)

    @Insert
    fun addSubmission(vararg values: SubmissionData)

    @Delete
    fun deleteSubmission(value: SubmissionData)

}