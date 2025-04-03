package kz.baymukach.canvasappmini

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val createCourseBtn: Button = findViewById(R.id.createCourseBtn)
        val viewCoursesBtn: Button = findViewById(R.id.viewCoursesBtn)

        createCourseBtn.setOnClickListener {
            // Открыть экран для создания курса
            startActivity(Intent(this, CreateCourseActivity::class.java))
        }


        viewCoursesBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.adminPanelContainer, CoursesFragment())
                .addToBackStack(null)
                .commit()
        }


    }
}