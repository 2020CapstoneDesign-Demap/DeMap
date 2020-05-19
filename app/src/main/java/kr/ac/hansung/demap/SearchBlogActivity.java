package kr.ac.hansung.demap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class SearchBlogActivity extends AppCompatActivity {

    private BufferedReader br;
    private StringBuilder searchResult;

    String data;
    String[] array;
    String[] title;
    String[] description;
    String[] postdate;
    String[] link;

    private Intent intent;

    private MySearchBlogRecyclerAdapter adapter; // BlogList 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_blog);

        intent = getIntent();

        String key = intent.getStringExtra("result_name");
        searchForNaverBlogAPI(key);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle(intent.getStringExtra("result_name"));
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.search_blog_result_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySearchBlogRecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    // 네이버 지역 검색 api 연동 및 데이터 가져오기
    public void searchForNaverBlogAPI(String query) {
        final String clientId = "F29Q2vNcHyw0fOQwkzbO";//애플리케이션 클라이언트 아이디값";
        final String clientSecret = "5dNDcpf9qo";//애플리케이션 클라이언트 시크릿값";
        final int display = 5; // 보여지는 검색결과의 수

        // 네트워크 연결은 Thread 생성 필요
        new Thread() {
            @Override
            public void run() {
                try {

                    String searchword = URLEncoder.encode(query, "UTF-8");
                    System.out.println("검색어 utf-8 : " + searchword);

                    URL url = new URL("https://openapi.naver.com/v1/search/blog?"
                            //+ "key=" + clientSecret
                            + "&query=" + searchword //여기는 쿼리를 넣으세요(검색어)
                            + "&target=local&start=1&display=" + display);

                    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    con.connect();

                    int responseCode = con.getResponseCode();


                    if(responseCode==200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }

                    searchResult = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        searchResult.append(inputLine + "\n");

                    }
                    br.close();
                    con.disconnect();

                    if(responseCode==200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }

                    // 데이터 파싱
                    data = searchResult.toString();
                    Log.d("log", "data : " + data);
                    array = data.split("\"");
                    title = new String[display];
                    link = new String[display];
                    description = new String[display];
                    postdate = new String[display];
                    //category = new String[display];

                    int k = 0;
                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals("title")) {
                            title[k] = Html.fromHtml(array[i + 2]).toString();
                            System.out.println(array[i + 2]);
                        }
                        if (array[i].equals("link")) {
                            String s = Html.fromHtml(array[i + 2]).toString();
                            link[k] = s.substring(s.lastIndexOf(">") + 1);
                        }
                        if (array[i].equals("description"))
                            description[k] = Html.fromHtml(array[i + 2]).toString();
                        if (array[i].equals("postdate")) {
                            postdate[k] = Html.fromHtml(array[i + 2]).toString();
                            String year = postdate[k].substring(0,4) + "년 ";
                            String mon = postdate[k].substring(4,6) + "월 ";
                            String day = postdate[k].substring(6) + "일 ";
                            postdate[k] = year + mon + day;
                            k++;
                        }
                        /*if (array[i].equals("category")) {
                            category[k] = Html.fromHtml(array[i + 2]).toString();
                            k++;
                        }*/
                    }

                    //System.out.println(array);
                    System.out.println(description[0]+description[1]+description[2]+description[3]+description[4]);

                    adapter.addItems(title, description, postdate, link);
                    adapter.notifyDataSetChanged();
                    // title[0], link[0], bloggername[0] 등 인덱스 값에 맞게 검색결과를 변수화하였다.

                } catch (
                        Exception e) {
                    //status1.setText("에러가..났습니다...");
                    System.out.println("에러 발생 : " + e);
                }
            }
        }.start();
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
    protected void onStart() {
        super.onStart();
        //mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }
}
