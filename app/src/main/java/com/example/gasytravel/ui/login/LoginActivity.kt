package com.example.gasytravel.ui.login


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.gasytravel.databinding.ActivityLoginBinding

import com.example.gasytravel.R
import com.example.gasytravel.ScrollingActivity
import com.example.gasytravel.SignUpActivity
import com.example.gasytravel.model.LoginModel
import com.example.gasytravel.model.LoginResponseModel
import com.example.gasytravel.model.UserModel
import com.example.gasytravel.service.ApiClient

import com.example.gasytravel.ui.login.LoggedInUserView
import com.example.gasytravel.ui.login.LoginViewModel
import com.example.gasytravel.ui.login.LoginViewModelFactory

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var apiClient = ApiClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val textInputError = binding.textInputError
        val signUpText = binding.textView3

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login!!.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading!!.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login?.setOnClickListener {
                loading!!.visibility = View.VISIBLE
                textInputError!!.text = ""
                textInputError.visibility = View.GONE
                apiClient.callLogin(LoginModel(username.text.toString(), password.text.toString()), object : Callback<LoginResponseModel> {
                    override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                        t.printStackTrace()
                        Log.e("DEBUG", "exception", t)
                        loading.visibility = View.GONE
                        textInputError.text = t.message
                        textInputError.visibility = View.VISIBLE
                    }

                    override fun onResponse(
                        call: Call<LoginResponseModel>,
                        response: Response<LoginResponseModel>
                    ) {
                        if (response.isSuccessful) {
                            val response = response.body()
                            if (response != null) {
                                val editor = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE).edit()

                                val user : UserModel = response.user
                                editor.putString("my_id", user.id)
                                editor.putString("my_name", user.name)
                                editor.putString("my_email", user.email)
                                editor.putString("my_token", response.token)

                                editor.apply()

                                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                    OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w("ERROR FCM", "Fetching FCM registration token failed", task.exception)
                                        return@OnCompleteListener
                                    }

                                    val token = task.result
                                    var userBody : UserModel =  user
                                    userBody.deviceToken = token

                                    Log.e("DEBUG", "DEVICE TOKEN = $token $userBody ")
                                    apiClient.fillDeviceToken(userBody, object : Callback<UserModel> {
                                        override fun onFailure(call: Call<UserModel>, t: Throwable) {
                                            t.printStackTrace()
                                            Log.e("DEBUG", "exception")
                                        }

                                        override fun onResponse(
                                            call: Call<UserModel>,
                                            response: Response<UserModel>
                                        ) {
                                            if (response.isSuccessful) {
                                                val i = Intent(this@LoginActivity, ScrollingActivity::class.java)
                                                startActivity(i)
                                                finish()
                                            }
                                        }
                                    })
                                })
                            }
                        }else{
                            textInputError.text = response.errorBody()?.string()
                            textInputError.visibility = View.VISIBLE
                        }
                        loading.visibility = View.GONE
                    }
                })
            }
        }

        // Lier la méthode onSignUpClicked au lien "S'inscrire"
        signUpText?.setOnClickListener {
            onSignUpClicked(it)
        }
    }

    fun onSignUpClicked(view: View) {
        // Code pour rediriger vers le formulaire d'inscription
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
