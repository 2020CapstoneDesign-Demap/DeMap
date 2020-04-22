package kr.ac.hansung.demap;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;

public class MyfolderViewActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동
    private CollectionReference folderRef = firestore.collection("folders"); // firestore에서 folder 내역 가져오기

    private UserMyFolderDTO userMyfolderDTO = new UserMyFolderDTO();
    private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();
    private ArrayList<FolderDTO> folderDTOs = new ArrayList<FolderDTO>(); // 폴더 리스트를 저장 할 FolderDTO ArrayList 생성

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("폴더 리스트");
        // ActionBar의 배경색 변경
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_myfolder_view);



        // usersMyFolder의 현재 로그인한 유저가 소유한 폴더 도큐먼트 이름 가져오기
        firestore.collection("usersMyFolder").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userMyfolderDTO = document.toObject(UserMyFolderDTO.class);
                        //도큐먼트 이름으로 소유한 폴더 데이터 가져오기
                        for (String key : userMyfolderDTO.getMyfolders().keySet()) {
//                            System.out.println("key : " + key + ", value : " + userMyfolderDTO.getMyfolders().get(key));
                            firestore.collection("folders").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
//                                        System.out.println(document.getId() + " => " + document.getData());
                                        FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                        folderDTOs.add(folderDTO);
//                                        System.out.println(folderDTO.getName());
                                        pagerAdapter.addmyfolderItem(folderDTO);
                                        viewPager.setAdapter(pagerAdapter);
                                        pagerAdapter.notifyDataSetChanged();
                                    } else {
                                        //Log.d(TAG, "Error getting documents: ", task.getException());
                                        System.out.println("Error getting documents: " + task.getException());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

        // usersSubsFolder의 현재 로그인한 유저가 구독한 폴더 도큐먼트 이름 가져오기
        firestore.collection("usersSubsFolder").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userSubsFolderDTO = document.toObject(UserSubsFolderDTO.class);
                        //도큐먼트 이름으로 구독한 폴더 데이터 가져오기
                        for (String key : userSubsFolderDTO.getSubscribefolders().keySet()) {
                            System.out.println("key : " + key + ", value : " + userSubsFolderDTO.getSubscribefolders().get(key));
                            firestore.collection("folders").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        System.out.println(document.getId() + " => " + document.getData());
                                        FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                        folderDTOs.add(folderDTO);
//                                        System.out.println(folderDTO.getName());
                                        pagerAdapter.addsubsfolderItem(folderDTO);
                                        viewPager.setAdapter(pagerAdapter);
                                        pagerAdapter.notifyDataSetChanged();
                                    } else {
                                        //Log.d(TAG, "Error getting documents: ", task.getException());
                                        System.out.println("Error getting documents: " + task.getException());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tab_layout_myfolder);
        tabLayout.addTab(tabLayout.newTab().setText("내 폴더"));
        tabLayout.addTab(tabLayout.newTab().setText("구독 폴더"));
        viewPager = findViewById(R.id.viewpager_folder_tab);

        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });


//        RecyclerView recyclerView = findViewById(R.id.listView_folder_list);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new MyAdapterForFolderList();
//        recyclerView.setAdapter(adapter);

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
