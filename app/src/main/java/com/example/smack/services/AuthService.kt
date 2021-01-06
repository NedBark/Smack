package com.example.smack.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utilities.BASE_URL2
import com.example.smack.utilities.URL_LOGIN
import com.example.smack.utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

object AuthService{
    var accountEmail = ""
    var authToken = ""
    var isLoggedIn = false

    fun registerAccount(context: Context, email: String, password: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object: StringRequest(Request.Method.POST, URL_REGISTER, Response.Listener { response ->
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

    fun loginAccount(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject();
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString();

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->
            try {
                accountEmail = response.getString("user")
                authToken = response.getString("token")
                isLoggedIn = true
            } catch (e : JSONException){
                e.printStackTrace()
            }
            complete(true)
        }, Response.ErrorListener {error: VolleyError ->
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
}