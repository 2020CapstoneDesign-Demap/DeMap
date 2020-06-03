package kr.ac.hansung.demap.ui.hotPlace;

import android.annotation.SuppressLint;
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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Objects;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class HotPlaceFragment extends Fragment {
    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();
    private HotPlaceRecyclerAdapter adapter;
    private SearchView searchView;
    private String instagram_url = "https://www.instagram.com";
    private WebView webView;
    private String authId;
    private String source;
    private ProgressBar progressBar;

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

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.hotplace_search_fragment, container, false);

        progressBar = view.findViewById(R.id.pg_hotPlace);

        RecyclerView recyclerView = view.findViewById(R.id.rv_hotPlace);
        recyclerView.setHasFixedSize(true);

        adapter = new HotPlaceRecyclerAdapter();
        adapter.setHotPlaceList(hotPlaceList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.sv_hotPlace);
        searchView.setIconifiedByDefault(false);

        webView = view.findViewById(R.id.wv_hotPlace);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
                super.onPageFinished(view, url);
                try {
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('div')[0].innerHTML);");
                    Thread.sleep(2000);
                    adapter.clearHotPlace();
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                } catch (Exception ignored) {

                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((GridLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                if (lastVisibleItemPosition == itemTotalCount) {
                    webView.pageDown(true);
                    webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('div')[0].innerHTML);");
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 버튼을 눌렀을 때
                progressBar.setVisibility(View.VISIBLE);
                searchView.clearFocus();
                clearHotPlace();
                adapter.clearHotPlace();
                adapter.notifyDataSetChanged();

                String tagUrl = "/explore/tags/" + query.replaceAll(" ","");
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
            //위 자바스크립트가 호출되면 여기로 html이 반환됨
            Document doc = Jsoup.parse(source);
            Elements item_list = doc.select("div.v1Nh3 img");
            Elements post_list = doc.select("div.v1Nh3 a");

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                ArrayList<String> listImageUrl = new ArrayList<>();
                ArrayList<String> listPostUrl = new ArrayList<>();

                for(Element element : item_list) {
                    boolean isSearched = false;
                    String imageUrl = element.attr("src");
                    for (HotPlaceDTO hotPlaceDTO : hotPlaceList) {
                        if (hotPlaceDTO.getImageUrl().equals(imageUrl)) {
                            isSearched = true;
                            break;
                        }
                    }
                    if (!isSearched) {
                        listImageUrl.add(imageUrl);
                    }
                }

                for(Element element : post_list) {
                    boolean isSearched = false;
                    String postUrl = "https://www.instagram.com"+element.attr("href");
                    for (HotPlaceDTO hotPlaceDTO : hotPlaceList) {
                        if (hotPlaceDTO.getPostUrl().equals(postUrl)) {
                            isSearched = true;
                            break;
                        }
                    }
                    if (!isSearched) {
                        listPostUrl.add(postUrl);
                    }
                }

                for (int i = 0; i < listPostUrl.size() ; i++) {
                    HotPlaceDTO hotPlaceDTO = new HotPlaceDTO();
                    hotPlaceDTO.setImageUrl(listImageUrl.get(i));
                    hotPlaceDTO.setPostUrl(listPostUrl.get(i));
                    hotPlaceDTO.setTag("#"+searchView.getQuery().toString());
                    addHotPlace(hotPlaceDTO);
                }

                setHotPlaceList(hotPlaceList);
                adapter.notifyDataSetChanged();

            });

        }
    }


}
