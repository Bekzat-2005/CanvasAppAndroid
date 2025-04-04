package kz.baymukach.canvasappmini

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kz.baymukach.canvasappmini.Admin.AdminPanelActivity
import kz.baymukach.canvasappmini.Teacher.TeacherCoursesActivity
import kz.baymukach.canvasappmini.Student.StudentCoursesActivity

class MainActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        val adminPanel: Button = findViewById(R.id.adminPanel);
        val studentPortal: Button = findViewById(R.id.studentPortal)
        val teacherPortal: Button = findViewById(R.id.teacherPortal);


        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Получаем данные о пользователе из Firestore
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val role = document.getString("role")
                        when (role) {
                            "admin" -> {
                                // Показываем только Admin Panel
                                adminPanel.isEnabled = true
                                studentPortal.isEnabled = false
                                teacherPortal.isEnabled = false
                            }
                            "teacher" -> {
                                // Показываем только Teacher Portal
                                adminPanel.isEnabled = false
                                studentPortal.isEnabled = false
                                teacherPortal.isEnabled = true
                            }
                            "student" -> {
                                // Показываем только Student Portal
                                adminPanel.isEnabled = false
                                studentPortal.isEnabled = true
                                teacherPortal.isEnabled = false
                            }
                            else -> {
                                Toast.makeText(this, "Роль не определена", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Ошибка при получении данных: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Если пользователь не авторизован, отправляем его на экран логина
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Обработка кликов по кнопкам
        adminPanel.setOnClickListener {
            // Открыть Admin Panel
            startActivity(Intent(this, AdminPanelActivity::class.java))
        }

        studentPortal.setOnClickListener {
            // Открыть Student Portal
            startActivity(Intent(this, StudentCoursesActivity::class.java))
        }

        teacherPortal.setOnClickListener {
            // Открыть Teacher Portal
            startActivity(Intent(this, TeacherCoursesActivity::class.java))
        }

    }
}