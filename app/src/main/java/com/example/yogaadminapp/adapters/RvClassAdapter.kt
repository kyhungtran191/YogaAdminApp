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
import com.example.yogaadminapp.EditClassInstanceActivity
import com.example.yogaadminapp.R
import com.example.yogaadminapp.models.ClassInstance

class RvClassAdapter (
    private val context: Context,
    private val classes: MutableList<ClassInstance>,
    private val onDeleteClass: (ClassInstance) -> Unit
) : RecyclerView.Adapter<RvClassAdapter.ClassViewHolder>() {
    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classDate: TextView = itemView.findViewById(R.id.txtDate)
        val classTeacher: TextView = itemView.findViewById(R.id.txtTeacher)
        val classComment:  TextView = itemView.findViewById(R.id.txtComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.class_item, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classInstance = classes[position]
        holder.classDate.text = classInstance.date
        holder.classTeacher.text = classInstance.teacherName
        holder.classComment.text = classInstance.additionalComments

//        Click detail class instance
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CourseDetailActivity::class.java)
            intent.putExtra("classInstanceId", classInstance.id)
            context.startActivity(intent)
        }
//        Click edit class instance
        holder.itemView.findViewById<View>(R.id.editClassBtn).setOnClickListener {
            val intent = Intent(context, EditClassInstanceActivity::class.java)
            intent.putExtra("classInstanceId", classInstance.id)
            intent.putExtra("courseId", classInstance.courseId)
            context.startActivity(intent)
        }

        holder.itemView.findViewById<View>(R.id.deleteClassBtn).setOnClickListener {
            onDeleteClass(classInstance)
        }
    }

    fun updateClasses(newClasses: List<ClassInstance>) {
            classes.clear()
            classes.addAll((newClasses))
            notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
     return  classes.size
    }
}