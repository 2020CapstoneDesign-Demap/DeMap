package kr.ac.hansung.demap.ui.hotPlace;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;

public class HotPlaceActivity extends AppCompatActivity {
    //private FirebaseAuth auth = FirebaseAuth.getInstance();
    //private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private HashMap<String, String> myHotPlace  = new HashMap();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HotPlacePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("핫플레이스");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        setContentView(R.layout.activity_hot_place);

        //setData();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_hotPlace);

        tabLayout.addTab(tabLayout.newTab().setText("핫플 검색"));
        tabLayout.addTab(tabLayout.newTab().setText("저장된 핫플"));

        viewPager = findViewById(R.id.viewpager_hotPlace);

        pagerAdapter = new HotPlacePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
