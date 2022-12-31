package com.tmp.thermaquil.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import org.json.JSONArray

@Entity(tableName = "submission")
data class SubmissionData(@PrimaryKey val id: Int, val timestamp: Long, val name: String, val status: Int, val list: ArrayList<StudyAssessment>)

data class StudyAssessment(val status: Int, val paintLevel: Int, val list: ArrayList<SymptomSeverity>)

data class SymptomSeverity(val name: String, val severity: Int)

class StudyAssessmentConverter {
    @TypeConverter
    fun fromStudyAssessments(cycles: ArrayList<StudyAssessment>): String {
        return Gson().toJson(cycles)
    }

    @TypeConverter
    fun toStudyAssessments(source: String): ArrayList<StudyAssessment> {
        val list = arrayListOf<StudyAssessment>()
        val json = JSONArray(source)
        for (i in 0 until json.length()){
            val o = json[i]
            list.add(Gson().fromJson<StudyAssessment>(o.toString(), StudyAssessment::class.java))
        }
        return list
    }
}

class SymptomSeverityConverter {
    @TypeConverter
    fun fromSymptomSeverity(cycles: ArrayList<SymptomSeverity>): String {
        return Gson().toJson(cycles)
    }

    @TypeConverter
    fun toSymptomSeverity(source: String): ArrayList<SymptomSeverity> {
        val list = arrayListOf<SymptomSeverity>()
        val json = JSONArray(source)
        for (i in 0 until json.length()){
            val o = json[i]
            list.add(Gson().fromJson<SymptomSeverity>(o.toString(), SymptomSeverity::class.java))
        }
        return list
    }
}