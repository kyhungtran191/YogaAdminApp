package com.example.yogaadminapp.models
data class CourseClassData(
    val classId: String,
    val courseId: String,
    val courseName: String,
    val dayOfWeek: String,
    val startTime: String,
    val duration: Int,
    val price: Double,
    val type: String,
    val capacity: Int,
    val classDate: String,
    val teacherName: String,
    val additionalComments: String?,
    val courseDescription: String?
)