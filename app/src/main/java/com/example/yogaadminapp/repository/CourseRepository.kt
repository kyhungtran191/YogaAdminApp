package com.example.yogaadminapp.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.core.database.getStringOrNull
import com.example.yogaadminapp.db.AdminDbHelper
import com.example.yogaadminapp.models.ClassInstance
import com.example.yogaadminapp.models.Course
import com.example.yogaadminapp.models.CourseClassData
import com.google.firebase.database.FirebaseDatabase

class CourseRepository (private val context: Context){
    private val dbHelper = AdminDbHelper(context)
    private val firebaseDb = FirebaseDatabase.getInstance().reference

    fun addNewCourse(course:Course, onSuccess: (Boolean,String?) -> Unit){
        val courseCollection = firebaseDb.child("courses").push()
        val courseId  = courseCollection.key
        if (courseId != null) {
            val courseWithId = course.copy(id = courseId)
            courseCollection.setValue(courseWithId)
                .addOnSuccessListener {
                    insertCourseSQLite(courseWithId)
                    onSuccess(true, null)
                }
                .addOnFailureListener { e ->
                    onSuccess(false, e.message)
                }
        }
    }

     fun addClassInstance(classInstance:ClassInstance, onSuccess: (Boolean, String?) -> Unit){
         val classFirebaseCollection = firebaseDb.child("classes").push()
         val classId = classFirebaseCollection.key
         if (classId != null) {
            val classWithId = classInstance.copy(id = classId)
             classFirebaseCollection.setValue(classWithId).addOnSuccessListener {
                 insertClassSQLite(classWithId)
                 onSuccess(true,null)
             }
                 .addOnFailureListener{ e->onSuccess(false,e.message) }
         }
     }


    fun updateCourse(course:Course, onSuccess: (Boolean,String?) -> Unit){
        val courseCollection = firebaseDb.child("courses").child(course.id)
        val courseFirebaseMapping = mapOf(
            "name" to course.name,
            "dayofWeek" to course.dayOfWeek,
            "timeOfCourse" to course.startTime,
            "duration" to course.duration,
            "price" to course.price,
            "capacity" to course.capacity,
            "type" to course.type,
            "description" to course.description
        )
        courseCollection.updateChildren(courseFirebaseMapping).addOnSuccessListener {
            onSuccess(true,null)
        } .addOnFailureListener { e ->
            onSuccess(false, e.message)
        }
    }

    fun updateClass(classInstance: ClassInstance,onSuccess: (Boolean, String?) -> Unit){
        val classCollection = firebaseDb.child("classes").child(classInstance.id)
        val classFirebaseMapping = mapOf(
            "courseId" to classInstance.courseId,
            "date" to classInstance.date,
            "teacher" to classInstance.teacherName,
            "additionalComments" to classInstance.additionalComments)
        classCollection.updateChildren(classFirebaseMapping).addOnSuccessListener {
            onSuccess(true,null)
        } .addOnFailureListener{e-> onSuccess(false,e.message)}
    }


    fun deleteCourse(course: Course, onSuccess: (Boolean, String?) -> Unit) {
        val courseRef = firebaseDb.child("courses").child(course.id)
        courseRef.removeValue()
            .addOnSuccessListener {
                deleteCourseSQLite(course.id)
                onSuccess(true, null)
            }
            .addOnFailureListener { e ->
                onSuccess(false, e.message)
            }
    }

    fun deleteClassInstance(classInstance: ClassInstance, onSuccess: (Boolean, String?) -> Unit) {
        val classInstanceCollection = firebaseDb.child("classes").child(classInstance.id)
        classInstanceCollection.removeValue()
            .addOnSuccessListener {
                deleteClassSQLite(classInstance.id)
                onSuccess(true, null)
            }
            .addOnFailureListener { e ->
                onSuccess(false, e.message)
            }
    }




    fun insertCourseSQLite(course: Course) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put("id", course.id)
        values.put("name", course.name)
        values.put("dayofWeek", course.dayOfWeek)
        values.put("timeOfCourse", course.startTime)
        values.put("duration", course.duration)
        values.put("price", course.price)
        values.put("capacity", course.capacity)
        values.put("type", course.type)
        values.put("description", course.description)
        db.insert("Course", null, values)
    }

    fun insertClassSQLite(classInstance: ClassInstance){
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put("id", classInstance.id)
        values.put("courseId", classInstance.courseId)
        values.put("date", classInstance.date)
        values.put("teacher",classInstance.teacherName)
        values.put("comments", classInstance.additionalComments)
        db.insert("ClassInstance", null, values)
    }

    fun getAllCourses(): List<Course> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Course", null)
        val courses = mutableListOf<Course>()
        if (cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndex("id")
            val nameColumnIndex = cursor.getColumnIndex("name")
            val dayOfWeekColumnIndex = cursor.getColumnIndex("dayOfWeek")
            val timeOfCourseColumnIndex = cursor.getColumnIndex("timeOfCourse")
            val capacityColumnIndex = cursor.getColumnIndex("capacity")
            val durationColumnIndex = cursor.getColumnIndex("duration")
            val priceColumnIndex = cursor.getColumnIndex("price")
            val typeColumnIndex = cursor.getColumnIndex("type")
            val descriptionColumnIndex = cursor.getColumnIndex("description")

            if (idColumnIndex >= 0 && nameColumnIndex >= 0) {
                do {
                    val id = cursor.getString(idColumnIndex)
                    val name = cursor.getString(nameColumnIndex)
                    val dayOfWeek = cursor.getString(dayOfWeekColumnIndex)
                    val timeOfCourse = cursor.getString(timeOfCourseColumnIndex)
                    val capacity = cursor.getInt(capacityColumnIndex)
                    val duration = cursor.getInt(durationColumnIndex)
                    val price = cursor.getDouble(priceColumnIndex)
                    val type = cursor.getString(typeColumnIndex)
                    val description = cursor.getString(descriptionColumnIndex)
                    val course = Course(id, name, dayOfWeek, timeOfCourse, duration, price, type, capacity,description)
                    courses.add(course)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return courses
    }

    fun getCourseDetail(courseId:String): Course?{
        val db = dbHelper.writableDatabase
        var course: Course? = null
        val cursor: Cursor = db.rawQuery("SELECT * FROM course WHERE id = ?", arrayOf(courseId))
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            val dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow("timeOfCourse"))
            course = Course(courseId, name, dayOfWeek, startTime, duration, price,type,capacity,description)
        }
        cursor.close()
        db.close()
        return course
    }

    fun getClassInstanceDetail(classInstanceId:String): ClassInstance?{
        val db = dbHelper.readableDatabase
        var classInstance : ClassInstance?=null
        val cursor: Cursor = db.rawQuery("SELECT * FROM ClassInstance WHERE id = ?", arrayOf(classInstanceId))
        if (cursor.moveToFirst()) {
            val courseId = cursor.getString(cursor.getColumnIndexOrThrow("courseId"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"))
            val comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"))
            classInstance = ClassInstance(classInstanceId,courseId,date,teacher,comments)
        }
        cursor.close()
        db.close()
        return classInstance
    }


    fun getAllClassInstanceFromCourse(courseId:String):List<ClassInstance>{
        val classList = mutableListOf<ClassInstance>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ClassInstance WHERE courseId = ?", arrayOf(courseId))
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex("id")
                val courseIdIndex = cursor.getColumnIndex("courseId")
                val dateIndex = cursor.getColumnIndex("date")
                val teacherIndex = cursor.getColumnIndex("teacher")
                val commentsIndex = cursor.getColumnIndex("comments")
                if (idIndex != -1 && courseIdIndex != -1 && dateIndex != -1 && teacherIndex != -1 && commentsIndex != -1) {
                    val id = cursor.getString(idIndex)
                    val courseId = cursor.getString(courseIdIndex)
                    val date = cursor.getString(dateIndex)
                    val teacher = cursor.getString(teacherIndex)
                    val comments = cursor.getString(commentsIndex)
                    val classInstance = ClassInstance(id, courseId, date, teacher, comments)
                    classList.add(classInstance)
                }
            }
            cursor.close()
            db.close()
        }
        return classList
    }

    fun updateCourseSQL(course: Course, onSuccess: (Boolean, String?) -> Unit){
        updateCourse(course, {success,message->
            if(success){
                val db = dbHelper.writableDatabase
                val values = ContentValues()
                values.put("id", course.id)
                values.put("name", course.name)
                values.put("dayofWeek", course.dayOfWeek)
                values.put("timeOfCourse", course.startTime)
                values.put("duration", course.duration)
                values.put("price", course.price)
                values.put("capacity", course.capacity)
                values.put("type", course.type)
                values.put("description", course.description)
                val newestCourse =  db.update("course", values, "id = ?", arrayOf(course.id))
                db.close()
                if (newestCourse > 0) {
                  onSuccess(true,null)
                } else {
                    onSuccess(false, "Failed to update SQLite")
                }
            }else{
                onSuccess(false, "Failed to update Firebase")
            }
        })
    }

    fun updateClassInstanceSql(classInstance: ClassInstance, onSuccess: (Boolean, String?) -> Unit){
        updateClass(classInstance, {success,message->
            if (success){
                val db = dbHelper.writableDatabase
                val values = ContentValues()
                values.put("id", classInstance.id)
                values.put("courseId'", classInstance.courseId)
                values.put("date", classInstance.date)
                values.put("teacher", classInstance.teacherName)
                values.put("comments", classInstance.additionalComments)
                val newestClassInstance = db.update("ClassInstance",values,"id = ?",arrayOf(classInstance.id))
                if (newestClassInstance > 0) {
                    onSuccess(true,null)
                } else {
                    onSuccess(false, "Failed to update SQLite")
                }
            }else{
                onSuccess(false, "Failed to update Firebase")
            }
        })
    }


    private fun deleteCourseSQLite(courseId: String) {
        val db = dbHelper.writableDatabase
        db.delete("course", "id = ?", arrayOf(courseId))
        db.close()
    }

    private fun deleteClassSQLite(classId:String){
        val db = dbHelper.writableDatabase
        db.delete("ClassInstance", "id = ?", arrayOf(classId))
        db.close()
    }

    fun searchCourses(query: String?): List<CourseClassData> {
        val db = dbHelper.readableDatabase
        val resultList = mutableListOf<CourseClassData>()
        val searchTerm = "%${query ?: ""}%"
        val sqlQuery = """
            SELECT 
                ClassInstance.id AS class_id,
                ClassInstance.courseId AS course_id,
                ClassInstance.date AS class_date,
                ClassInstance.teacher AS teacher_name,
                ClassInstance.comments AS additional_comments,
                Course.name AS course_name,
                Course.dayOfWeek AS course_day_of_week,
                Course.timeOfCourse AS course_start_time,
                Course.capacity AS course_capacity,
                Course.duration AS course_duration,
                Course.price AS course_price,
                Course.type AS course_type,
                Course.description AS course_description
            FROM 
                ClassInstance
            JOIN 
                Course
            ON 
                ClassInstance.courseId = Course.id
            WHERE 
                ClassInstance.teacher LIKE ? 
                OR Course.name LIKE ?
                OR Course.dayOfWeek LIKE ?
                OR ClassInstance.date LIKE ?
        """

        val cursor = db.rawQuery(sqlQuery, arrayOf(searchTerm, searchTerm, searchTerm, searchTerm))
        if (cursor.moveToFirst()) {
            do {
                val classId = cursor.getString(cursor.getColumnIndexOrThrow("class_id"))
                val courseId = cursor.getString(cursor.getColumnIndexOrThrow("course_id"))
                val courseName = cursor.getString(cursor.getColumnIndexOrThrow("course_name"))
                val dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("course_day_of_week"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("course_start_time"))
                val capacity = cursor.getInt(cursor.getColumnIndexOrThrow("course_capacity"))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("course_duration"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("course_price"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("course_type"))
                val description = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("course_description"))
                val classDate = cursor.getString(cursor.getColumnIndexOrThrow("class_date"))
                val teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))
                val additionalComments = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("additional_comments"))
                resultList.add(
                    CourseClassData(
                        classId = classId,
                        courseId = courseId,
                        courseName = courseName,
                        dayOfWeek = dayOfWeek,
                        startTime = startTime,
                        capacity = capacity,
                        duration = duration,
                        price = price,
                        type = type,
                        classDate = classDate,
                        teacherName = teacherName,
                        additionalComments = additionalComments,
                        courseDescription = description
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return resultList
    }
}