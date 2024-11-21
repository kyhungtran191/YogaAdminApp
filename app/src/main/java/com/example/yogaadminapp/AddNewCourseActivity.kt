package com.example.yogaadminapp

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yogaadminapp.databinding.ActivityAddNewCourseBinding
import com.example.yogaadminapp.databinding.ActivityMainBinding
import com.example.yogaadminapp.models.Course
import com.example.yogaadminapp.repository.CourseRepository
import com.example.yogaadminapp.utils.NetworkChecker
import java.util.Calendar

class AddNewCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewCourseBinding
    private lateinit var courseRepository: CourseRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCourseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        handleClickBack()
        handleInitList()
        handleClickAddButton()
        handleClickSetTime()
        courseRepository = CourseRepository(this)
    }

    private fun handleClickSetTime() {
        binding.startTime.setOnClickListener{
            showTimePickerDialog(binding.startTime)
        }
    }

    private fun handleClickBack(){
        binding.txtBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showTimePickerDialog(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            timeTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            binding.startTime.error = null
        }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun handleInitList(){
        val listType = resources.getStringArray(R.array.categories)
        val listDays = resources.getStringArray(R.array.days)
//        val list
        val adtType = ArrayAdapter(this, android.R.layout.simple_spinner_item, listType)
        val adtDay = ArrayAdapter(this, android.R.layout.simple_spinner_item, listDays)
        binding.spCourseType.adapter = adtType
        binding.spDayOfWeek.adapter = adtDay
    }
    private fun handleClickAddButton(){
        binding.addBtn.setOnClickListener{
            if(binding.nameInput.text.toString().isEmpty()){
                binding.nameInput.error = "Please enter course name"
                return@setOnClickListener
            }
            if(binding.courseDuration.text.toString().isEmpty()){
                binding.courseDuration.error = "Please enter course duration"
                return@setOnClickListener
            }
            if(binding.courseDuration.text.toString().toInt() < 0){
                binding.courseDuration.error = "Please enter a valid duration"
                return@setOnClickListener
            }
            if(binding.priceInput.text.toString().isEmpty()){
                binding.priceInput.error = "Please enter course price"
                return@setOnClickListener
            }
            if(binding.capacityInput.text.toString().isEmpty()){
                binding.capacityInput.error = "Please enter start time"
                return@setOnClickListener
            }

            if(binding.startTime.text.toString().isEmpty()){
                binding.startTime.error = "Please enter start time"
                return@setOnClickListener
            }

            val course = Course(
                id = "",
                name = binding.nameInput.text.toString(),
                dayOfWeek = binding.spDayOfWeek.selectedItem.toString(),
                startTime = binding.startTime.text.toString(),
                duration = binding.courseDuration.text.toString().toInt(),
                price = binding.priceInput.text.toString().toDouble(),
                type = binding.spCourseType.selectedItem.toString(),
                capacity = binding.capacityInput.text.toString().toInt(),
                description = binding.descriptionInput.text.toString()
            )
            if(NetworkChecker.isOnline(this)){
                courseRepository.addNewCourse(course,{ success, errMsg ->
                    if (success) {
                        Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error when add course ${errMsg}", Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                NetworkChecker.showAlertNoNetwork(this)
            }
        }
    }
}