package kr.ac.hansung.demap.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kr.ac.hansung.demap.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    var nickname: String? = null

    private var adapter // FolderList 어댑터
            : MySearchNaverRecyclerAdapter? = null


    private var br: BufferedReader? = null
    private var searchResult: StringBuilder? = null

    var datas: String? = null
    var array: Array<String?>? = null
    var title: Array<String?>? = null
    var roadaddress: Array<String?>? = null
    var mapx: IntArray? = null;
    var mapy: IntArray? = null;
    //var category: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initiate
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setSupportActionBar(toolbar_main) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.let {
            it.setBackgroundDrawable(getDrawable(R.color.colorWhite))
            it.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
            it.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp) // 홈버튼 이미지 변경
            it.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        }

        window.statusBarColor = resources.getColor(R.color.colorWhite, theme)

        // 로그인한 유저 닉네임 받아오기
        firestore?.collection("users")?.document(auth?.currentUser?.uid!!)?.get()?.addOnSuccessListener { documentSnapshot ->
            nickname = documentSnapshot["nickName"].toString();
            main_nav_header.tv_nickname.text = nickname
            main_nav_header.tv_email.text = auth?.currentUser?.email
        }

        //navigationListener
        main_nav.setNavigationItemSelectedListener(this)

        // bottom navigation
        bottom_nav.setOnNavigationItemSelectedListener(this)

        // naver map 객체 가져오기
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 검색어 가져오기
        //status1 = (TextView)findViewById(R.id.textview_searchresult_name); //파싱된 결과를 보자
//status2 = (TextView)findViewById(R.id.textview_seatchresult_address);
// 검색어 가져오기
        val searchView =
            findViewById<SearchView>(R.id.sv_searchPlace)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchword: String): Boolean { // 검색 버튼이 눌러졌을 때 이벤트 처리
                println("검색 처리됨 : $searchword")
                //searchForFolderName(keyword);
                searchForNaverAPI(searchword)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean { // 검색어가 변경되었을 때 이벤트 처리
                return false
            }
        })
        //System.out.println(query);

        //System.out.println(query);
        val recyclerView : RecyclerView? =
            findViewById<RecyclerView>(R.id.listView_search_result_list)
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false)
        }
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        adapter = MySearchNaverRecyclerAdapter()
        if (recyclerView != null) {
            recyclerView.adapter = adapter
        }
    }

    fun createFolder() {
        var intent = Intent(this, CreateFolderActivity::class.java)
        startActivity(intent)
    }

    fun viewFolderList() {
        var intent = Intent(this, FolderListActivity::class.java)
        startActivity(intent)
    }

    fun viewMyFolderList() {
        var intent = Intent(this, MyfolderViewActivity::class.java)
        startActivity(intent)
    }

    fun viewSearchHotPlace() {
        var intent = Intent(this, SearchNaverActivity::class.java)
        startActivity(intent)
    }

/*

    // searchview
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        sv_searchPlace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // 검색어가 변경되었을 때 이벤트 처리
                return false
            }

            override fun onQueryTextChange(keyword: String): Boolean {
                Toast.makeText(this@MainActivity, keyword ,Toast.LENGTH_SHORT).show()
                return true
            }
        })
        return true
    }

*/
// 네이버 지역 검색 api 연동 및 데이터 가져오기
open fun searchForNaverAPI(query: String?): Unit {
    val clientId = "F29Q2vNcHyw0fOQwkzbO" //애플리케이션 클라이언트 아이디값";
    val clientSecret = "5dNDcpf9qo" //애플리케이션 클라이언트 시크릿값";
    val display = 5 // 보여지는 검색결과의 수
    // 네트워크 연결은 Thread 생성 필요
    object : Thread() {
        override fun run() {
            try {
                val searchword : String? = URLEncoder.encode(query, "UTF-8")
                println("검색어 utf-8 : $searchword")
                val url = URL(
                    "https://openapi.naver.com/v1/search/local?" //+ "key=" + clientSecret
                            + "&query=" + searchword //여기는 쿼리를 넣으세요(검색어)
                            + "&target=local&start=1&display=" + display
                )
                println("검색 url : $url")
                val con =
                    url.openConnection() as HttpsURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("X-Naver-Client-Id", clientId)
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret)
                con.connect()
                val responseCode = con.responseCode
                br = if (responseCode == 200) { // 정상 호출
                    BufferedReader(InputStreamReader(con.inputStream))
                } else { // 에러 발생
                    BufferedReader(InputStreamReader(con.errorStream))
                }
                searchResult = java.lang.StringBuilder()
                println("207라인 정상")
                var inputLine: String = br!!.readLine()
                println("209라인 정상" + inputLine)
                while (inputLine != null) {
                    // 여기에서 FATAL 예외 발생 중
                    searchResult!!.append(inputLine + "\n")
                    inputLine = br!!.readLine()
                }
                println("212라인 정상")
                br!!.close()
                con.disconnect()
                br = if (responseCode == 200) { // 정상 호출
                    BufferedReader(InputStreamReader(con.inputStream))
                } else { // 에러 발생
                    BufferedReader(InputStreamReader(con.errorStream))
                }
                // 데이터 파싱
                datas = searchResult.toString()
                println("검색 datas : $datas")
                array = datas!!.split("\"").toTypedArray()
                title = arrayOfNulls(display)
                roadaddress = arrayOfNulls(display)
                mapx = IntArray(display)
                mapy = IntArray(display)
                //category = new String[display];
                var k = 0
                for (i in array!!.indices) {
                    if (array!![i] == "title") {
                        title!![k] = Html.fromHtml(array!![i + 2]).toString()
                        println(array!![i + 2])
                    }
                    if (array!![i] == "roadAddress") roadaddress!![k] =
                        Html.fromHtml(array!![i + 2]).toString()
                    if (array!![i] == "mapx") mapx!![k] = array!![i + 2]?.toInt()!!
                    if (array!![i] == "mapy") {
                        mapy!![k] = array!![i + 2]?.toInt()!!
                        k++
                    }
                    /*if (array[i].equals("category")) {
                            category[k] = Html.fromHtml(array[i + 2]).toString();
                            k++;
                        }*/
                }
                //System.out.println(array);
                println(
                    roadaddress!![0] + roadaddress!![1] + roadaddress!![2] + roadaddress!![3] + roadaddress!![4]
                )
                //System.out.println(category[0]+category[1]+category[2]+category[3]+category[4]);
                println(
                    "x 좌표 : " + mapx!![0] + mapx!![1] + mapx!![2] + mapx!![3] + mapx!![4]
                )
                println(
                    "y 좌표 : " + mapy!![0] + mapy!![1] + mapy!![2] + mapy!![3] + mapy!![4]
                )
                adapter!!.addItems(title, roadaddress,  /*category,*/mapx, mapy)
                adapter!!.notifyDataSetChanged()
                // title[0], link[0], bloggername[0] 등 인덱스 값에 맞게 검색결과를 변수화하였다.
            } catch (e: Exception) { //status1.setText("에러가..났습니다...");
                println("에러 발생 : $e")
            }
        }
    }.start()
}


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                drawerlayout_main.openDrawer(GravityCompat.START) // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search_folder_menu-> viewFolderList() //Toast.makeText(this,"폴더검색 clicked",Toast.LENGTH_SHORT).show()
            R.id.my_folder_menu-> viewMyFolderList() //Toast.makeText(this,"마이폴더 clicked",Toast.LENGTH_SHORT).show()
            R.id.hotplace_menu-> viewSearchHotPlace()
            R.id.history_menu-> Toast.makeText(this,"히스토리 clicked",Toast.LENGTH_SHORT).show()
            R.id.setting_menu-> Toast.makeText(this,"설정 clicked",Toast.LENGTH_SHORT).show()
            R.id.service_menu-> Toast.makeText(this,"고객센터 clicked",Toast.LENGTH_SHORT).show()
            R.id.logout_menu-> Toast.makeText(this,"로그아웃 clicked",Toast.LENGTH_SHORT).show()

            R.id.history_bottom_nav-> Toast.makeText(this,"바텀히스토리 clicked",Toast.LENGTH_SHORT).show()
            R.id.search_folder_bottom_nav-> viewFolderList()
            R.id.my_folder_bottom_nav-> createFolder()
        }
        return false
    }

    // 드로어가 나와있을 때 뒤로가기 버튼 누를 경우 드로어 닫기
    override fun onBackPressed() {
        if (drawerlayout_main.isDrawerOpen(GravityCompat.START)) {
            drawerlayout_main.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }


    // 좌표 눌렀을 때 커스텀 window adapter
    private class InfoWindowAdapter(private val context: Context) : InfoWindow.ViewAdapter() {
        private var rootView: View? = null
        private var icon: ImageView? = null
        private var text: TextView? = null

        override fun getView(infoWindow: InfoWindow): View {
            val view = rootView ?: View.inflate(context, R.layout.view_custom_info_window, null).also { rootView = it }
            val icon = icon ?: view.findViewById<ImageView>(R.id.icon).also { icon = it }
            val text = text ?: view.findViewById<TextView>(R.id.text).also { text = it }

            val marker = infoWindow.marker
            if (marker != null) {
                icon.setImageResource(R.drawable.ic_place_black_24dp)
                text.text = marker.tag as String?
            } else {
                icon.setImageResource(R.drawable.ic_my_location_black_24dp)
                text.text = context.getString(
                    R.string.format_coord, infoWindow.position.latitude, infoWindow.position.longitude)
            }
            return view
        }
    }



    // 지도 사용하는 거 다 여기에 쓰면 되는거가타요
    override fun onMapReady(naverMap: NaverMap) {
        val infoWindow = InfoWindow().apply {
            anchor = PointF(0f, 1f)
            offsetX = resources.getDimensionPixelSize(R.dimen.custom_info_window_offset_x)
            offsetY = resources.getDimensionPixelSize(R.dimen.custom_info_window_offset_y)
            adapter = InfoWindowAdapter(this@MainActivity)
            setOnClickListener {
                close()
                true
            }
        }


        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = naverMap

        // 지도 아무데나 눌렀을 때
        naverMap.setOnMapClickListener { _, coord ->
            Toast.makeText(this, "${coord}", Toast.LENGTH_LONG).show()
            var now : LatLng? = coord

            if(now != marker.position) {

                marker.map = null

                marker.position = coord
                marker.map = naverMap

            }
            /*
            infoWindow.position = coord
            infoWindow.open(naverMap)
            */
        }
    }
}
