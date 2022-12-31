package com.tmp.thermaquil.data.models

object Data {
    val fakeTreaments = arrayListOf(SubmissionData(1, 21.2.toLong(), "December 24, 2022", 1, arrayListOf()))

    val fakeTreaments2 = arrayListOf(
        SubmissionData(1, 21.2.toLong(), "Submission 30 min", 1, arrayListOf()))



    val defaultCycle = Cycle(1, 120f, 2 * 60, 39f, 1 * 60)

    val defaultTreatment = Treatment(
        0,
        System.currentTimeMillis()/1000,
        0,
        arrayListOf(
        defaultCycle.copy(orderByCycle = 1)
    ))

    var event = EVENT.evNone

    val Warning_High = 150
    val Warning_Cold = 15
}

enum class EVENT(val state: Int) {
    evNone(0x00),
    evTimer(0x01),
    evReadyToRun(0x02),
    evTreatmentStart(0x03),
    evTreatmentPause(0x04),
    evTreatmentRemuse(0x05),
    evTreatmentEnd(0x06),
    evCurrentCycleSetPointChanged(0x07),
    evCurrentCycleTimeChanged(0x08),
    evSwitchToCold(0x09),
    evSwitchToHot(0x10),
    evCycleTimerReaches0(0x11)
}

/**
 * @param cm Command to SubCommand
 */
enum class COMMAND(val cm: Pair<Int, Int?>) {
    cmPrepare(0x01 to null),
    cmPrepareForFirst(0x02 to null),
    cmStart(0x03 to 0x01),
    cmPause(0x03 to 0x02),
    cmResume(0x03 to 0x03),
    cmEnd(0x03 to 0x04),
    cmSetTemp(0x04 to null),
    cmDuration(0x05 to null),
    cmPower(0x06 to null),
    cmClearAllEvent(0x07 to null),
    cmSwitch(0x08 to null),
    cmGetLog(0x09 to null),
    cmCancelGetLog(0x0A to null),
    cmRealTime(0x0B to null),
    cmReservoirFilling(0x0C to null),
    cm71(0x71 to null),
    cm72(0x72 to null),
    cm73(0x73 to null)
}

enum class SEND_STATE {
    SENDING,
    SUCCESS,
    FAIL
}