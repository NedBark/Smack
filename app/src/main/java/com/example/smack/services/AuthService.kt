package com.example.smack.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utilities.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

object AuthService {
    var accountEmail = ""
    var authToken = ""
    var isLoggedIn = false

    fun registerAccount(
        context: Context,
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object :
            StringRequest(Request.Method.POST, URL_REGISTER, Response.Listener { response ->
                println(response)
                complete(true)
            }, Response.ErrorListener { error ->
                error.printStackTrace()
                Log.d("ERROR", "Error: $error");
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginAccount(
        context: Context,
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject();
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString();

        val loginRequest =
            object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener { response ->
                try {
                    accountEmail = response.getString("user")
                    authToken = response.getString("token")
                    isLoggedIn = true
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                complete(true)
            }, Response.ErrorListener { error: VolleyError ->
                Log.d("ERROR", "Error: $error")
                complete(false)
            }) {
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray();
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }
            }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(
        context: Context,
        name: String,
        email: String,
        password: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val createRequest = object :
            JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.id = response.getString("_id")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.avatarName = response.getString("avatarName")
                    complete(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not get user: $error")
                complete(false)
            }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray();
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $authToken"
                return headers
            }
        }
        Volley.newRequestQueue(context).add(createRequest)
    }

    public fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){
        val findUserByEmailRequest = object : JsonObjectRequest(Method.GET, "$URL_GET_USER_BY_EMAIL$accountEmail", null, Response.Listener { response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.id = response.getString("_id")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarName = response.getString("avatarName")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)
            } catch (e: JSONException){
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            Log.d("ERROR", "Could not find user by email")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $authToken"
                return headers
            }
        }
        Volley.newRequestQueue(context).add(findUserByEmailRequest)
    }

}