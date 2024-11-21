package com.example.yogaadminapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaadminapp.CourseDetailActivity
import com.example.yogaadminapp.EditCourseActivity
import com.example.yogaadminapp.R
import com.example.yogaadminapp.models.Course

class RvCourseAdapter(
    private val context: Context,
    private val courses: MutableList<Course>,
    private val onDeleteCourse: (Course) -> Unit
) : RecyclerView.Adapter<RvCourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.course_name)
        val courseCategory: TextView = itemView.findViewById(R.id.course_category)
        val coursePrice: TextView = itemView.findViewById(R.id.course_price)
        val courseDay: TextView = itemView.findViewById(R.id.course_day)
        val courseTime: TextView = itemView.findViewById(R.id.course_startTime)
        val courseDuration: TextView = itemView.findViewById(R.id.course_duration)
        val courseCapacity: TextView = itemView.findViewById(R.id.course_capacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.courseName.text =course.name
        holder.courseCategory.text = course.type
        holder.coursePrice.text = course.price.toString()
        holder.courseDay.text = course.dayOfWeek
        holder.courseTime.text ="${course.startTime} - "
        holder.courseCapacity.text = "Capacity: ${course.capacity}"
        holder.courseDuration.text = "${course.duration} mins"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CourseDetailActivity::class.java)
            intent.putExtra("courseId", course.id)
            context.startActivity(intent)
        }

        holder.itemView.findViewById<View>(R.id.editCourseBtn).setOnClickListener {
            val intent = Intent(context, EditCourseActivity::class.java)
            intent.putExtra("courseId", course.id)
            context.startActivity(intent)
        }

        holder.itemView.findViewById<View>(R.id.deleteCourseBtn).setOnClickListener {
            onDeleteCourse(course)
        }

    }

    fun updateCourses(newCourses: List<Course>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return courses.size
    }
}