package kr.ac.hansung.demap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;

public class NaverSearchContentActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_search_content);

        intent = getIntent();
        bundle = intent.getExtras();

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

        tv_address = findViewById(R.id.tv_naver_search_content_address);
        tv_address.setText(intent.getStringExtra("result_addr"));

        tv_category = findViewById(R.id.tv_naver_search_content_category);
        tv_category.setText(intent.getStringExtra("result_category"));

        tv_phone = findViewById(R.id.tv_naver_search_content_phone);
        tv_phone.setText(intent.getStringExtra("result_phone"));

        result_tags.addAll(bundle.getStringArrayList("result_tags"));
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

        mapView = findViewById(R.id.navermap_map_view_search_content);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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

}
