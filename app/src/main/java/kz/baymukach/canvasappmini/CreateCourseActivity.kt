package kz.baymukach.canvasappmini

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateCourseActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_course)

        firestore = FirebaseFirestore.getInstance()

        val courseNameEditText: EditText = findViewById(R.id.courseName)
        val courseDescriptionEditText: EditText = findViewById(R.id.courseDescription)
        val createCourseBtn: Button = findViewById(R.id.createCourseBtn)

        createCourseBtn.setOnClickListener {
            val courseName = courseNameEditText.text.toString().trim()
            val courseDescription = courseDescriptionEditText.text.toString().trim()

            if (courseName.isNotEmpty() && courseDescription.isNotEmpty()) {
                val courseData = hashMapOf(
                    "course_name" to courseName,
                    "course_description" to courseDescription,
                    "created_by" to FirebaseAuth.getInstance().currentUser?.uid
                )

                firestore.collection("courses")
                    .add(courseData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Course created successfully!", Toast.LENGTH_SHORT).show()
                        finish()  // Закрыть экран и вернуться в Admin Panel
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error creating course: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}