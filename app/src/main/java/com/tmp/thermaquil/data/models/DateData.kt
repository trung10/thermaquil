package com.tmp.thermaquil.data.models

data class DateData(val timestamp: Int, val status: Int, val list: ArrayList<StudyAssessment>)

data class StudyAssessment(val status: Int, val paintLevel: Int, val list: ArrayList<SymptomSeverity>)

data class SymptomSeverity(val severity: Int)