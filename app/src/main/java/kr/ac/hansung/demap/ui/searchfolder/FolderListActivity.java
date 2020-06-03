package kr.ac.hansung.demap.ui.searchfolder;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.getCount_interface;
import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.UserMyFolderDTO;


public class FolderListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, getCount_interface {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동

    private MyAdapterForFolderList adapter; // FolderList 어댑터

    private ArrayList<FolderDTO> folderDTOs = new ArrayList<FolderDTO>(); // 폴더 리스트를 저장 할 FolderDTO ArrayList 생성
    private ArrayList<FolderObj> folderObjs = new ArrayList<FolderObj>(); // 폴더 관련 모든 데이터를 저장 할 FolderObj ArrayList 생성
    private ArrayList<FolderObj> searchByNicknameFolderObjs = new ArrayList<FolderObj>(); // 구독 가능 폴더 리스트를 저장 할 FolderObj ArrayList 생성
    private ArrayList<String> subFolderIds = new ArrayList<String>(); // 구독 가능 폴더 id만 담아 놓을 배열리스트
    private ArrayList<FolderObj> searchFolderResult = new ArrayList<FolderObj>(); // 폴더명 검색 결과 폴더 리스트를 저장 할 FolderObj ArrayList 생성
    private ArrayList<String> tagsForSearch = new ArrayList<String>(); // 서치할 태그 저장
    private UserMyFolderDTO nicknameResultFolderDTO = new UserMyFolderDTO();
    private ArrayList<FolderObj> tmpNicknameResultObjs = new ArrayList<FolderObj>(); // 닉네임 검색 결과 폴더 임시 저장 리스트(공개여부검증전)

    // 체크박스
    CheckBox restaurant;
    CheckBox cafe;
    CheckBox tour;
    CheckBox sport;
    CheckBox entertain;

    private TextView tv_search_result;

    // 구독 리스너 -> 사용할지 안할지 모르겠음
    //private ListenerRegistration followListenerRegistration = null;
    //private ListenerRegistration followingListenerRegistration = null;
    private Spinner spinner;

    private String result_set = null;
    private TextView tv_result_set;
    private String search_key;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("폴더 검색");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_folder_list);

        // Spinner 선택 리스트 가져오기
        final String[] data = getResources().getStringArray(R.array.searchresult); // xml에서 가져올때 get 리소스
        // Spinner 선언/객체화, 데이터와 화면 연결할 spinner adapter생성, Spinner와 어댑터 연결
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,data);
        spinner = (Spinner)findViewById(R.id.result_spinner);
        spinner.setAdapter(arrayAdapter);

        tv_result_set = findViewById(R.id.result_tv);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0 :
                        result_set = "all";
                        tv_result_set.setText("전체");
                        if(search_key != null) {
                            searchFolderResult.clear();
                            searchForFolderOwner(search_key);
                            searchForFolderName(search_key);
                        }
                        break;
                    case 1 :
                        result_set = "folder name";
                        tv_result_set.setText("폴더명");
                        if(search_key != null) {
                            searchFolderResult.clear();
                            searchForFolderName(search_key);
                        }
                        break;
                    case 2 :
                        result_set = "nick name";
                        tv_result_set.setText("닉네임");
                        if(search_key != null) {
                            searchFolderResult.clear();
                            searchForFolderOwner(search_key);
                        }

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                result_set = "all";
            }
        });


        //구독 가능한 폴더 리스트
        setSubableIDList();
        setMyFolderIdList();

        // 검색어 가져오기
        SearchView searchView =  findViewById(R.id.folder_name_edittext);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                // 검색 버튼이 눌러졌을 때 이벤트 처리
                System.out.println("검색 처리됨 : " + keyword);

                        search_key = keyword;
                        searchForFolderOwner(keyword);
                        searchForFolderName(keyword);

                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경되었을 때 이벤트 처리
                return false;
            }
        });

        // 선택된 태그 가져오기
        restaurant = (CheckBox) findViewById(R.id.restau_check);
        cafe = (CheckBox)findViewById(R.id.cafe_check);
        tour = (CheckBox)findViewById(R.id.tour_check);
        sport = (CheckBox)findViewById(R.id.sport_check);
        entertain = (CheckBox)findViewById(R.id.enter_check);

        restaurant.setOnCheckedChangeListener(this);
        cafe.setOnCheckedChangeListener(this);
        tour.setOnCheckedChangeListener(this);
        sport.setOnCheckedChangeListener(this);
        entertain.setOnCheckedChangeListener(this);

        tv_search_result = findViewById(R.id.tv_search_result);

        RecyclerView recyclerView = findViewById(R.id.listView_folder_list);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapterForFolderList(this);
        recyclerView.setAdapter(adapter);

    }


    public void setMyFolderIdList() {
        // usersSubsFolder의 현재 로그인한 유저가 구독한 폴더 도큐먼트 이름 가져오기
        firestore.collection("usersMyFolder").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserMyFolderDTO userMyFolderDTO = document.toObject(UserMyFolderDTO.class);
                        ArrayList<String> userMyFolderId = new ArrayList<String>();
                        userMyFolderId.addAll(userMyFolderDTO.getMyfolders().keySet());
                        adapter.setMyFolderList(userMyFolderId,auth.getCurrentUser().getUid());
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });
    }

    public void searchForFolderOwner(String nickName) {
        searchByNicknameFolderObjs.clear();
        tmpNicknameResultObjs.clear();
        firestore.collection("users")
                .whereEqualTo("nickName", nickName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uid = document.getId();
                                System.out.println("검색한 닉네임의 uid : "+ uid);

                                firestore.collection("usersMyFolder").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                nicknameResultFolderDTO = document.toObject(UserMyFolderDTO.class);
                                                // 닉네임에 해당하는 유저 uid로 소유한 폴더 상세 데이터 가져오기
                                                for (String key : nicknameResultFolderDTO.getMyfolders().keySet()) {
                                                    System.out.println("닉네임으로 폴더 검색 결과 : "+key);

                                                    for(FolderObj tmpFolder : folderObjs) {
                                                        if(tmpFolder.getId().equals(key)==true) {
                                                            searchByNicknameFolderObjs.add(tmpFolder);
                                                            if(!searchFolderResult.contains(tmpFolder)) {
                                                                searchFolderResult.add(tmpFolder);
                                                            }
                                                            for(FolderObj tmpFolder2 : searchFolderResult) {
                                                                System.out.println("닉네임 폴더 검색 최종 결과 : "+tmpFolder2.getName());
                                                            }

                                                            adapter.addItems(searchFolderResult);
                                                            adapter.notifyDataSetChanged();

                                                        }
                                                    }

                                                }

                                            }

                                        } else {
                                            System.out.println("Error getting documents: " + task.getException());
                                        }
                                    }
                                });

                            }

                        } else {
                        }
                    }
                });

    }


    // 구독 가능한 폴더 리스트에서 폴더명 키워드로 검색
    public void searchForFolderName(String keyword) {
        // 검색 결과 리스트 초기화
        searchFolderResult.clear();

        //searchForFolderOwner(keyword);

    for (FolderObj tempfolder : folderObjs) {
        // 키워드가 들어간 폴더들을 긁어와서
        // 그 폴더들의 아이디가 구독가능폴더아이디 리스트에 있는거면
        // 검색결과 리스트에 넣는다
        // 그리고 그걸 화면에 보여줌
        //System.out.println("함수는 돌아감");
        String str1 = tempfolder.getName();
        boolean b = str1.toLowerCase().contains(keyword.toLowerCase());
        //System.out.println("키워드 검사 논리 결과 : "+ b);
        if (b) {
            //System.out.println("키워드 여부 검사 성공");
            if(!searchFolderResult.contains(tempfolder)) {
                searchFolderResult.add(tempfolder);
            }
                System.out.println(tempfolder.getName() + " : " + tempfolder.getId() + " , " + tempfolder.getIspublic());

        }

    }

        adapter.addItems(searchFolderResult);
        adapter.notifyDataSetChanged();

    }

    // 체크 박스를 클릭해서 체크상태가 되면 호출되는 콜백 메서드
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        tagsForSearch.clear();
        if(!searchFolderResult.isEmpty()) {
            if (restaurant.isChecked() == true) {
                tagsForSearch.add("맛집"); // 체크 활성화 됐으면 서치태그리스트에 추가
                System.out.println("태그 클릭 : 맛집");
            } else {
                tagsForSearch.remove("맛집"); // 체크 비활성화 되면 서치태그리스트에서 삭제
            }
            if (cafe.isChecked() == true) {
                tagsForSearch.add("카페");
                System.out.println("태그 클릭 : 카페");
            } else {
                tagsForSearch.remove("카페");
            }
            if (tour.isChecked() == true) {
                tagsForSearch.add("관광지");
                System.out.println("태그 클릭 : 관광지");
            } else {
                tagsForSearch.remove("관광지");
            }
            if (sport.isChecked() == true) {
                tagsForSearch.add("스포츠");
                System.out.println("태그 클릭 : 스포츠");
            } else {
                tagsForSearch.remove("스포츠");
            }
            if (entertain.isChecked() == true) {
                tagsForSearch.add("공연/전시");
                System.out.println("태그 클릭 : 공연/전시");
            } else {
                tagsForSearch.remove("공연/전시");
            }

            searchForFolderTag(/*tagsForSearch*/);
        }

    }

    // 구독 가능한 폴더 리스트에서 태그로 검색
    public void searchForFolderTag(/*ArrayList<String> tagsForSearch*/) {
        ArrayList<FolderObj> searchTagNameResult = new ArrayList<FolderObj>();
        // 검색 결과 리스트 초기화
        //searchFolderResult.clear();
        for(FolderObj tempfolder : searchFolderResult) {
            // 폴더들을 가져와서
            // 그 폴더들의 태그에 넘어온 태그가 존재하면
            // 검색결과 리스트에 넣는다
            // 그리고 그걸 화면에 보여줌
            String str1 = tempfolder.getTag();
            for(String tag : tagsForSearch) {
                if(str1.equals(tag)) {
                    System.out.println("태그 검색 성공");
                    searchTagNameResult.add(tempfolder);
                    System.out.println(tempfolder.getName()+" : "+tempfolder.getId()+" , "+tempfolder.getTag());
                }
            }

        }

        // 서치태그 리스트가 비었으면 태그검색을 취소한 것이므로 키워드 검색 결과만 보여줌
        if(tagsForSearch.isEmpty())
            adapter.addItems(searchFolderResult);
        else
            adapter.addItems(searchTagNameResult);
        //searchFolderResult.clear();
        adapter.notifyDataSetChanged();

    }


    // 구독 가능한 폴더 id만 리스트로 저장
    public void setSubableIDList() {

        // firestore에서 folderPublic 내역 가져오기
        CollectionReference folderPublicRef = firestore.collection("folderPublic");

        // 구독 가능한 foler 도큐먼트 id 가져오기
        folderPublicRef.whereEqualTo("public", "공개").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getId() + " => " + document.getData());
                                String subId = document.getId();
                                subFolderIds.add(subId);
                            }

                            setSubableFolderList();

                        } else {
                            System.out.println("Error getting documents: " + task.getException());

                        }
                    }
                });

    }

    // 구독 가능한 폴더 전체 정보를 리스트로 저장
    public void setSubableFolderList( ) {

        for ( String subId : subFolderIds ) {

            // 공개 설정한 폴더 id로 folders의 폴더 도큐먼트 가져오기
            firestore.collection("folders").document(subId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            // 가져온 도큐먼트를 folderObj객체로 저장
                            FolderObj folderObj = document.toObject(FolderObj.class);

                            // 폴더 객체에 폴더 정보 삽입
                            folderObj.setId(document.getId());
                            folderObj.setIspublic("공개");

                            folderObjs.add(folderObj);

                        }

                        Collections.sort(folderObjs);
                        setFolderTags();

                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                }
            });

        }

    }

    public void setFolderTags() {

        for ( FolderObj folderObj : folderObjs ) {

            // 공개 설정한 폴더 id로 폴더 태그 가져오기
            firestore.collection("folderTags").document(folderObj.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String folderTag = document.get("folderTag").toString();
                            folderObj.setTag(folderTag);
                            folderObjs.set(folderObjs.indexOf(folderObj), folderObj);

//                            adapter.setItem(folderObjs);
                        }

//                        adapter.addItem(folderObj);
//                        adapter.notifyDataSetChanged();

                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                }
            });

        }

    }

    @Override
    public void getCount(int count) {
        if(searchFolderResult.size() == 0 || searchFolderResult.get(0).getName() == " 검색 결과가 존재하지 않습니다. ") {
            tv_search_result.setText(String.valueOf(0));
        } else {
            tv_search_result.setText(String.valueOf(count));
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

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

}
