package com.tmp.thermaquil.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tmp.thermaquil.data.database.daos.CustomerDao
import com.tmp.thermaquil.data.database.entities.CustomerEntity
import com.tmp.thermaquil.data.models.*

@Database(entities = [CustomerEntity::class, SubmissionData::class, Treatment::class],version = 1)
@TypeConverters(CyclesTypeConverter::class, StudyAssessmentConverter::class, SymptomSeverityConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun customerDao(): CustomerDao
}