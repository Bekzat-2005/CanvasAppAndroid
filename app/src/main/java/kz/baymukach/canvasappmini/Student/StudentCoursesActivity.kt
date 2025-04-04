package kz.baymukach.canvasappmini.Student

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

class StudentCoursesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoursesAdapter
    private val courseList = mutableListOf<Course>()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_courses)

        recyclerView = findViewById(R.id.studentCoursesRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        adapter = CoursesAdapter(courseList) { course ->
            val intent = Intent(this, StudentTasksActivity::class.java)
            intent.putExtra("courseId", course.id)
            intent.putExtra("courseName", course.course_name)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        loadStudentCourses()
    }

    private fun loadStudentCourses() {
        val currentUserId = auth.currentUser?.uid ?: return

        firestore.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                courseList.clear()
                for (doc in result) {
                    val studentRef = firestore.collection("courses")
                        .document(doc.id)
                        .collection("students")
                        .document(currentUserId)

                    studentRef.get().addOnSuccessListener { studentDoc ->
                        if (studentDoc.exists()) {
                            val course = doc.toObject(Course::class.java)
                            course.id = doc.id
                            courseList.add(course)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }
}
