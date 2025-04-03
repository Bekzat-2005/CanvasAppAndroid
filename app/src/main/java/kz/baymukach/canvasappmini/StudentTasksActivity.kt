package kz.baymukach.canvasappmini

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class StudentTasksActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TasksAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_tasks)

        courseId = intent.getStringExtra("courseId") ?: return
        courseName = intent.getStringExtra("courseName") ?: "Курс"

        val courseTitle: TextView = findViewById(R.id.courseTitle)
        courseTitle.text = courseName

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.tasksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TasksAdapter(
            taskList,
            onTaskClicked = { task ->
                val intent = Intent(this, AnswerActivity::class.java)
                intent.putExtra("courseId", courseId)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            },
            onViewAnswersClicked = { /* Студентке керек емес, бос қалдырамыз */ }
        )


        recyclerView.adapter = adapter

        loadTasks()
    }

    private fun loadTasks() {
        firestore.collection("courses").document(courseId)
            .collection("tasks")
            .get()
            .addOnSuccessListener { docs ->
                taskList.clear()
                for (doc in docs) {
                    val task = doc.toObject(Task::class.java)
                    task.id = doc.id
                    taskList.add(task)
                }
                adapter.notifyDataSetChanged()
            }
    }
}
