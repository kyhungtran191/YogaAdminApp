package com.example.yogaadminapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yogaadminapp.adapters.RvClassAdapter
import com.example.yogaadminapp.adapters.RvCourseAdapter
import com.example.yogaadminapp.databinding.ActivityAddNewCourseBinding
import com.example.yogaadminapp.databinding.ActivityCourseDetailBinding
import com.example.yogaadminapp.models.ClassInstance
import com.example.yogaadminapp.models.Course
import com.example.yogaadminapp.repository.CourseRepository

class CourseDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityCourseDetailBinding
    private lateinit var courseRepository: CourseRepository
    private lateinit var adapterClass: RvClassAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val courseId = intent.getStringExtra("courseId")
        courseRepository = CourseRepository(this)
        if (courseId.isNullOrBlank()) {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show()
        } else {
            fetchCourseDetail(courseId)
//            handleOnClickUpdateButton(courseId)
        }
        setupRecyclerView()
        updateClassList()
        binding.addNewClassInstance.setOnClickListener {
            val intent = Intent(this, AddNewClassInstaceActivity::class.java)
            val dayOfWeek = binding.txtDayOfWeek.text.toString()
            intent.putExtra("dayOfWeek", dayOfWeek)
            intent.putExtra("courseId", courseId)
            startActivity(intent)
        }
    }

    fun fetchCourseDetail(courseId:String){
        val  course = courseRepository.getCourseDetail(courseId)
        if(course!=null){
            binding.txtTitle.setText(course.name)
            binding.txtDescription.setText(course.description)
            binding.txtPrice.setText("${course.price}$")
            binding.txtType.setText(course.type)
            binding.txtDayOfWeek.setText(course.dayOfWeek)
            binding.txtStartTime.setText("- Start at: ${course.startTime}")
            binding.txtDuration.setText("- Duration: ${course.duration} minutes")
        }
    }

    private fun onClickDeleteDialog(classInstance: ClassInstance) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this class instance? This action cannot be undo.")
            .setPositiveButton("Delete") { _, _ -> handleDeleteClass(classInstance) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupRecyclerView() {
        adapterClass = RvClassAdapter(this, mutableListOf()) { classInstance ->
            onClickDeleteDialog(classInstance)
        }
        binding.rvClasses.layoutManager = LinearLayoutManager(this)
        binding.rvClasses.adapter = adapterClass
    }

    private fun handleDeleteClass(classInstance: ClassInstance){
        courseRepository.deleteClassInstance(classInstance) { success, message ->
            if (success) {
                Toast.makeText(this, "Deleted class instance successfully !", Toast.LENGTH_SHORT).show()
                updateClassList()
            } else {
                Toast.makeText(this, "Error deleting class instance: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateClassList() {
        val courseId = intent.getStringExtra("courseId")
        if(courseId!=null){
            val listClass: List<ClassInstance> = courseRepository.getAllClassInstanceFromCourse(courseId)
            adapterClass.updateClasses(listClass)
        }
    }

    override fun onResume() {
        super.onResume()
        updateClassList()
    }

}