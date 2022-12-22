package com.tmp.thermaquil.data.models

object Data {
    val defaultCycle = Cycle(1, 120f, 45 * 60, 39f, 15 * 60)

    val defaultTreatment = Treatment(
        0,
        arrayListOf(
        defaultCycle.copy(orderByCycle = 1),
        defaultCycle.copy(orderByCycle = 2),
        defaultCycle.copy(orderByCycle = 3),
        defaultCycle.copy(orderByCycle = 4),
        defaultCycle.copy(orderByCycle = 5)
    ))
}