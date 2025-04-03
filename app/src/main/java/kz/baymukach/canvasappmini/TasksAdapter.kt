package kz.baymukach.canvasappmini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(
    private val tasks: List<Task>,
    private val onTaskClicked: (Task) -> Unit,
    private val onViewAnswersClicked: (Task) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val viewAnswersBtn: Button = itemView.findViewById(R.id.viewAnswersBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.taskTitle

        holder.itemView.setOnClickListener {
            onTaskClicked(task)
        }

        holder.viewAnswersBtn.setOnClickListener {
            onViewAnswersClicked(task)
        }
    }

    override fun getItemCount(): Int = tasks.size
}
