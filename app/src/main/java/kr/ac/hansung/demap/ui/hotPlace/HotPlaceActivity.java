package kr.ac.hansung.demap.ui.hotPlace;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;

public class HotPlaceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    HotPlaceRecyclerAdapter adapter;
    String instagram_url = "https://www.instagram.com/";
    WebView webView;
    String source;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("핫플레이스");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_hot_place);

        recyclerView = findViewById(R.id.rv_hotPlace);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new HotPlaceRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        searchView = findViewById(R.id.sv_hotPlaceTag);
        webView = new WebView(this);

        // WebView 자바스크립트 활성화
        webView.getSettings().setJavaScriptEnabled(true);
        // 자바스크립트인터페이스 연결
        // 이걸 통해 자바스크립트 내에서 자바함수에 접근할 수 있음.
        webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");
        // 페이지가 모두 로드되었을 때, 작업 정의
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
                view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 버튼을 눌렀을 때
                adapter.clearHotPlace();
                String tagUrl = "explore/tags/" + query;
                //지정한 URL을 웹 뷰로 접근하기
                webView.loadUrl(instagram_url + tagUrl);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경됐을 때
                return false;
            }
        });
    }

    // 전달받은 html 소스를 Jsoup로 로컬에서 직접 파싱을 한 후, 원하는 작업을 수행
    public class MyJavascriptInterface {
        @JavascriptInterface
        public void getHtml(String html) {
            ArrayList<String> listComment = new ArrayList<>();
            ArrayList<String> listImageUrl = new ArrayList<>();

            //위 자바스크립트가 호출되면 여기로 html이 반환됨
            source = html;
            Document doc = Jsoup.parse(source);
            Elements item_list = doc.select("div.v1Nh3 img");
            int elementSize = item_list.size();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                for(Element element : item_list) {
                    listImageUrl.add(element.attr("src"));
                    listComment.add(element.attr("alt"));
                }

                for (int i = 0; i < elementSize ; i++) {
                    HotPlaceDTO data = new HotPlaceDTO();
                    data.setImageUrl(listImageUrl.get(i));
                    data.setTag(listComment.get(i));
                    adapter.addHotPlace(data);
                }

                adapter.notifyDataSetChanged();
            });

        }
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
