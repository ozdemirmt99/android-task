package com.example.vero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.vero.Models.Item
import com.example.vero.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class Home : AppCompatActivity() {
    private val tokenUrl = "https://api.baubuddy.de/index.php/login"
    private val dataUrl = "https://api.baubuddy.de/dev/index.php/v1/tasks/select"
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private lateinit var response: String
    private lateinit var accessToken: String
    private lateinit var data: String
    private lateinit var binding: ActivityHomeBinding
    private lateinit var activityData: List<Item>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetcher()
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swiping()
        }
        refresherPerMinute()

    }

    fun swiping() {
        fetcher()
        swipeRefreshLayout.isRefreshing = false
    }

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

    private fun get(url: String): String {
        val req = Request.Builder()
            .url(url)
            .get()
            .header("Authorization", "Bearer ${accessToken}")
            .build()

        return client.newCall(req).execute().use { response ->
            response.body!!.string() // Ensure a non-null body
        }
    }

    fun converter() {
        var allData = JSONArray(data)
        val returnedList = mutableListOf<Item>()

        for (i in 0..allData.length() - 1) {
            val current = JSONObject(allData[i].toString())
            val title = current.get("title").toString()
            val task = current.get("task").toString()
            val description = current.get("description").toString()
            val colorCode = current.get("colorCode").toString()
            val newItem =
                Item(task = task, title = title, description = description, colorCode = colorCode)
            returnedList.add(newItem)
        }
        activityData = returnedList
    }

    fun refresherPerMinute() {
        val handler = Handler()
        val runnable = Runnable {
            fetcher()
        }
        handler.postDelayed(runnable, 60000)


    }

    fun fetcher() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response =
                    post(tokenUrl, "{\r\n  \"username\":\"365\",\r\n  \"password\":\"1\"\r\n}")
                var json = JSONObject(response)
                var auth = JSONObject(json.get("oauth").toString())
                accessToken = auth.get("access_token").toString()
//                Log.e("Tag", this@Home.response.toString())
                data = get(dataUrl)
//                Log.e("access", accessToken)
                converter()
//                binding.ListOfAll.adapter=ArrayAdapter(this@Home,android.R.layout.simple_list_item_1,activityData)
                withContext(Dispatchers.Main) {
                    val recycler = binding.recyclerMain

                    recycler.layoutManager = LinearLayoutManager(this@Home)
                    recycler.adapter = RecyclerItems(activityData)
                }
            } catch (e: Exception) {
                if (e != null)
                    Log.e("Erorr", e.toString())
                Log.e("Catch", "Bu kaç " + e.message.toString())
            }
        }

    }
}