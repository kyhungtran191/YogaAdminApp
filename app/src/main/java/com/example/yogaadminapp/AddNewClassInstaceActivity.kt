package com.example.yogaadminapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yogaadminapp.databinding.ActivityAddNewClassInstanceBinding
import com.example.yogaadminapp.models.ClassInstance
import com.example.yogaadminapp.repository.CourseRepository
import com.example.yogaadminapp.utils.MatchDayOfWeekChecker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewClassInstaceActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddNewClassInstanceBinding
    private lateinit var courseRepository: CourseRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewClassInstanceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val courseId = intent.getStringExtra("courseId")
        val dayOfWeek = intent.getStringExtra("dayOfWeek")
        if(!courseId.isNullOrBlank() && !dayOfWeek.isNullOrBlank()){
            binding.classDate.setOnClickListener{
                handleShowDatePicker(dayOfWeek)
            }
            binding.addBtn.setOnClickListener{
                handleOnSubmitButton(courseId)
            }
        }

        binding.txtBack.setOnClickListener({
            val backIntent = Intent(this, CourseDetailActivity::class.java)
            backIntent.putExtra("courseId", courseId)
            this.startActivity(backIntent)
        })

        courseRepository = CourseRepository(this)
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

    fun handleOnSubmitButton(courseId:String){
        if(binding.classDate.text.toString().isEmpty()){
            binding.classDate.error = "Please select date occur!"
        }
        val newClass = ClassInstance("",courseId,binding.classDate.text.toString(),binding.spTeacher.selectedItem.toString(),binding.courseComments.text.toString())
        courseRepository.addClassInstance(newClass){success, errMsg ->
            if(success){
                Toast.makeText(this, "Add class instance successfully", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Error when add class instance ${errMsg}", Toast.LENGTH_SHORT).show()
            }
        }

    }

}