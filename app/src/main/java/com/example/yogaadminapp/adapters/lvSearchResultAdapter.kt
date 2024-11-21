package com.example.yogaadminapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.yogaadminapp.R
import com.example.yogaadminapp.models.CourseClassData

class SearchResultAdapter(private val context: Context, private val courseClassList: MutableList<CourseClassData>) : BaseAdapter() {
    override fun getCount(): Int = courseClassList.size

    override fun getItem(position: Int): CourseClassData = courseClassList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)

        val name = view.findViewById<TextView>(R.id.txtName)
        val date = view.findViewById<TextView>(R.id.txtDate)
        val teacher = view.findViewById<TextView>(R.id.txtTeacher)
        val dayOfWeek = view.findViewById<TextView>(R.id.txtDayOfWeek)
        val type = view.findViewById<TextView>(R.id.txtType)

        val item = getItem(position)

        name.text = item.courseName
        date.text = item.classDate
        teacher.text = item.teacherName
        dayOfWeek.text = item.dayOfWeek
        type.text = item.type
        return view
    }

    fun updateData(newData: List<CourseClassData>) {
        courseClassList.clear()
        courseClassList.addAll(newData)
        notifyDataSetChanged()
    }
}