package kr.ac.hansung.demap;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;


public class FolderListActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동
    private CollectionReference folderRef = firestore.collection("folders"); // firestore에서 folder 내역 가져오기

    private MyAdapterForFolderList adapter; // FolderList 어댑터

    private ArrayList<FolderDTO> folderDTOs = new ArrayList<FolderDTO>(); // 폴더 리스트를 저장 할 FolderDTO ArrayList 생성


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("폴더 리스트");
        // ActionBar의 배경색 변경
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_folder_list);

        // folders의 모든 도큐먼트 가져오기
        folderRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println(document.getId() + " => " + document.getData());
                                FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                folderDTOs.add(folderDTO);
                                System.out.println(folderDTO.getName());
                                adapter.addItem(folderDTO);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                            System.out.println("Error getting documents: " + task.getException());

                        }
                    }
                });




        //Query query = folderRef.orderBy("timestamp", Query.Direction.ASCENDING);
        //FirestoreRecyclerOptions<FolderDTO> options = new FirestoreRecyclerOptions.Builder<FolderDTO>()
        //        .setQuery(query, FolderDTO.class)
        //        .build();


        RecyclerView recyclerView = findViewById(R.id.listView_folder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapterForFolderList();
        recyclerView.setAdapter(adapter);
/*
        firestore.collection("folders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FolderDTO folderDTO = document.toObject(FolderDTO.class);
                        folderDTOs.add(folderDTO);

                        Log.d("TAG", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }

            }
        });

 */

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