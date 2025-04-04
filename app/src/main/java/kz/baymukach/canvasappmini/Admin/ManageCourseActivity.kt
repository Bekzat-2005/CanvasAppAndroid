package kz.baymukach.canvasappmini.Admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kz.baymukach.canvasappmini.R

class ManageCourseActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var courseId: String
    private lateinit var courseName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_course)

        firestore = FirebaseFirestore.getInstance()

        val courseTitle: TextView = findViewById(R.id.courseTitle)
        val userEmailEditText: EditText = findViewById(R.id.userEmailEditText)
        val addTeacherBtn: Button = findViewById(R.id.addTeacherBtn)
        val addStudentBtn: Button = findViewById(R.id.addStudentBtn)

        courseId = intent.getStringExtra("courseId").toString()
        courseName = intent.getStringExtra("courseName").toString()
        courseTitle.text = courseName

        addTeacherBtn.setOnClickListener {
            val email = userEmailEditText.text.toString().trim()
            addTeacherToCourse(email)
        }

        addStudentBtn.setOnClickListener {
            val email = userEmailEditText.text.toString().trim()
            addStudentToCourse(email)
        }
    }

    private fun addTeacherToCourse(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Email енгізіңіз!", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val userId = userDoc.id
                    val userRole = userDoc.getString("role") ?: ""

                    if (userRole != "teacher") {
                        Toast.makeText(this, "Бұл қолданушы мұғалім емес!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val name = userDoc.getString("display_name") ?: "Name not found"
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email
                    )

                    firestore.collection("courses").document(courseId)
                        .collection("teachers")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            firestore.collection("courses").document(courseId)
                                .update("teacherIds", FieldValue.arrayUnion(userId))
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Мұғалім қосылды!", Toast.LENGTH_SHORT).show()
                                }
                        }

                } else {
                    Toast.makeText(this, "Қолданушы табылмады!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Қате: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addStudentToCourse(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Email енгізіңіз!", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val userId = userDoc.id
                    val userRole = userDoc.getString("role") ?: ""

                    if (userRole != "student") {
                        Toast.makeText(this, "Бұл қолданушы студент емес!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val name = userDoc.getString("display_name") ?: "Name not found"
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email
                    )

                    firestore.collection("courses").document(courseId)
                        .collection("students")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Студент қосылды!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Қате: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(this, "Қолданушы табылмады!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Қате: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}