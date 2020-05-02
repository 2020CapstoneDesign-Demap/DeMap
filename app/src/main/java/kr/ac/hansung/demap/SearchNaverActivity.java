package kr.ac.hansung.demap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class SearchNaverActivity extends AppCompatActivity {

    private boolean inItem = false, inTitle = false, inAddress = false, inMapx = false, inMapy = false;
    private String title = null, address = null, mapx = null, mapy = null;
    //private String query = null; //이부분은 검색어를 UTF-8로 넣어줄거임.

    //파싱된 결과를 보여줄 textVeiw들
    private TextView status1;
    private TextView status2;

    private BufferedReader br;
    private StringBuilder searchResult;


    @Override
        public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_naver);

        status1 = (TextView)findViewById(R.id.textview_searchresult_name); //파싱된 결과를 보자
        status2 = (TextView)findViewById(R.id.textview_seatchresult_address);

        // 검색어 가져오기
        SearchView searchView =  findViewById(R.id.folder_name_edittext);
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



    }

    /*
    public void searchNaver(final String searchObject) { // 검색어 = searchObject로 ;
        final String clientId = "F29Q2vNcHyw0fOQwkzbO";//애플리케이션 클라이언트 아이디값";
        final String clientSecret = "5dNDcpf9qo";//애플리케이션 클라이언트 시크릿값";
        final int display = 5; // 보여지는 검색결과의 수

        // 네트워크 연결은 Thread 생성 필요
        new Thread() {

            @Override
            public void run() {
                try {
                    String text = URLEncoder.encode(searchObject, "UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text + "&display=" + display + "&"; // json 결과
                    // Json 형태로 결과값을 받아옴.
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
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

                    String data = searchResult.toString();
                    String[] array;
                    array = data.split("\"");
                    String[] title = new String[display];
                    String[] link = new String[display];
                    String[] description = new String[display];
                    String[] bloggername = new String[display];
                    String[] postdate = new String[display];

                    int k = 0;
                    for (int i = 0; i < array.length; i++) {
                        if (array[i].equals("title"))
                            title[k] = array[i + 2];
                        if (array[i].equals("link"))
                            link[k] = array[i + 2];
                        if (array[i].equals("description"))
                            description[k] = array[i + 2];
                        if (array[i].equals("bloggername"))
                            bloggername[k] = array[i + 2];
                        if (array[i].equals("postdate")) {
                            postdate[k] = array[i + 2];
                            k++;
                        }
                    }

                    System.out.println("title잘나오니: " + title[0] + title[1] + title[2]);
                    // title[0], link[0], bloggername[0] 등 인덱스 값에 맞게 검색결과를 변수화하였다.

                } catch (Exception e) {
                    System.out.println("error : " + e);
                }

            }
        }.start();

    }
*/

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
                    String data = searchResult.toString();
                    String[] array;
                    array = data.split("\"");
                    String[] title = new String[display];
                    String[] roadaddress = new String[display];
                    int[] mapx = new int[display];
                    int[] mapy = new int[display];
                    //String[] postdate = new String[display];

                    int k = 0;
                    for (int i = 0; i < array.length; i++) {
                        System.out.println(array[i]);

                        if (array[i].equals("title")) {
                            title[k] = array[i + 2];
                            System.out.println(array[i + 2]);
                        }
                        if (array[i].equals("roadAddress"))
                            roadaddress[k] = array[i + 2];
                        if (array[i].equals("mapx"))
                            mapx[k] = Integer.parseInt(array[i + 2]);
                        if (array[i].equals("mapy")) {
                            mapy[k] = Integer.parseInt(array[i + 2]);
                            k++;
                        }
                        //if (array[i].equals("postdate")) {
                        //    postdate[k] = array[i + 2];
                        //    k++;
                        //}
                    }

                    System.out.println("title잘나오니: " + title[0] + title[1] + title[2]);
                    // title[0], link[0], bloggername[0] 등 인덱스 값에 맞게 검색결과를 변수화하였다.

                    /*
                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();

                    parser.setInput(url.openStream(), null);


                    int parserEvent = parser.getEventType();

                    while (parserEvent != XmlPullParser.END_DOCUMENT) {
                        switch (parserEvent) {
                            case XmlPullParser.START_TAG:  //parser가 시작 태그를 만나면 실행
                                if (parser.getName().equals("item")) {
                                    inItem = true;
                                }
                                if (parser.getName().equals("title")) { //title 만나면 내용을 받을수 있게 하자
                                    inTitle = true;
                                }
                                if (parser.getName().equals("address")) { //address 만나면 내용을 받을수 있게 하자
                                    inAddress = true;
                                }
                                if (parser.getName().equals("mapx")) { //mapx 만나면 내용을 받을수 있게 하자
                                    inMapx = true;
                                }
                                if (parser.getName().equals("mapy")) { //mapy 만나면 내용을 받을수 있게 하자
                                    inMapy = true;
                                }
                                if (parser.getName().equals("message")) { //message 태그를 만나면 에러 출력
                                    System.out.println("에러");
                                    //status1.setText(status1.getText() + "에러");
                                    //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                                }
                                break;

                            case XmlPullParser.TEXT://parser가 내용에 접근했을때
                                if (inTitle) { //isTitle이 true일 때 태그의 내용을 저장.
                                    title = parser.getText();
                                    inTitle = false;
                                }
                                if (inAddress) { //isAddress이 true일 때 태그의 내용을 저장.
                                    address = parser.getText();
                                    inAddress = false;
                                }
                                if (inMapx) { //isMapx이 true일 때 태그의 내용을 저장.
                                    mapx = parser.getText();
                                    inMapx = false;
                                }
                                if (inMapy) { //isMapy이 true일 때 태그의 내용을 저장.
                                    mapy = parser.getText();
                                    inMapy = false;
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("item")) {
                                    //status1.setText(status1.getText() + "상호 : " + title + "\n주소 : " + address + "\n좌표 : " + mapx + ", " + mapy + "\n\n");
                                    System.out.println("검색API성공 : " + title + address);
                                    inItem = false;
                                }
                                break;
                        }
                        parserEvent = parser.next();
                    }
                    //status2.setText("파싱 끝!");
                    System.out.println("파싱 성공"); */
                } catch (
                        Exception e) {
                    //status1.setText("에러가..났습니다...");
                    System.out.println("에러 발생 : " + e);
                }
            }
        }.start();
    }

}
