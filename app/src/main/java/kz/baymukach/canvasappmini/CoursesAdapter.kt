package kz.baymukach.canvasappmini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CoursesAdapter(
    private val courseList: List<Course>,
    private val onCourseClick: (Course) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.courseName)
        val courseDescription: TextView = itemView.findViewById(R.id.courseDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun getItemCount() = courseList.size

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]

        holder.courseName.text = course.course_name
        holder.courseDescription.text = course.course_description

        holder.itemView.setOnClickListener {
            onCourseClick(course)
        }
    }
}
