package kr.ac.hansung.demap.ui.main;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import kr.ac.hansung.demap.CreateFolderActivity;
import kr.ac.hansung.demap.FolderListActivity;
import kr.ac.hansung.demap.MyfolderViewActivity;
import kr.ac.hansung.demap.NoticeActivity;
import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.SearchNaverActivity;
import kr.ac.hansung.demap.ui.hotPlace.HotPlaceActivity;
import kr.ac.hansung.demap.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String nickname = "";
    private TextView tv_nickname;
    private TextView tv_email;

    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private DrawerLayout drawerLayout;

    private MapView mapView;
//    private LocationButtonView locationButtonView;

    private SearchView searchView;

    // FusedLocationSource (Google)
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 드로어를 꺼낼 홈 버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp); // 홈버튼 이미지 변경
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바에 타이틀 안보이게
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite)); // 배경색

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        drawerLayout = findViewById(R.id.drawerlayout_main);

        // 로그인한 유저 닉네임 받아오기
        firestore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nickname = documentSnapshot.get("nickName").toString();
                tv_nickname = findViewById(R.id.tv_main_nickname);
                tv_nickname.setText(nickname);
                tv_email = findViewById(R.id.tv_main_email);
                tv_email.setText(auth.getCurrentUser().getEmail());
            }
        });

        navigationView = findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

//        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        mapView = findViewById(R.id.navermap_map_view);
        mapView.onCreate(savedInstanceState);

        naverMapBasicSettings();

        searchView = findViewById(R.id.sv_searchPlace);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("log", "검색 버튼 눌림");
                searchView.clearFocus();
                searchPlace(query); // 검색화면
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public void naverMapBasicSettings() {
        mapView.getMapAsync(this);
        //내위치 버튼
//        locationButtonView = findViewById(R.id.locationbuttonview);
        // 내위치 찾기 위한 source
        // 오류나서 지움
//        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
//            return;
//        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5666103, 126.9783882));
        marker.setMap(naverMap);

        // 지도 아무데나 눌렀을 때
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                Log.d("log", latLng.toString());

                LatLng now = latLng;
                if (now != marker.getPosition()) {
                    marker.setMap(null);
                    marker.setPosition(latLng);
                    marker.setMap(naverMap);
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void searchPlace(String searchText) {
        Intent intent = new Intent(this, SearchNaverActivity.class);
        intent.putExtra("searchText", searchText);
        startActivity(intent);
    }

    public void createFolder() {
        Intent intent = new Intent(this, CreateFolderActivity.class);
        startActivity(intent);
    }

    public void viewFolderList() {
        Intent intent = new Intent(this, FolderListActivity.class);
        startActivity(intent);
    }

    public void viewMyFolderList() {
        Intent intent = new Intent(this, MyfolderViewActivity.class);
        startActivity(intent);
    }

    public void viewSearchHotPlace() {
        Intent intent = new Intent(this, HotPlaceActivity.class);
//        intent.putExtra("search_hint", "맛집 검색");
        startActivity(intent);
    }

    public void notice() {
        Intent intent = new Intent(this, NoticeActivity.class);
        startActivity(intent);
    }

    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START); // 네비게이션 드로어 열기
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_folder_menu: viewFolderList(); break; // 폴더 검색
            case R.id.my_folder_menu: viewMyFolderList(); break; // 마이 폴더
            case R.id.hotplace_menu: viewSearchHotPlace(); break; // 핫플레이스
            case R.id.history_menu: notice(); break;
            case R.id.setting_menu: Toast.makeText(this,"설정 clicked",Toast.LENGTH_SHORT).show(); break;
            case R.id.service_menu: Toast.makeText(this,"고객센터 clicked",Toast.LENGTH_SHORT).show(); break;
            case R.id.logout_menu: logout(); break;

            //바텀
            case R.id.history_bottom_nav: Toast.makeText(this,"바텀히스토리 clicked",Toast.LENGTH_SHORT).show(); break;
            case R.id.search_folder_bottom_nav: viewFolderList(); break; // 폴더 검색
            case R.id.my_folder_bottom_nav: createFolder(); break; // 폴더 생성
        }
        return false;
    }
}


/*
class MainActivity : AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    var nickname: String? = null

    private var adapter // FolderList 어댑터
            : MySearchNaverRecyclerAdapter? = null

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

 */

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


/*
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
            //infoWindow.position = coord
            //infoWindow.open(naverMap)
        }
    }
}
*/
