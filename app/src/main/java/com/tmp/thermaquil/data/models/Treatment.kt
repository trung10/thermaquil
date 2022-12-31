package com.tmp.thermaquil.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import org.json.JSONArray
import java.sql.Timestamp

@Entity(tableName = "treatment")
data class Treatment(@PrimaryKey val id: Int, val timestamp: Long, var currentIndex: Int = 0, val cycles: ArrayList<Cycle>)

data class Cycle(val orderByCycle: Int,
                 val hotSetPont: Float,
                 val hotTime: Int,
                 val coldSetPont: Float,
                 val coldTime: Int)

class CyclesTypeConverter {
    @TypeConverter
    fun fromCycles(cycles: ArrayList<Cycle>): String {
        return Gson().toJson(cycles)
    }

    @TypeConverter
    fun toCycles(source: String): ArrayList<Cycle> {
        val list = arrayListOf<Cycle>()
        val json = JSONArray(source)
        for (i in 0 until json.length()){
            val o = json[i]
            list.add(Gson().fromJson<Cycle>(o.toString(), Cycle::class.java))
        }
        return list
    }
}