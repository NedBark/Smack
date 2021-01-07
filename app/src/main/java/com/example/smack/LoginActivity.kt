package com.example.smack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginButtonClicked(view: View){
        val userEmail = userEmailLogin.text.toString()
        val userPassword = userPasswordLogin.text.toString()

        AuthService.loginAccount(this, userEmail, userPassword) { complete ->
            if (complete) {
                AuthService.findUserByEmail(this) { complete ->
                    if (complete) {
                        println("A mers lol")
                    }
                }
            }
        }
    }

    fun loginCreateUserBtnClicked(view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }
}