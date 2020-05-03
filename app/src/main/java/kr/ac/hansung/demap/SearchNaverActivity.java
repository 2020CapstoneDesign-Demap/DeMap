package kr.ac.hansung.demap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.Html;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class SearchNaverActivity extends AppCompatActivity {

    private MySearchNaverRecyclerAdapter adapter; // FolderList 어댑터


    private BufferedReader br;
    private StringBuilder searchResult;

    String data;
    String[] array;
    String[] title;
    String[] roadaddress;
    int[] mapx;
    int[] mapy;
    //String[] category;


    @Override
        public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_naver);

        //status1 = (TextView)findViewById(R.id.textview_searchresult_name); //파싱된 결과를 보자
        //status2 = (TextView)findViewById(R.id.textview_seatchresult_address);

        // 검색어 가져오기
        SearchView searchView =  findViewById(R.id.searchword_edittext);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchword) {
                // 검색 버튼이 눌러졌을 때 이벤트 처리
                System.out.println("검색 처리됨 : " + searchword);
                //searchForFolderName(keyword);
                searchForNaverAPI(searchword);

                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경되었을 때 이벤트 처리
                return false;
            }
        });
        //System.out.println(query);

        RecyclerView recyclerView = findViewById(R.id.listView_search_result_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySearchNaverRecyclerAdapter();
        recyclerView.setAdapter(adapter);

    }

    // 네이버 지역 검색 api 연동 및 데이터 가져오기
    public void searchForNaverAPI(String query) {
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


                    URL url = new URL("https://openapi.naver.com/v1/search/local?"
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
                    array = data.split("\"");
                    title = new String[display];
                    roadaddress = new String[display];
                    mapx = new int[display];
                    mapy = new int[display];
                    //category = new String[display];

                    int k = 0;
                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals("title")) {
                            title[k] = Html.fromHtml(array[i + 2]).toString();
                            System.out.println(array[i + 2]);
                        }
                        if (array[i].equals("roadAddress"))
                            roadaddress[k] = Html.fromHtml(array[i + 2]).toString();
                        if (array[i].equals("mapx"))
                            mapx[k] = Integer.parseInt(array[i + 2]);
                        if (array[i].equals("mapy")) {
                            mapy[k] = Integer.parseInt(array[i + 2]);
                            k++;
                        }
                        /*if (array[i].equals("category")) {
                            category[k] = Html.fromHtml(array[i + 2]).toString();
                            k++;
                        }*/
                    }

                    //System.out.println(array);
                    System.out.println(roadaddress[0]+roadaddress[1]+roadaddress[2]+roadaddress[3]+roadaddress[4]);
                    //System.out.println(category[0]+category[1]+category[2]+category[3]+category[4]);
                    System.out.println("x 좌표 : "+mapx[0]+mapx[1]+mapx[2]+mapx[3]+mapx[4]);
                    System.out.println("y 좌표 : "+mapy[0]+mapy[1]+mapy[2]+mapy[3]+mapy[4]);

                    adapter.addItems(title, roadaddress, /*category,*/ mapx, mapy);
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

}
