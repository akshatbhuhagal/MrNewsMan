package com.akshatbhuhagal.mrnewsman

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akshatbhuhagal.mrnewsman.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: NewsListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        fetchData()
        mAdapter = NewsListAdapter(this)
        binding.recyclerView.adapter = mAdapter


        val homeFragment = home_fragment()
        val categoryFragment = category_fragment()
        val aboutFragment = about_fragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miCategory -> setCurrentFragment(categoryFragment)
                R.id.miAbout -> setCurrentFragment(aboutFragment)
            }
            true
        }

    }


    private fun fetchData() {

        val url= "API LINK"

        val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null,
            {

                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()

                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage"),
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            {

            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {

        val builder = CustomTabsIntent.Builder();
        val customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(item.url))

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

}