package kz.baymukach.canvasappmini

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CoursesFragment : Fragment(R.layout.fragment_courses) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCourses)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("courses").get()
            .addOnSuccessListener { documents ->
                val courseList = ArrayList<Course>()
                for (document in documents) {
                    val course = document.toObject(Course::class.java).apply {
                        id = document.id
                    }
                    courseList.add(course)
                }

                recyclerView.adapter = CoursesAdapter(courseList) { course ->
                    Toast.makeText(context, course.course_name, Toast.LENGTH_SHORT).show()

                    // Кейін ManageCourseActivity-ге көшу:
                    val intent = Intent(requireContext(), ManageCourseActivity::class.java).apply {
                        putExtra("courseId", course.id)
                        putExtra("courseName", course.course_name)
                    }
                    startActivity(intent)
                }

            }
            .addOnFailureListener {
                Toast.makeText(context, "Курстар жүктелмеді!", Toast.LENGTH_SHORT).show()
            }
    }
}
