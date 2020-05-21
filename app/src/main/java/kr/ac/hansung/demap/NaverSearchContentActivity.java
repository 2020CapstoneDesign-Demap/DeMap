package kr.ac.hansung.demap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
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
import java.util.ArrayList;
import java.util.List;

public class NaverSearchContentActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    private Intent intent;
    private Bundle bundle;
    private ArrayList<String> result_tags = new ArrayList<String>();
    private PlaceTagListAdapter adapter;

    private MapView mapView;
    private Marker marker;

    private TextView tv__name;
    private TextView tv_address;
    private TextView tv_category;
    private TextView tv_phone;
    //private TextView tv_tags;
    private RecyclerView rv_tags;

    private Button btn_folder_save;
    private Button btn_search_blog;
    private Button btn_navigation;

    // FusedLocationSource (Google)
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private static Geocoder geocoder;


    private static double latitude=0;
    private static double altitude;
    private static double longitude;

    private static double latitude_togo;
    private static double longitude_togo;
    private static List<Address> location_name;
    private static String location_name_togo;

    private FloatingActionButton fab_now;
    private FloatingActionButton fab_navi;

    static NaverMap naverMap_keep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_search_content);

        intent = getIntent();
        bundle = intent.getExtras();

        // 플로팅 버튼 생성
        fab_now = (FloatingActionButton) findViewById(R.id.fab_now_point);
        fab_now.setOnClickListener(this);
        fab_navi = (FloatingActionButton) findViewById(R.id.fab_navi);
        fab_navi.setOnClickListener(this);

        geocoder = new Geocoder(this);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle(intent.getStringExtra("result_name"));
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        

        tv__name = findViewById(R.id.tv_naver_search_content_name);
        tv__name.setText(intent.getStringExtra("result_name"));
        if(intent.getStringExtra("result_name") != null) {
            location_name_togo = intent.getStringExtra("result_name");
        } else {
            location_name_togo = "도착지";
        }
        tv_address = findViewById(R.id.tv_naver_search_content_address);
        tv_address.setText(intent.getStringExtra("result_addr"));

        tv_category = findViewById(R.id.tv_naver_search_content_category);
        tv_category.setText(intent.getStringExtra("result_category"));

        tv_phone = findViewById(R.id.tv_naver_search_content_phone);
        tv_phone.setText(intent.getStringExtra("result_phone"));

        if(bundle.getStringArrayList("result_tags") != null) {
            result_tags.addAll(bundle.getStringArrayList("result_tags"));
        }
        if(result_tags != null) {
            rv_tags = findViewById(R.id.tags_RecyclerView);
            rv_tags.setHasFixedSize(false);
            rv_tags.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PlaceTagListAdapter();
            if(result_tags.size()%3 ==1) {
                result_tags.add(null);
                result_tags.add(null);
            } else if(result_tags.size()%3 ==2) {
                result_tags.add(null);
            }
            adapter.setItem(result_tags);
            adapter.setTagCount(result_tags);
            rv_tags.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        btn_folder_save = findViewById(R.id.btn_naver_search_content_folder);
        btn_folder_save.setEnabled(true);
        btn_folder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AddPlaceToFolderActivity.class);
                int x = intent.getIntExtra("result_mapx", 0);
                int y = intent.getIntExtra("result_mapy", 0);
                intent1.putExtra("result_mapx", x);
                intent1.putExtra("result_mapy", y);
                intent1.putExtra("result_name", intent.getStringExtra("result_name"));
                intent1.putExtra("result_addr", intent.getStringExtra("result_addr"));
                intent1.putExtra("result_category", intent.getStringExtra("result_category"));
                intent1.putExtra("result_phone", intent.getStringExtra("result_phone"));

                startActivity(intent1);
            }
        });

        btn_navigation = findViewById(R.id.btn_naver_search_content_navi);
        btn_navigation.setEnabled(true);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sname = "현재위치";
                String dname = "도착위치";

                if(latitude == 0) {
                    Toast.makeText(NaverSearchContentActivity.this,"현재 위치를 설정해 주세요",Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        String loc_name_str = location_name.toString();
                        String data[] = loc_name_str.split("\"");
//                        System.out.println("현재위치주소" + data[1]);
                        sname = URLEncoder.encode(data[1], "UTF-8");
                        dname = URLEncoder.encode(String.valueOf(location_name_togo), "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        System.out.println("현재위치 utf-8 인코딩 에러");
                    }

                    String url = "nmap://route/public?slat=" + latitude + "&slng=" + longitude +
                            "&sname=" + sname + "&dlat=" + latitude_togo + "&dlng=" + longitude_togo +
                            "&dname=" + dname + "&appname=kr.ac.hansung.demap";

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);

                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list == null || list.isEmpty()) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")));
                    } else {
                        view.getContext().startActivity(intent);
                    }
                }
            }
        });

        btn_search_blog = findViewById(R.id.blog_search_btn);
        btn_search_blog.setEnabled(true);
        btn_search_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), SearchBlogActivity.class);
                intent2.putExtra("result_name", intent.getStringExtra("result_name"));

                startActivity(intent2);
            }
        });

        mapView = findViewById(R.id.navermap_map_view_search_content);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


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

                if(latitude == 0) {
                    Toast.makeText(NaverSearchContentActivity.this,"현재 위치를 설정해 주세요",Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        String loc_name_str = location_name.toString();
                        String data[] = loc_name_str.split("\"");
                        System.out.println("현재위치주소" + data[1]);
                        sname = URLEncoder.encode(data[1], "UTF-8");
                        dname = URLEncoder.encode(String.valueOf(location_name_togo), "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        System.out.println("현재위치 utf-8 인코딩 에러");
                    }

                    String url = "nmap://route/public?slat=" + latitude + "&slng=" + longitude +
                            "&sname=" + sname + "&dlat=" + latitude_togo + "&dlng=" + longitude_togo +
                            "&dname=" + dname + "&appname=kr.ac.hansung.demap";

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);

                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list == null || list.isEmpty()) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")));
                    } else {
                        view.getContext().startActivity(intent);
                    }
                }
                break;
        }
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
//            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        naverMap_keep = naverMap;

        Double x = (double)intent.getIntExtra("result_mapx", 0);
        Double y = (double)intent.getIntExtra("result_mapy", 0);
        GeoTransPoint gtp1 = new GeoTransPoint(x, y);
        GeoTransPoint gtp2 = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, gtp1);
        Double lat = gtp2.getY();
        Double lng = gtp2.getX();
        GeoTransPoint oLatLng = new GeoTransPoint(lat, lng);  // 맵뷰에서 사용가능한 좌표계
        Log.d("log", "좌표변환결과 x : " + oLatLng.getX() + ", y : " + oLatLng.getY());

        LatLng location = new LatLng(oLatLng.getX(), oLatLng.getY());
        naverMap.setCameraPosition(new CameraPosition(location, 17));

        marker = new Marker();
        marker.setPosition(new LatLng(oLatLng.getX(), oLatLng.getY()));
        marker.setIcon(OverlayImage.fromResource(R.drawable.icon_place_marker_for_map));
        marker.setMap(naverMap);

        latitude_togo = oLatLng.getX();
        longitude_togo = oLatLng.getY();

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
