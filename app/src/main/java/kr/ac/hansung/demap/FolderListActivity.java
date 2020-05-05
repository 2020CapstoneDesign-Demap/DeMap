package kr.ac.hansung.demap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.FolderSubsDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;
import kr.ac.hansung.demap.ui.main.MainActivity;


public class FolderListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동
    //private CollectionReference folderRef = firestore.collection("folders"); // firestore에서 folder 내역 가져오기
    //private CollectionReference folderPublicRef = firestore.collection("folderPublic"); // firestore에서 folderPublic 내역 가져오기
    //private CollectionReference subableFolderRef = firestore.collection("folders"); // firestore에서 folders 내역 가져오기

    //private String destinationUid;
    //private String uid = auth.currentUser.uid;
    //private String currentUserUid = null;

    private MyAdapterForFolderList adapter; // FolderList 어댑터

    private ArrayList<FolderDTO> folderDTOs = new ArrayList<FolderDTO>(); // 폴더 리스트를 저장 할 FolderDTO ArrayList 생성
    private ArrayList<FolderObj> folderObjs = new ArrayList<FolderObj>(); // 폴더 관련 모든 데이터를 저장 할 FolderObj ArrayList 생성
    private ArrayList<FolderObj> subableFolderObjs = new ArrayList<FolderObj>(); // 구독 가능 폴더 리스트를 저장 할 FolderObj ArrayList 생성
    private ArrayList<String> subFolderIds = new ArrayList<String>(); // 구독 가능 폴더 id만 담아 놓을 배열리스트
    private ArrayList<FolderObj> searchFolderResult = new ArrayList<FolderObj>(); // 폴더명 검색 결과 폴더 리스트를 저장 할 FolderObj ArrayList 생성
    private ArrayList<String> tagsForSearch = new ArrayList<String>(); // 서치할 태그 저장

    // 체크박스
    CheckBox restaurant;
    CheckBox cafe;
    CheckBox tour;
    CheckBox sport;
    CheckBox entertain;

    // 구독 리스너 -> 사용할지 안할지 모르겠음
    //private ListenerRegistration followListenerRegistration = null;
    //private ListenerRegistration followingListenerRegistration = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("폴더 검색");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_folder_list);


        //구독 가능한 폴더 리스트
        setSubableIDList();

        // 검색어 가져오기
        SearchView searchView =  findViewById(R.id.folder_name_edittext);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                // 검색 버튼이 눌러졌을 때 이벤트 처리
                System.out.println("검색 처리됨 : " + keyword);
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




      // for ( FolderObj temp : subableFolderObjs )
        //    System.out.println("구독 가능 폴더 : " + temp.getId() + temp.getName() + temp.getIspublic());



        RecyclerView recyclerView = findViewById(R.id.listView_folder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapterForFolderList();
        recyclerView.setAdapter(adapter);

    }

    // 구독 가능한 폴더 리스트에서 폴더명 키워드로 검색
    public void searchForFolderName(String keyword) {
        // 검색 결과 리스트 초기화
        searchFolderResult.clear();
        for(FolderObj tempfolder : folderObjs) {
            // 키워드가 들어간 폴더들을 긁어와서
            // 그 폴더들의 아이디가 구독가능폴더아이디 리스트에 있는거면
            // 검색결과 리스트에 넣는다
            // 그리고 그걸 화면에 보여줌
            //System.out.println("함수는 돌아감");
            String str1 = tempfolder.getName();
            boolean b = str1.toLowerCase().contains(keyword.toLowerCase());
            //System.out.println("키워드 검사 논리 결과 : "+ b);
            if(b) {
                //System.out.println("키워드 여부 검사 성공");
                searchFolderResult.add(tempfolder);
                System.out.println(tempfolder.getName()+" : "+tempfolder.getId()+" , "+tempfolder.getIspublic());
                /*
                for(FolderObj result : searchFolderResult) {
                    int flag=0;
                    if(tempfolder.equals(result))
                        flag++;
                }*/


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

        //if(!tagsForSearch.isEmpty())

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
            //System.out.println("함수는 돌아감");
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

//    public void createFolderInfoList() {
//        CollectionReference folderRef = firestore.collection("folders");
//        // folders의 모든 도큐먼트 가져오기
//        folderRef.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //Log.d(TAG, document.getId() + " => " + document.getData());
//                                System.out.println(document.getId() + " => " + document.getData());
//                                // 가져온 도큐먼트를 folderDTO객체로 저장
//                                FolderDTO folderDTO = document.toObject(FolderDTO.class);
//                                // 가져온 도큐먼트를 folderObj객체로 저장
//                                FolderObj folderObj = document.toObject(FolderObj.class);
//                                folderObj.setId(document.getId()); // 폴더객체에 폴더도큐먼트id 삽입
//                                folderDTOs.add(folderDTO);
//                                folderObjs.add(folderObj);
//                                System.out.println("폴더디티오 : " + folderDTO.getName());
//                                System.out.println("폴더오비제 : " + folderObj.getId());
//                                adapter.addItem(folderDTO);
//                            }
//                            setSubableIDList();
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            //Log.d(TAG, "Error getting documents: ", task.getException());
//                            System.out.println("Error getting documents: " + task.getException());
//
//                        }
//                    }
//                });
//
//    }

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
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println(document.getId() + " => " + document.getData());
                                String subId = document.getId();
                                subFolderIds.add(subId);
                            }

                            setSubableFolderList();

                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
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

                            // 가져온 도큐먼트를 folderDTO객체로 저장
//                            FolderDTO folderDTO = document.toObject(FolderDTO.class);
                            // 가져온 도큐먼트를 folderObj객체로 저장
                            FolderObj folderObj = document.toObject(FolderObj.class);

                            // 폴더 객체에 폴더 정보 삽입
                            folderObj.setId(document.getId());
                            folderObj.setIspublic("공개");

//                            folderDTOs.add(folderDTO);
                            folderObjs.add(folderObj);

//                            System.out.println("폴더디티오 : " + folderDTO.getName());
//                            System.out.println("폴더오비제 : " + folderObj.getId());

                        }

                        setFolderTags();

                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                }
            });

        }


        // 전체 folder 리스트를 for문 돌려 구독 가능 폴더 id와 비교
//        for ( FolderObj fObj : folderObjs ) {
//            int index = folderObjs.indexOf(fObj);
//            System.out.println("folderObjs index : " + index);
//
//            for ( String subId : subFolderIds ) {
//                if (fObj.getId().equals(subId)){ // 전체 폴더 중 구독 가능 폴더 id 리스트에 존재하는 폴더 id라면
//                    //FolderDTO publicfolderDTO = documentSub.toObject(FolderDTO.class);
//                    // 폴더 공개 여부를 "공개"로 설정 후 변경 사항 배열리스트에 set 시키기
//                    fObj.setIspublic("공개");
//                    folderObjs.set(folderObjs.indexOf(fObj), fObj);
//                    // 공개된 폴더를 구독가능폴더리스트에 저장
//                    subableFolderObjs.add(fObj);
//                    subscribeFolder(subFolderIds.indexOf(subId));
//                }
//
//            }
//
//            System.out.println("구독 가능 여부 : " + fObj.getId() + fObj.getName() + fObj.getIspublic());
//
//        }

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

/*
    public void getAllFolderList() {
        Thread allFolderListThread = new Thread(new AllFolderListThread(folderRef, folderDTOs, folderObjs));
        try {
            allFolderListThread.start();
            allFolderListThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (FolderDTO folderDTO : folderDTOs) {
            adapter.addItem(folderDTO);
            adapter.notifyDataSetChanged();
        }


    }
*/
    /*
    fun getFollowing() {
        followingListenerRegistration = firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)
            if (followDTO == null) return@addSnapshotListener
                    fragmentView!!.account_tv_following_count.text = followDTO?.followingCount.toString()
        }
    }*/

/*
    fun getFollower() {

        followListenerRegistration = firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)
            if (followDTO == null) return@addSnapshotListener
                    fragmentView?.account_tv_follower_count?.text = followDTO?.followerCount.toString()
            if (followDTO?.followers?.containsKey(currentUserUid)!!) {

                fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow_cancel)
                fragmentView?.account_btn_follow_signout
                        ?.background
                        ?.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorLightGray), PorterDuff.Mode.MULTIPLY)
            } else {

                if (uid != currentUserUid) {

                    fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
                    fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                }
            }

        }

    }
*/

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

//class FolderListActivity : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: RecyclerView.Adapter<*>
//    private lateinit var viewManager: RecyclerView.LayoutManager
//
//    var auth: FirebaseAuth? = null
//    var firestore: FirebaseFirestore? = null
//
//    var item_folder_list = arrayOf<FolderDTO>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        //Initiate
//        auth = FirebaseAuth.getInstance()
//        firestore = FirebaseFirestore.getInstance()
//
//        // ActionBar에 타이틀 변경
//        getSupportActionBar()?.setTitle("폴더 리스트");
//        // ActionBar의 배경색 변경
//        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())
//
//        // 홈 아이콘 표시
//        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
//
//        setContentView(R.layout.activity_folder_list)
//
//        getData()
//
//        viewManager = LinearLayoutManager(this)
//        viewAdapter = MyAdapterForFolderList()
//        recyclerView = findViewById<RecyclerView>(R.id.listView_folder_list).apply {
//            setHasFixedSize(true)
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }
//    }
//
//    fun getData() {
//        item_folder_list.apply {
//            //folder 데이터
//            var folderDTO = arrayOf<FolderDTO>()
//            firestore?.collection("folders")?.get()?.addOnSuccessListener {
//
//            }
//
//            firestore?.collection("folders")?.add(folderDTO)?.addOnSuccessListener {
//
//                var folderID = it.id //도큐먼트 ID 가져옴
//
//                //폴더 생성자 정보 저장
//                var owner: MutableMap<String, Any> = HashMap()
//                owner.put("owner", auth?.currentUser?.uid as Any)
//                firestore?.collection("folderOwner")?.document(folderID)?.set(owner)
//
//                //폴더 구독자 정보 저장
//                var subscriber: MutableMap<String, Any> = HashMap()
//                var subscribers : MutableMap<String, Any> = HashMap()
//                subscriber.put("subscribers", subscribers)
//                firestore?.collection("folderSubscribers")?.document(folderID)?.set(subscriber)
//
//                // 내 폴더에 추가
//                var doc = firestore?.collection("usersMyFolder")?.document(auth?.currentUser?.uid!!)
//                firestore?.runTransaction {
//                    var userMyfolderDTO = UserMyFolderDTO()
//                    if (it.get(doc!!).toObject(UserMyFolderDTO::class.java) == null) { //리스트에 처음 들어갈 경우
//                        userMyfolderDTO.myfolders[folderID] = true
//                    }
//                    else {
//                        userMyfolderDTO = it.get(doc).toObject(UserMyFolderDTO::class.java)!!
//                        userMyfolderDTO.myfolders[folderID] = true
//                    }
//                    it.set(doc, userMyfolderDTO)
//                }
//
//                //폴더 공개 범위 저장
//                var public: MutableMap<String, Any> = HashMap()
//                public.put("public", item_pub[position[0]!!]) //어댑터에서 받아온 데이터 저장
//                firestore?.collection("folderPublic")?.document(folderID)?.set(public)
//
//                //폴더 수정 권한 저장
//                var edit_auth: MutableMap<String, Any> = HashMap()
//                edit_auth.put("edit_auth", item_edit_auth[position[1]!!])
//                firestore?.collection("folderEditors")?.document(folderID)?.set(edit_auth)
//
//                //폴더 태그 저장
//                var folderTag: MutableMap<String, Any> = HashMap()
//                folderTag.put("folderTag", item_folder_tag[position[2]!!])
//                firestore?.collection("folderTags")?.document(folderID)?.set(folderTag)
//            }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        var id = item.getItemId()
////
////        if (id == android.R.id.home) {
////            finish()
////            return true
////        }
////        return super.onOptionsItemSelected(item)
////    }
//}