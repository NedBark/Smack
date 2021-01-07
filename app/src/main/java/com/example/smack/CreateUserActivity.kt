package com.example.smack

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.services.AuthService
import com.example.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if(color == 0){
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }
        val resourceID = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImageView.setImageResource(resourceID)

    }

    fun generateColorClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        val imageView = findViewById<ImageView>(R.id.createAvatarImageView)
        imageView.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserClicked(view: View) {
        enableSpinner(true)
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()
        val userName = createUserNameText.text.toString()
        AuthService.registerAccount(this, email, password) { registerSuccess: Boolean ->
            if (registerSuccess) {
                AuthService.loginAccount(this, email, password) { loginSuccess: Boolean ->
                    if (loginSuccess) {
                        println("Logged in")
                        println(AuthService.authToken)
                        AuthService.createUser(this, userName, email, password, userAvatar, avatarColor, complete = { createSuccess ->
                            if (createSuccess) {
                                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                println("Created user success")
                                finish()
                            }
                            enableSpinner(false)
                        })
                    } else {
                        enableSpinner(false);
                    }
                }
            } else {
                enableSpinner(false);
            }
        }
    }

    fun enableSpinner(enable: Boolean): Unit{
        if(enable){
            createUserSpinner.visibility = View.VISIBLE
            createUserBtn.isEnabled = false
        } else {
            createUserSpinner.visibility = View.INVISIBLE
            createUserBtn.isEnabled = true
        }
    }
}