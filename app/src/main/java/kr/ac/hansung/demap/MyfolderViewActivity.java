package kr.ac.hansung.demap;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;

public class MyfolderViewActivity extends AppCompatActivity {
    public static Context mContext;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private UserMyFolderDTO userMyfolderDTO = new UserMyFolderDTO();
    private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();

    private ArrayList<FolderObj> myfolderObjs = new ArrayList<FolderObj>();
    private ArrayList<FolderObj> subsfolderObjs = new ArrayList<FolderObj>();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("폴더 리스트");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_myfolder_view);
        mContext = this;


        setData();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_myfolder);
        tabLayout.addTab(tabLayout.newTab().setText("내 폴더"));
        tabLayout.addTab(tabLayout.newTab().setText("구독 폴더"));
        viewPager = findViewById(R.id.viewpager_folder_tab);

        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

    }

    public void setAdapterItem(int position, FolderDTO folderDTO, String folderId) {
        pagerAdapter.setUpdate(position, folderDTO, folderId);
        pagerAdapter.notifyDataSetChanged();
    }


    public void setData() {

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
                            firestore.collection("folders").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
//                                        FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                            FolderObj folderObj = document.toObject(FolderObj.class);
                                            folderObj.setId(document.getId());
                                            folderObj.setOwner(auth.getCurrentUser().getUid());

                                            myfolderObjs.add(folderObj);
                                        }

                                        getPublic(myfolderObjs);

                                        pagerAdapter.setmyfolderItem(myfolderObjs);
                                        pagerAdapter.setAuthId(auth.getCurrentUser().getUid());
                                        viewPager.setAdapter(pagerAdapter);
                                        pagerAdapter.notifyDataSetChanged();

                                    } else {
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
                            firestore.collection("folders").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
//                                            FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                            FolderObj folderObj = document.toObject(FolderObj.class);
                                            folderObj.setId(document.getId());
                                            subsfolderObjs.add(folderObj);
                                        }

                                        getPublic(subsfolderObjs);

                                        pagerAdapter.setsubsfolderItem(subsfolderObjs);
                                        pagerAdapter.setAuthId(auth.getCurrentUser().getUid());
                                        viewPager.setAdapter(pagerAdapter);
                                        pagerAdapter.notifyDataSetChanged();

                                    } else {
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

    }

    public void getPublic(ArrayList<FolderObj> folderObjs) {
        for (FolderObj folderObj: folderObjs) {
            firestore.collection("folderPublic").document(folderObj.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        folderObj.setIspublic(document.getString("public"));
                    }
                }
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
