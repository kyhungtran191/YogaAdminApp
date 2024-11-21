package com.example.yogaadminapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaadminapp.databinding.ActivityEditClassInstanceBinding
import com.example.yogaadminapp.models.ClassInstance
import com.example.yogaadminapp.repository.CourseRepository
import com.example.yogaadminapp.utils.MatchDayOfWeekChecker
import com.example.yogaadminapp.utils.NetworkChecker
import java.util.Calendar

class EditClassInstanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditClassInstanceBinding
    private lateinit var courseRepository: CourseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditClassInstanceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val courseId = intent.getStringExtra("courseId")
        val classInstanceId = intent.getStringExtra("classInstanceId")
//        Listen on Back button
        binding.txtBack.setOnClickListener({
            val backIntent = Intent(this, CourseDetailActivity::class.java)
            backIntent.putExtra("courseId", courseId)
            this.startActivity(backIntent)
        })
//        End
        courseRepository = CourseRepository(this)
        if (courseId.isNullOrBlank() || classInstanceId.isNullOrBlank()) {
            Toast.makeText(this, "Course ID or class ID is missing", Toast.LENGTH_SHORT).show()
        } else {
            fetchClassInstanceDetail(classInstanceId)
            binding.classDate.setOnClickListener{
                val dayOfWeek = courseRepository.getCourseDetail(courseId)?.dayOfWeek
                if(!dayOfWeek.isNullOrBlank()){
                    handleShowDatePicker(dayOfWeek)
                }
            }
            binding.editBtn.setOnClickListener{
                handleUpdateClassInstance(classInstanceId,courseId)
            }
        }

        val listTeacher = resources.getStringArray(R.array.teachers)
        val adtTeacher = ArrayAdapter(this, android.R.layout.simple_spinner_item, listTeacher)
        binding.spTeacher.adapter = adtTeacher
    }

    fun handleShowDatePicker(dayOfWeek:String){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val courseId = intent.getStringExtra("courseId") ?: return
        val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            if (MatchDayOfWeekChecker.handleCheckIsMatchCourseDayOfWeek(this,selectedDate, dayOfWeek)) {
                if (MatchDayOfWeekChecker.isDateConflict(courseRepository,selectedDate, courseId)) {
                    Toast.makeText(this, "The selected date conflicts with an existing class!", Toast.LENGTH_SHORT).show()
                } else {
                    binding.classDate.text = selectedDate
                }
            } else {
                Toast.makeText(this, "Date selected not match with day of week of whole course!", Toast.LENGTH_SHORT).show()
            }
        }, year, month, day)

        val today = calendar.time
        dpd.datePicker.minDate = today.time
        dpd.show()
    }

    fun fetchClassInstanceDetail(classInstanceId: String) {
        val teachers = resources.getStringArray(R.array.teachers)
        val classInstance = courseRepository.getClassInstanceDetail(classInstanceId)
        if (classInstance != null) {
            val teacher = classInstance.teacherName
            val positionTeacher = teachers.indexOf(teacher)
            Toast.makeText(this, "${classInstance.date}", Toast.LENGTH_SHORT).show()
            binding.spTeacher.setSelection(positionTeacher)
            binding.classDate.setText(classInstance.date)
            binding.comments.setText(classInstance.additionalComments)
        }
    }

    fun handleUpdateClassInstance(classInstanceId: String, courseId: String) {
        if (binding.classDate.text.toString().isEmpty()) {
            binding.classDate.error = "Please select date occur!"
        }

        val updatedClassInstance = ClassInstance(
            classInstanceId,
            courseId,
            binding.classDate.text.toString(),
            binding.spTeacher.selectedItem.toString(),
            binding.comments.text.toString()
        )

        if (NetworkChecker.isOnline(this)) {
            courseRepository.updateClassInstanceSql(updatedClassInstance) { success, message ->
                if (success) {
                    Toast.makeText(this, "Update class instance ${courseId} successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Update class instance failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            NetworkChecker.showAlertNoNetwork(this)
        }
    }
}
