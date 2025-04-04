package kz.baymukach.canvasappmini.Teacher

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kz.baymukach.canvasappmini.Answer.AnswersAdapter
import kz.baymukach.canvasappmini.R
import kz.baymukach.canvasappmini.Student.StudentAnswer

class GradeStudentsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var courseId: String
    private lateinit var taskId: String
    private val answerList = mutableListOf<StudentAnswer>()
    private lateinit var adapter: AnswersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade_students)

        courseId = intent.getStringExtra("courseId") ?: return
        taskId = intent.getStringExtra("taskId") ?: return

        firestore = FirebaseFirestore.getInstance()

        val recycler: RecyclerView = findViewById(R.id.answersRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = AnswersAdapter(answerList) { answer, newGrade ->
            updateGrade(answer.studentId, newGrade)
        }

        recycler.adapter = adapter

        loadAnswers()
    }

    private fun loadAnswers() {
        firestore.collection("courses").document(courseId)
            .collection("tasks").document(taskId)
            .collection("answers")
            .get()
            .addOnSuccessListener { docs ->
                answerList.clear()
                for (doc in docs) {
                    val answer = StudentAnswer(
                        studentId = doc.id,
                        studentName = doc.getString("studentName") ?: "Аты жоқ",
                        answerText = doc.getString("answerText") ?: "",
                        grade = doc.getString("grade")
                    )
                    answerList.add(answer)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun updateGrade(studentId: String, grade: String) {
        firestore.collection("courses").document(courseId)
            .collection("tasks").document(taskId)
            .collection("answers").document(studentId)
            .update("grade", grade)
            .addOnSuccessListener {
                Toast.makeText(this, "Баға қойылды!", Toast.LENGTH_SHORT).show()
            }
    }
}
