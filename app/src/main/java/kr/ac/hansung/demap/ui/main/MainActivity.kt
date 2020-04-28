package kr.ac.hansung.demap.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kr.ac.hansung.demap.CreateFolderActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kr.ac.hansung.demap.FolderListActivity
import kr.ac.hansung.demap.R


class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
            it.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp) // 홈버튼 이미지 변경
            it.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        }

        main_nav.setNavigationItemSelectedListener(this) //navigationListener

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
    }

    fun createFolder() {
        var intent = Intent(this, CreateFolderActivity::class.java)
        startActivity(intent)
    }

    fun viewFolderList() {
        var intent = Intent(this, FolderListActivity::class.java)
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
            R.id.search_folder_menu-> Toast.makeText(this,"폴더검색 clicked",Toast.LENGTH_SHORT).show()
            R.id.my_folder_menu-> Toast.makeText(this,"마이폴더 clicked",Toast.LENGTH_SHORT).show()
            R.id.hotplace_menu-> Toast.makeText(this,"핫플 clicked",Toast.LENGTH_SHORT).show()
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


        // 지도 아무데나 눌렀을 때
        naverMap.setOnMapClickListener { _, coord ->
            Toast.makeText(this, "${coord}", Toast.LENGTH_LONG).show()
            // 현재 마커 무한 생성..^^
            Marker().apply {
                position = coord
                map = naverMap
            }
            /*
            infoWindow.position = coord
            infoWindow.open(naverMap)
            */
        }
    }
}
