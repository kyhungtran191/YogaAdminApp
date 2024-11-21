package com.example.yogaadminapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yogaadminapp.adapters.RvCourseAdapter
import com.example.yogaadminapp.databinding.ActivityMainBinding
import com.example.yogaadminapp.models.ClassInstance
import com.example.yogaadminapp.models.Course
import com.example.yogaadminapp.repository.CourseRepository
import com.example.yogaadminapp.utils.NetworkChecker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var courseRepository: CourseRepository
    private lateinit var adapterCourse: RvCourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        courseRepository = CourseRepository(this)
        setupRecyclerView()
        handleClickAddNewButton()
        updateCourseList()
        binding.search.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupRecyclerView() {
        adapterCourse = RvCourseAdapter(this, mutableListOf()) { course ->
            onClickDeleteDialog(course)
        }
        binding.rvCourses.layoutManager = LinearLayoutManager(this)
        binding.rvCourses.adapter = adapterCourse
    }

    private fun updateCourseList() {
        val listCourse: List<Course> = courseRepository.getAllCourses()
        adapterCourse.updateCourses(listCourse)
    }

    private fun handleClickAddNewButton() {
        binding.addNewCourse.setOnClickListener {
            val intent = Intent(this, AddNewCourseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickDeleteDialog(course: Course) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete the course \"${course.name}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->

                handleDeleteCourse(course)

            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleDeleteCourse(course: Course) {
        if(NetworkChecker.isOnline(this)){
            //        Logic check if have any class instance
            val classInstanceList: List<ClassInstance> = courseRepository.getAllClassInstanceFromCourse(course.id)
            if(classInstanceList.size > 0){
                    Toast.makeText(this, "Please remove all class instance to delete this course", Toast.LENGTH_SHORT).show()
            }else{
                courseRepository.deleteCourse(course) { success, message ->
                    if (success) {
                        Toast.makeText(this, "Deleted course: ${course.name}", Toast.LENGTH_SHORT).show()
                        updateCourseList()
                    } else {
                        Toast.makeText(this, "Error deleting course: ${course.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            NetworkChecker.showAlertNoNetwork(this)
        }

    }


    override fun onResume() {
        super.onResume()
        updateCourseList()
    }
}
