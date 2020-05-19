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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.HashMap;
import java.util.Map;

import kr.ac.hansung.demap.CreateFolderActivity;
import kr.ac.hansung.demap.CustomerCenterActivity;
import kr.ac.hansung.demap.FolderListActivity;
import kr.ac.hansung.demap.MyfolderViewActivity;
import kr.ac.hansung.demap.NoticeActivity;
import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.SearchNaverActivity;
import kr.ac.hansung.demap.SettingsActivity;
import kr.ac.hansung.demap.ui.hotPlace.HotPlaceActivity;
import kr.ac.hansung.demap.ui.login.LoginActivity;
import kr.ac.hansung.demap.ui.nickname.NickNameActivity;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Intent settingsIntent;

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

        settingsIntent = new Intent(this, SettingsActivity.class);

        drawerLayout = findViewById(R.id.drawerlayout_main);

        //push 메시지 token
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
//                        Log.d("TAG", token);
//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
//
//                        Map<String, String> pushToken = new HashMap<>();
//                        pushToken.put("token", token);
//                        firestore.collection("pushTokens").document(auth.getCurrentUser().getUid()).set(pushToken);
//                    }
//                });

        // 로그인한 유저 닉네임 받아오기
        if (auth.getCurrentUser() != null) {
            firestore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        nickname = documentSnapshot.get("nickName").toString();
                        tv_nickname = findViewById(R.id.tv_main_nickname);
                        tv_nickname.setText(nickname);
                        tv_email = findViewById(R.id.tv_main_email);
                        tv_email.setText(auth.getCurrentUser().getEmail());

                        settingsIntent.putExtra("nickname", nickname);
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
}

