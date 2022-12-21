package com.tmp.thermaquil.data.models

data class SubmissionData(val timestamp: Long, val name: String, val status: Int, val list: ArrayList<StudyAssessment>)

data class StudyAssessment(val status: Int, val paintLevel: Int, val list: ArrayList<SymptomSeverity>)

data class SymptomSeverity(val severity: Int)