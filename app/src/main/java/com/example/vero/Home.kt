package com.example.vero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.util.Locale

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        val searchView = searchItem!!.actionView as SearchView
        Log.e("null mu ", searchView.toString())


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Kullanıcı arama yapmak için gönder düğmesine bastığında çağrılır
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Arama metni değiştikçe çağrılır
                performSearch(newText!!)
                return true
            }
        })

        return true
    }

    private fun performSearch(query: String) {
        var temp = activityData
        temp = temp.filter { e -> e.title.toLowerCase(Locale("DE","de"))
            .contains(query.toLowerCase(Locale("DE","de"))) }
        binding.recyclerMain.adapter = RecyclerItems(temp)
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
                data = get(dataUrl)
                converter()

                withContext(Dispatchers.Main) {
                    val recycler = binding.recyclerMain

                    recycler.layoutManager = LinearLayoutManager(this@Home)
                    recycler.adapter = RecyclerItems(activityData)
                }
            } catch (e: Exception) {
                if (e != null)
                    Log.e("Fetcher Error", e.toString())
            }
        }

    }
}