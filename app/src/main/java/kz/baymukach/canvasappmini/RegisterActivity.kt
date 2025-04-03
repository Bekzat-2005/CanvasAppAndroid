package kz.baymukach.canvasappmini

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var firestore: FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regBtn: Button = findViewById(R.id.regBtn);
        val regTxt: TextView = findViewById(R.id.regTxt);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener {
            registerUser();
        }

        regTxt.setOnClickListener(){
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }

    private fun registerUser() {
        val regName: EditText = findViewById(R.id.regName)
        val regEmail: EditText = findViewById(R.id.regEmail)
        val regPass: EditText = findViewById(R.id.regPass)
        val regPassConf: EditText = findViewById(R.id.regPassConf)

        val name = regName.text.toString().trim()
        val email = regEmail.text.toString().trim()
        val password = regPass.text.toString().trim()
        val confPassword = regPassConf.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confPassword.isEmpty()) {
            Toast.makeText(this, "Толтырыныз!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confPassword) {
            Toast.makeText(this, "Құпия сөздер сәйкес келмейді!", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userData = hashMapOf(
                        "display_name" to name,
                        "email" to email,
                        "password" to password,
                        "role" to "student"
                    )
                    firestore.collection("users")
                        .document(user!!.uid)
                        .set(userData)
                        .addOnCompleteListener(this) { fireStoreTask ->
                            if (fireStoreTask.isSuccessful) {
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Firestore қатесі: ${fireStoreTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Тіркеу қатесі: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}