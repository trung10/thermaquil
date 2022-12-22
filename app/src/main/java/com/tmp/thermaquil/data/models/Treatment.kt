package com.tmp.thermaquil.data.models

data class Treatment(var currentIndex: Int = 0, val cycles: ArrayList<Cycle>)

data class Cycle(val orderByCycle: Int,
                 val hotSetPont: Float,
                 val hotTime: Int,
                 val coldSetPont: Float,
                 val coldTime: Int)