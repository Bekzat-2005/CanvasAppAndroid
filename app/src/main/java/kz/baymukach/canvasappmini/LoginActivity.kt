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

class LoginActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        val logTxt: TextView = findViewById(R.id.logTxt);
        val logBtn: Button = findViewById(R.id.logBtn);

        logTxt.setOnClickListener(){
            startActivity(Intent(this, RegisterActivity::class.java));
        }
        logBtn.setOnClickListener(){
            loginUser()
        }


    }

    private fun loginUser() {
        val logEmail: EditText = findViewById(R.id.logEmail);
        val logPass: EditText = findViewById(R.id.logPass);

        val email = logEmail.text.toString().trim();
        val password = logPass.text.toString().trim();

        if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(this, "Толтырыныз", Toast.LENGTH_SHORT).show();
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, MainActivity::class.java));
                    finish();
                }else{
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

    }
}