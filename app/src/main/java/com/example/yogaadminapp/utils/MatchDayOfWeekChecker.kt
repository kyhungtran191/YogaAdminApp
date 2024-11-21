package com.example.yogaadminapp.utils

import android.content.Context
import com.example.yogaadminapp.R
import com.example.yogaadminapp.repository.CourseRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MatchDayOfWeekChecker {
    fun handleCheckIsMatchCourseDayOfWeek(context: Context, datePick: String, dayOfWeek: String): Boolean {
        val calendarHandler = Calendar.getInstance()
        calendarHandler.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datePick)!!

        val selectedDayOfWeek = calendarHandler.get(Calendar.DAY_OF_WEEK)


        val weekDays = context.resources.getStringArray(R.array.days)

        val dayIndex = weekDays.indexOf(dayOfWeek)

        return selectedDayOfWeek == dayIndex + 1
    }
    fun isDateConflict(courseRepository:CourseRepository,selectedDate: String, courseId: String): Boolean {
        val classInstances = courseRepository.getAllClassInstanceFromCourse(courseId)
        for (classInstance in classInstances) {
            if (classInstance.date == selectedDate) {
                return true
            }
        }
        return false
    }
}