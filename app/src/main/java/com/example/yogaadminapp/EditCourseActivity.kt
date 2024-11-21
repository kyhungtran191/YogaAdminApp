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
import com.example.yogaadminapp.databinding.ActivityCourseDetailBinding
import com.example.yogaadminapp.databinding.ActivityEditCourseBinding
import com.example.yogaadminapp.models.Course
import com.example.yogaadminapp.repository.CourseRepository
import com.example.yogaadminapp.utils.NetworkChecker
import java.util.Calendar

class EditCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCourseBinding
    private lateinit var courseRepository: CourseRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCourseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        handleInitList()
        handleClickSetTime()
        val courseId = intent.getStringExtra("courseId")
        courseRepository = CourseRepository(this)
        if (courseId.isNullOrBlank()) {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show()
        } else {
            fetchCourseDetail(courseId.toString())
            handleOnClickUpdateButton(courseId)
        }
        binding.txtBack.setOnClickListener{
            handleClickBack()
        }

    }

    private fun handleInitList() {
        val listType = resources.getStringArray(R.array.categories)
        val listDays = resources.getStringArray(R.array.days)
//        val list
        val adtType = ArrayAdapter(this, android.R.layout.simple_spinner_item, listType)
        val adtDay = ArrayAdapter(this, android.R.layout.simple_spinner_item, listDays)
        binding.spCourseType.adapter = adtType
        binding.spDayOfWeek.adapter = adtDay
    }

    private fun handleClickSetTime() {
        binding.startTime.setOnClickListener{
            showTimePickerDialog(binding.startTime)
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

    private fun handleClickBack(){
        binding.txtBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun fetchCourseDetail(courseId: String){
        val daysOfWeek = resources.getStringArray(R.array.days)
        val courseTypeList = resources.getStringArray(R.array.categories)
           val  course = courseRepository.getCourseDetail(courseId)
            if(course!=null){
                binding.nameInput.setText(course.name)
                val dayOfWeek = course.dayOfWeek
                val positionDay = daysOfWeek.indexOf(dayOfWeek)
                val positionType = courseTypeList.indexOf(course.type)
                binding.spDayOfWeek.setSelection(positionDay)
                binding.spDayOfWeek.isEnabled = false
                binding.spCourseType.setSelection(positionType)
                binding.startTime.setText(course.startTime)
                binding.courseDuration.setText(course.duration.toString())
                binding.priceInput.setText(course.price.toString())
                binding.capacityInput.setText(course.capacity.toString())
                binding.descriptionInput.setText(course.description)
            }
    }
    fun handleOnClickUpdateButton(courseId:String){
        binding.updateBtn.setOnClickListener{
            val course = Course(
                id = courseId,
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
                courseRepository.updateCourseSQL(course){ success, message ->
                    if (success) {
                        Toast.makeText(this, "Update course succesfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Update course fail: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                NetworkChecker.showAlertNoNetwork(this)
            }

        }

    }
}