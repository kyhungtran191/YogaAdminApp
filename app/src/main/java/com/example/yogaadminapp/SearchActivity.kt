package com.example.yogaadminapp

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView

import androidx.appcompat.app.AppCompatActivity
import com.example.yogaadminapp.adapters.SearchResultAdapter
import com.example.yogaadminapp.databinding.ActivitySearchBinding
import com.example.yogaadminapp.models.CourseClassData
import com.example.yogaadminapp.repository.CourseRepository

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    lateinit var courseRepository: CourseRepository
    lateinit var lvSearchAdt: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lvSearchAdt = SearchResultAdapter(this,mutableListOf())
        binding.lvSearchResult.adapter = lvSearchAdt
        courseRepository = CourseRepository(this)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredData = courseRepository.searchCourses(newText)
                lvSearchAdt.updateData(filteredData)
                return true
            }
        })
        binding.lvSearchResult.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = binding.lvSearchResult.getItemAtPosition(position) as CourseClassData

            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detail_class_instance, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.txtName).setText(clickedItem.courseName)
            dialogView.findViewById<TextView>(R.id.txtDate).setText(clickedItem.classDate)
            dialogView.findViewById<TextView>(R.id.txtComment).setText(clickedItem.additionalComments)
            dialogView.findViewById<TextView>(R.id.txtTeacher).setText(clickedItem.teacherName)
            dialogView.findViewById<TextView>(R.id.txtDayOfWeek).setText(clickedItem.dayOfWeek)
            dialogView.findViewById<TextView>(R.id.txtType).setText(clickedItem.type)
            dialogView.findViewById<TextView>(R.id.txtPrice).setText(clickedItem.price.toString())
            dialogView.findViewById<TextView>(R.id.txtCapacity).setText(clickedItem.capacity.toString())
            dialogView.findViewById<TextView>(R.id.txtDuration).setText(clickedItem.duration.toString())
            dialogView.findViewById<TextView>(R.id.txtDescription).setText(clickedItem.courseDescription)


            dialog.window?.setGravity(Gravity.CENTER)

            dialog.show()

            dialogView.findViewById<Button>(R.id.btnClose).setOnClickListener {
                dialog.dismiss()
            }

        }
    }



    override fun onResume() {
        super.onResume()
        val allData = courseRepository.searchCourses(null)
        lvSearchAdt.updateData(allData)
    }
}