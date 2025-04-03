package kz.baymukach.canvasappmini

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ManageTasksActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var courseId: String
    private lateinit var taskTitle: EditText
    private lateinit var taskDescription: EditText
    private lateinit var addTaskBtn: Button
    private lateinit var tasksRecycler: RecyclerView
    private val taskList = mutableListOf<Task>()
    private lateinit var adapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tasks)

        firestore = FirebaseFirestore.getInstance()
        courseId = intent.getStringExtra("courseId") ?: return

        taskTitle = findViewById(R.id.taskTitle)
        taskDescription = findViewById(R.id.taskDescription)
        addTaskBtn = findViewById(R.id.addTaskBtn)
        tasksRecycler = findViewById(R.id.tasksRecycler)

        tasksRecycler.layoutManager = LinearLayoutManager(this)

        adapter = TasksAdapter(taskList,
            onTaskClicked = { task ->
                showPopupMenu(task)
            },
            onViewAnswersClicked = { task ->
                val intent = Intent(this, GradeStudentsActivity::class.java)
                intent.putExtra("courseId", courseId)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            }
        )

        tasksRecycler.adapter = adapter

        addTaskBtn.setOnClickListener {
            addTask()
        }

        loadTasks()
    }

    private fun addTask() {
        val title = taskTitle.text.toString().trim()
        val description = taskDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Барлық жолдарды толтырыңыз!", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(
            taskTitle = title,
            taskDescription = description,
            createdAt = Timestamp.now()
        )

        firestore.collection("courses").document(courseId)
            .collection("tasks")
            .add(task)
            .addOnSuccessListener {
                Toast.makeText(this, "Тапсырма қосылды!", Toast.LENGTH_SHORT).show()
                taskTitle.setText("")
                taskDescription.setText("")
                loadTasks()
            }
    }

    private fun loadTasks() {
        firestore.collection("courses").document(courseId)
            .collection("tasks")
            .orderBy("createdAt")
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

    private fun showPopupMenu(task: Task) {
        val viewHolder = tasksRecycler.findViewHolderForAdapterPosition(taskList.indexOf(task))
        val anchorView = viewHolder?.itemView ?: return

        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.task_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editTask -> editTask(task)
                R.id.deleteTask -> deleteTask(task)
            }
            true
        }
        popupMenu.show()
    }

    private fun editTask(task: Task) {
        taskTitle.setText(task.taskTitle)
        taskDescription.setText(task.taskDescription)

        addTaskBtn.text = "Өзгерту"
        addTaskBtn.setOnClickListener {
            val updatedTask = mapOf(
                "taskTitle" to taskTitle.text.toString(),
                "taskDescription" to taskDescription.text.toString()
            )

            firestore.collection("courses").document(courseId)
                .collection("tasks").document(task.id)
                .update(updatedTask)
                .addOnSuccessListener {
                    Toast.makeText(this, "Тапсырма өзгертілді!", Toast.LENGTH_SHORT).show()
                    taskTitle.setText("")
                    taskDescription.setText("")
                    addTaskBtn.text = "Тапсырма қосу"
                    addTaskBtn.setOnClickListener { addTask() }
                    loadTasks()
                }
        }
    }

    private fun deleteTask(task: Task) {
        firestore.collection("courses").document(courseId)
            .collection("tasks").document(task.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Тапсырма жойылды!", Toast.LENGTH_SHORT).show()
                loadTasks()
            }
    }
}
