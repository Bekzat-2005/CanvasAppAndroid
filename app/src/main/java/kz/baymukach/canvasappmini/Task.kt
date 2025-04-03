package kz.baymukach.canvasappmini

import com.google.firebase.Timestamp

data class Task(
    var id: String = "",
    val taskTitle: String = "",
    val taskDescription: String = "",
    val createdAt: Timestamp? = null
)
