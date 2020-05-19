package kr.ac.hansung.demap.ui.hotPlace;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;


public class HotPlaceFragment extends Fragment {
    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HotPlaceRecyclerAdapter adapter;
    private SearchView searchView;
    private String instagram_url = "https://www.instagram.com";
    private WebView webView;
    private String source;
    private String authId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void clearHotPlace() {
        hotPlaceList.clear();
    }

    public void addHotPlace(HotPlaceDTO data) {
        hotPlaceList.add(data);
    }

    void setHotPlaceList(ArrayList<HotPlaceDTO> hotPlaceList) {
        this.hotPlaceList = hotPlaceList;
    }

    void setAuthId(String authId) {
        this.authId = authId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.hotplace_search_fragment, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.rv_hotPlace);
        recyclerView.setHasFixedSize(true);

        adapter = new HotPlaceRecyclerAdapter();
        adapter.setHotPlaceList(hotPlaceList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        /*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                if (lastVisibleItemPosition == itemTotalCount) {
                    webView.setScrollY(5000);
                    webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);");
                }
            }
        });
        */

        searchView = view.findViewById(R.id.sv_hotPlace);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("공백없이 입력해주세요");


        webView = new WebView(this.getActivity());
        //webView = view.findViewById(R.id.wv_hotPlace);
        // WebView 자바스크립트 활성화
        webView.getSettings().setJavaScriptEnabled(true);
        // 자바스크립트인터페이스 연결
        // 이걸 통해 자바스크립트 내에서 자바함수에 접근할 수 있음.
        webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

        // 클릭 시 새 창 안뜨게
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            // WebView에서 처음 한 번만 호출되는 method. 페이지 로딩이 완료된 것을 알림
            // 페이지가 모두 로드되었을 때, 작업 정의
            // 로딩이 다 끝난다고 해서 이미지파일이 다 보일때까지는 아님
            // response를 다 받았다 정도
            @Override
            public void onPageFinished(WebView view, String url) {
                //System.out.println("(2) ########################onPageFinished###############################");
                // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
                try {
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);");
                    Thread.sleep(2000);

                } catch (Exception e) {

                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 버튼을 눌렀을 때
                clearHotPlace();
                //adapter.clearHotPlace();
                String tagUrl = "/explore/tags/" + query;
                //Log.d("(1) #########search_url\n", instagram_url+tagUrl);
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

        return view;

    }

    // 전달받은 html 소스를 Jsoup로 로컬에서 직접 파싱을 한 후, 원하는 작업을 수행
    public class MyJavascriptInterface {
        @JavascriptInterface
        public void getHtml(String html) {
            source = html;
            //System.out.println("(3) ######################## HTML 가져옴 ##############################");

            //위 자바스크립트가 호출되면 여기로 html이 반환됨
            Log.d("javascript_html:", source);
            Document doc = Jsoup.parse(source);
            Elements item_list = doc.select("div.v1Nh3 img");
            int elementSize = item_list.size();
            Log.d("elementSize:", ""+elementSize);

            Elements post_list = doc.select("div.v1Nh3 a");
            //int elementSize = post_list.size();
            //Log.i("elementSize", ""+elementSize);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                ArrayList<String> listComment = new ArrayList<>();
                ArrayList<String> listImageUrl = new ArrayList<>();
                ArrayList<String> listPostUrl = new ArrayList<>();

                for(Element element : item_list) {
                    //System.out.println(" < array에 저장 > ");
                    //if (!listImageUrl.contains(element.attr("src"))){
                        listImageUrl.add(element.attr("src"));
                        //listComment.add(element.attr("alt"));
                    //}
                    //Log.d("imageurl", element.attr("src"));
                }

                for(Element element : post_list) {
                    //if(!listPostUrl.contains("https://www.instagram.com"+element.attr("href"))){
                        listPostUrl.add("https://www.instagram.com"+element.attr("href"));
                        //Log.d("postUrl", element.attr("href"));
                    //}
                }

                for (int i = 0; i < elementSize ; i++) {
                    //System.out.println(" < DTO" + i + "에 저장 > ");
                    HotPlaceDTO hotPlaceDTO = new HotPlaceDTO();
                    hotPlaceDTO.setImageUrl(listImageUrl.get(i));
                    hotPlaceDTO.setTag("#"+searchView.getQuery().toString());
                    hotPlaceDTO.setPostUrl(listPostUrl.get(i));
                    //if(!hotPlaceList.contains(hotPlaceDTO)){
                        hotPlaceList.add(hotPlaceDTO);
                    //}
                }
                adapter.setHotPlaceList(hotPlaceList);
                adapter.notifyDataSetChanged();

            });

        }
    }


}
