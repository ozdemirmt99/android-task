package com.example.vero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception

class Home : AppCompatActivity() {
    private val tokenUrl = "https://api.baubuddy.de/index.php/login"
    private val dataUrl = "https://api.baubuddy.de/dev/index.php/v1/tasks/select"
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private lateinit var response: String
    private lateinit var accessToken: String
    private lateinit var data:Any
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response =
                    post(tokenUrl, "{\r\n  \"username\":\"365\",\r\n  \"password\":\"1\"\r\n}")
                var json = JSONObject(response)
                var auth = JSONObject(json.get("oauth").toString())
                accessToken= auth.get("access_token").toString()
                Log.e("Tag", this@Home.response.toString())
                data=get(dataUrl)
                Log.e("access",accessToken)
            } catch (e: Exception) {
                if (e != null) Log.e("Catch", e.message.toString())
            }
        }


//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                data = get(dataUrl)
//                Log.e("get",data.toString())
//            } catch (ex:Exception) {
//
//            }
//
//        }
    }

    //    override fun onResume() {
//        super.onResume()
//
//    }
    private fun post(url: String, json: String): String {

        val body = json.toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz")
            .header("Content-Type", "application/json").build()

        return client.newCall(request).execute().use { response ->
            response.body!!.string() // Ensure a non-null body
        }
    }
    private fun get(url:String) : String {
        val req = Request.Builder()
            .url(url)
            .get()
            .header("Authorization","Bearer ${accessToken}")
            .build()

        return client.newCall(req).execute().use { response ->
            response.body!!.string() // Ensure a non-null body
        }
    }
}