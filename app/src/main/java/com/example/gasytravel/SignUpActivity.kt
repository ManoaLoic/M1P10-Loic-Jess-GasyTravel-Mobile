package com.example.gasytravel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gasytravel.model.SignUpModel
import com.example.gasytravel.model.SignUpResponseModel
import com.example.gasytravel.model.UserModel
import com.example.gasytravel.service.ApiClient
import com.example.gasytravel.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameEditText = findViewById(R.id.name)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        signUpButton = findViewById(R.id.sign_up_button)

        apiClient = ApiClient(this)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUp(name, email, password)
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        // Appeler l'API d'inscription en utilisant Retrofit
        apiClient.callSignUp(SignUpModel(name, email, password), object : Callback<SignUpResponseModel> {
            override fun onFailure(call: Call<SignUpResponseModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@SignUpActivity, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<SignUpResponseModel>, response: Response<SignUpResponseModel>) {
                if (response.isSuccessful) {
                    // Inscription réussie
                    Toast.makeText(this@SignUpActivity, "Inscription réussie pour $name", Toast.LENGTH_SHORT).show()
                    val i = Intent(this@SignUpActivity, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@SignUpActivity, errorBody, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
