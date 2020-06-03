package kr.ac.hansung.demap.ui.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import kr.ac.hansung.demap.CreateFolderActivity;
import kr.ac.hansung.demap.CustomerCenterActivity;
import kr.ac.hansung.demap.ui.searchfolder.FolderListActivity;
import kr.ac.hansung.demap.ui.myfolderlist.MyfolderViewActivity;
import kr.ac.hansung.demap.ui.notice.NoticeActivity;
import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.ui.searchplace.SearchNaverActivity;
import kr.ac.hansung.demap.ui.setting.SettingsActivity;
import kr.ac.hansung.demap.ui.hotPlace.HotPlaceActivity;
import kr.ac.hansung.demap.ui.login.LoginActivity;
import kr.ac.hansung.demap.ui.nickname.NickNameActivity;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Intent settingsIntent;
    private Intent myFolderIntent;

    private String uid;
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
    private static Geocoder geocoder;


    private static double latitude;
    private static double altitude;
    private static double longitude;

    private static double latitude_togo = 37.5666103;
    private static double longitude_togo = 126.9783882;
    private static List<Address> location_name;
    private static List<Address> location_name_togo;

    private FloatingActionButton fab_now;
    private FloatingActionButton fab_navi;

    static NaverMap naverMap_keep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 플로팅 버튼 생성
        fab_now = (FloatingActionButton) findViewById(R.id.fab_now_point);
        fab_now.setOnClickListener(this);
        fab_navi = (FloatingActionButton) findViewById(R.id.fab_navi);
        fab_navi.setOnClickListener(this);

        geocoder = new Geocoder(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 드로어를 꺼낼 홈 버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp); // 홈버튼 이미지 변경
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바에 타이틀 안보이게
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite)); // 배경색

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        settingsIntent = new Intent(this, SettingsActivity.class);
        myFolderIntent = new Intent(this, MyfolderViewActivity.class);

        drawerLayout = findViewById(R.id.drawerlayout_main);

/**  내 위치 리스너 **/
        // GPS 연동을 위한 권한 체크
        /*
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        */


        // 로그인한 유저 닉네임 받아오기
        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
            settingsIntent.putExtra("uid", uid);
            firestore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        nickname = documentSnapshot.get("nickName").toString();
                        tv_nickname = findViewById(R.id.tv_main_nickname);
                        tv_nickname.setText(nickname);
                        tv_email = findViewById(R.id.tv_main_email);
                        tv_email.setText(auth.getCurrentUser().getEmail());

                        settingsIntent.putExtra("nickname", nickname);
                        myFolderIntent.putExtra("nickname", nickname);
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), NickNameActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

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

    private void nowMyPoint(NaverMap naverMap) throws IOException {
        /**  내 위치 리스너 **/
        // GPS 연동을 위한 권한 체크
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            // 내위치 검색
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location==null){
                //gps를 이용한 좌표조회 실패시 network로 위치 조회
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            String provider = location.getProvider();
            double now_longitude = location.getLongitude();
            double now_latitude = location.getLatitude();
            double now_altitude = location.getAltitude();
            location_name = geocoder.getFromLocation(now_latitude,now_longitude,1);

            latitude = now_latitude;
            altitude = now_altitude;
            longitude = now_longitude;

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);

            // 카메라 위치 변경
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(now_latitude, now_longitude)).animate(CameraAnimation.Easing);
            naverMap.moveCamera(cameraUpdate);

            // 현재 위치 오버레이
            LocationOverlay locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setVisible(true);

            locationOverlay.setPosition(new LatLng(now_latitude, now_longitude));
            locationOverlay.setSubIconWidth(80);
            locationOverlay.setSubIconHeight(40);
            locationOverlay.setSubAnchor(new PointF(0.5f, 1));
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        naverMap_keep = naverMap;

        try {
            nowMyPoint(naverMap);
        } catch (IOException e) {
            System.out.println("현재 위치 가져오기 에러");
        }

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5666103, 126.9783882));
       // marker.setPosition(new LatLng(latitude, longitude));
        System.out.println("내 현재위치 : " + latitude +", " + longitude);
//        marker.setIcon(OverlayImage.fromResource(R.drawable.icon_place_marker_for_map));
        marker.setIcon(OverlayImage.fromResource(R.drawable.icon_place_marker));
        marker.setWidth(100);
        marker.setHeight(120);
        marker.setMap(naverMap);

        try {
            location_name_togo = geocoder.getFromLocation(latitude_togo,longitude_togo,1);
        } catch (IOException e) {
            System.out.println("도착지 주소 가져오기 실패");
        }

        // 지도 아무데나 눌렀을 때
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                //Log.d("log", "마커이동"+latLng.toString());


                LatLng now = latLng;
                if (now != marker.getPosition()) {
                    System.out.println("마커이동"+latLng.toString());
                    marker.setMap(null);
                    marker.setPosition(latLng);
                    marker.setMap(naverMap);

                    latitude_togo = latLng.latitude;
                    longitude_togo = latLng.longitude;
                    try {
                        location_name_togo = geocoder.getFromLocation(latitude_togo,longitude_togo,1);
                    } catch (IOException e) {
                        System.out.println("도착지 주소 가져오기 실패");
                    }

                }



            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_now_point:
                try {
                    nowMyPoint(naverMap_keep);
                } catch (IOException e) {
                    System.out.println("현재 위치 가져오기 에러");
                }
                break;
            case R.id.fab_navi:
                String sname = "현재위치";
                String dname = "도착위치";

                try {
                    String loc_name_str = location_name.toString();
                    String data[] = loc_name_str.split("\"");
                    System.out.println("현재위치주소"+data[1]);
                    String loc_name_str2 = location_name_togo.toString();
                    String data2[] = loc_name_str2.split("\"");
                    System.out.println("도착위치주소"+data[1]);
                    sname = URLEncoder.encode(data[1], "UTF-8");
                    dname = URLEncoder.encode(data2[1], "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    System.out.println("현재위치 utf-8 인코딩 에러");
                }

                String url = "nmap://route/public?slat="+latitude+"&slng="+longitude+
                        "&sname="+sname+"&dlat="+latitude_togo+"&dlng="+longitude_togo+
                "&dname="+dname+"&appname=kr.ac.hansung.demap";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list == null || list.isEmpty()) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")));
                } else {
                    view.getContext().startActivity(intent);
                }
                break;
        }
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
        intent.putExtra("folder_edit_flag", "create");
        intent.putExtra("fromMain", true);
        startActivity(intent);
    }

    public void viewFolderList() {
        Intent intent = new Intent(this, FolderListActivity.class);
        startActivity(intent);
    }

    public void viewMyFolderList() {
        startActivity(myFolderIntent);
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

    public void service() {
        Intent intent = new Intent(this, CustomerCenterActivity.class);
        startActivity(intent);
    }

    public void settings() {
        startActivity(settingsIntent);
    }

    public void logout() {
        auth.signOut();
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
            case R.id.setting_menu: settings(); break;
            case R.id.service_menu: service(); break;
            case R.id.logout_menu: logout(); break;

            //바텀
            case R.id.home_nav: break;
            case R.id.history_bottom_nav: notice(); break;
            case R.id.search_folder_bottom_nav: viewFolderList(); break; // 폴더 검색
            case R.id.my_folder_bottom_nav: createFolder(); break; // 폴더 생성
        }
        return false;
    }

    /** 위치 정보 리스너 **/
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double now_longitude = location.getLongitude();
            double now_latitude = location.getLatitude();
            double now_altitude = location.getAltitude();

            latitude = now_latitude;
            longitude = now_longitude;
            altitude = now_altitude;

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // 권한 체크
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}

