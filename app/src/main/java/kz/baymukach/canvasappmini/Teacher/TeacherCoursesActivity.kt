package kz.baymukach.canvasappmini.Teacher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kz.baymukach.canvasappmini.Admin.CoursesAdapter
import kz.baymukach.canvasappmini.Course
import kz.baymukach.canvasappmini.R

class TeacherCoursesActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoursesAdapter
    private val courseList = mutableListOf<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_courses)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.teacherCoursesRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CoursesAdapter(courseList) { course ->
            val intent = Intent(this, ManageTasksActivity::class.java)
            intent.putExtra("courseId", course.id)
            intent.putExtra("courseName", course.course_name)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        loadTeacherCourses()
    }

    private fun loadTeacherCourses() {
        val currentUserId = auth.currentUser?.uid ?: return

        firestore.collection("courses")
            .whereArrayContains("teacherIds", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                courseList.clear()
                for (doc in documents) {
                    val course = doc.toObject(Course::class.java)
                    course.id = doc.id
                    courseList.add(course)
                }
                adapter.notifyDataSetChanged()
            }
    }
}
