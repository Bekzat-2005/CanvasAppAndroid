package kz.baymukach.canvasappmini

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnswerActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var courseId: String
    private lateinit var taskId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        val taskTitleText: TextView = findViewById(R.id.taskTitleText)
        val taskTitleText2: TextView = findViewById(R.id.taskTitleText2)
        val answerInput: EditText = findViewById(R.id.answerInput)
        val sendAnswerBtn: Button = findViewById(R.id.sendAnswerBtn)
        val gradeText: TextView = findViewById(R.id.gradeText)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        courseId = intent.getStringExtra("courseId") ?: return
        taskId = intent.getStringExtra("taskId") ?: return

        val studentId = auth.currentUser?.uid ?: return

        // Тапсырма атын көрсету
        firestore.collection("courses").document(courseId)
            .collection("tasks").document(taskId)
            .get()
            .addOnSuccessListener { doc ->
                taskTitleText.text = doc.getString("taskTitle") ?: "Тапсырма"
                taskTitleText2.text = doc.getString("taskDescription") ?: "Тапсырма"
            }

        // Бар жауапты шығару
        firestore.collection("courses").document(courseId)
            .collection("tasks").document(taskId)
            .collection("answers").document(studentId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    answerInput.setText(doc.getString("answerText"))
                    val grade = doc.getString("grade")
                    if (!grade.isNullOrEmpty()) {
                        gradeText.text = "Баға: $grade"
                        gradeText.visibility = TextView.VISIBLE
                    }
                }
            }

        // "Жіберу" батырмасы
        sendAnswerBtn.setOnClickListener {
            val answer = answerInput.text.toString().trim()
            if (answer.isEmpty()) {
                Toast.makeText(this, "Жауапты жазыңыз!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser ?: return@setOnClickListener

            // Студенттің атын Firestore-дан алу
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { userDoc ->
                    val studentName = userDoc.getString("display_name") ?: "Аты жоқ"

                    val answerData = hashMapOf(
                        "answerText" to answer,
                        "timestamp" to Timestamp.now(),
                        "studentName" to studentName
                    )

                    firestore.collection("courses").document(courseId)
                        .collection("tasks").document(taskId)
                        .collection("answers").document(currentUser.uid)
                        .set(answerData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Жауап жіберілді!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Қате: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }
}
