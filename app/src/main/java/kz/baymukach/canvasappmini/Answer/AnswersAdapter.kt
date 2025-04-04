package kz.baymukach.canvasappmini.Answer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kz.baymukach.canvasappmini.R
import kz.baymukach.canvasappmini.Student.StudentAnswer

class AnswersAdapter(
    private val answers: List<StudentAnswer>,
    private val onGradeChanged: (StudentAnswer, String) -> Unit
) : RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.studentName)
        val answer: TextView = view.findViewById(R.id.answerText)
        val gradeInput: EditText = view.findViewById(R.id.gradeInput)
        val saveBtn: Button = view.findViewById(R.id.saveGradeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val item = answers[position]
        holder.name.text = item.studentName
        holder.answer.text = item.answerText
        holder.gradeInput.setText(item.grade ?: "")

        holder.saveBtn.setOnClickListener {
            val grade = holder.gradeInput.text.toString()
            if (grade.isNotEmpty()) {
                onGradeChanged(item, grade)
            }
        }
    }

    override fun getItemCount(): Int = answers.size
}
