package kr.ac.hansung.jsouptest

import android.R
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException


class AppCrawlingActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var adapter: AppCrawlingRecyclerAdapter? = null
    var melon_chart_url = "https://www.melon.com/chart/"
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appcrawling)
        recyclerView = findViewById(R.id.recyclerView_chart)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(linearLayoutManager)
        adapter = AppCrawlingRecyclerAdapter()
        recyclerView.setAdapter(adapter)
        data
    }

    private val data: Unit
        private get() {
            val jsoupAsyncTask = MelonJsoup()
            jsoupAsyncTask.execute()
        }

    private inner class MelonJsoup :
        AsyncTask<Void?, Void?, Void?>() {
        var listTitle: ArrayList<String> = ArrayList()
        var listName: ArrayList<String> = ArrayList()
        var listUrl: ArrayList<String> = ArrayList()

        override fun doInBackground(vararg voids: Void?): Void? {
            try {
                val doc: Document = Jsoup.connect(melon_chart_url).get()
                val rank_list1: Elements =
                    doc.select("div.wrap_song_info div.ellipsis.rank01 span a")
                val rank_list_name: Elements =
                    doc.select("div.wrap_song_info div.ellipsis.rank02 span a")
                val image_list1: Elements =
                    doc.select("tr#lst50.lst50 div.wrap a.image_typeAll img")
                val handler = Handler(Looper.getMainLooper()) // 객체생성
                handler.post(Runnable {
                    //순위정보
                    for (element in rank_list1) {
                        listTitle.add(element.text())
                    }
                    //가수정보
                    for (element in rank_list_name) {
                        listName.add(element.text())
                    }
                    // 이미지정보
                    for (element in image_list1) {
                        listUrl.add(element.attr("src"))
                    }
                    for (i in 0..29) {
                        val data = ChartDTO()
                        data.title = listTitle[i]
                        data.imageUrl = listUrl[i]
                        data.rankNum = (i + 1).toString()
                        data.name = listName[i]
                        adapter!!.addItem(data)
                    }
                    adapter!!.notifyDataSetChanged()
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

    }
}
