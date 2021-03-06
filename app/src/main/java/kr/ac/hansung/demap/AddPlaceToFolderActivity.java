package kr.ac.hansung.demap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Key;
import java.util.ArrayList;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderEditorListDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;

public class AddPlaceToFolderActivity extends AppCompatActivity implements FolderList_onClick_interface, View.OnClickListener {

    public static Context addPlaceToFolderContext;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private UserMyFolderDTO userMyfolderDTO = new UserMyFolderDTO();
    private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();

    private ArrayList<FolderObj> myfolderObjs = new ArrayList<FolderObj>();
    private ArrayList<FolderObj> subsfolderObjs = new ArrayList<FolderObj>();

    private PlaceDTO placeDTO = new PlaceDTO();

    public static AddPlaceToFolderRecyclerAdapter adapter;

    // 장소에 관한 데이터를 받아올 인텐트
    private Intent intentForAddPlace;
    // 장소 추가 버튼
    private Button addButton;

    // 체크한 폴더 ID, 소유자
    private String folderId;
    private String folderOwner;
    private String folderName;

    private Map<FolderObj, Key> folderIdMap;
    private ArrayList<CheckedFolderId> folderIds;

    private String nickName;

    private FloatingActionButton fab_main;

    private Intent createFolderIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplacetofolder);

        getSupportActionBar().setTitle("추가할 폴더 선택");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPlaceToFolderContext = this;

        // 플로팅 버튼 생성
        fab_main = (FloatingActionButton) findViewById(R.id.fab);
        fab_main.setOnClickListener(this);

        // 폴더 생성 인텐트
        createFolderIntent = new Intent(AddPlaceToFolderActivity.this, CreateFolderActivity.class);
        createFolderIntent.putExtra("addPlace", 1);

        // 장소 추가 버튼 생성
        addButton = findViewById(R.id.btn_checkfolder_add);

        intentForAddPlace = getIntent();
        // 장소 정보를 placeDTO에 저장
        String name = intentForAddPlace.getStringExtra("result_name");
        placeDTO.setName(name);
        String addr = intentForAddPlace.getStringExtra("result_addr");
        placeDTO.setAddress(addr);
        String category = intentForAddPlace.getStringExtra("result_category");
        placeDTO.setCategory(category);
        String phone = intentForAddPlace.getStringExtra("result_phone");
        placeDTO.setTelephone(phone);
        int x = intentForAddPlace.getIntExtra("result_mapx",0);
        placeDTO.setX(x);
        int y = intentForAddPlace.getIntExtra("result_mapy",0);
        placeDTO.setY(y);

        System.out.println("인텐트로 가져온 장소 : " + placeDTO.getName());

        // 장소를 저장할 내 폴더 리스트 가져오기
        setData();

        ArrayList<Integer> indexes = new ArrayList<Integer>();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                for(CheckedFolderId fid : folderIds) {
                    System.out.println("폴더 아이디 리스트 정상으로 넘어옴 : " + fid.getFolderId());
                }*/
                // 장소 저장 폼으로 이동
                Intent placeFormIntent = new Intent(AddPlaceToFolderActivity.this, AddPlaceFormActivity.class);
                placeFormIntent.putExtra("result_mapx", placeDTO.getX());
                placeFormIntent.putExtra("result_mapy", placeDTO.getY());
                placeFormIntent.putExtra("result_name", placeDTO.getName());
                placeFormIntent.putExtra("result_addr", placeDTO.getAddress());
                placeFormIntent.putExtra("result_phone", placeDTO.getTelephone());
                placeFormIntent.putExtra("result_category", placeDTO.getCategory());

                placeFormIntent.putExtra("folder_id", folderId);
                placeFormIntent.putExtra("folder_owner", folderOwner);
                placeFormIntent.putExtra("folder_name", folderName);

                startActivity(placeFormIntent);
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listView_checkfolder_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddPlaceToFolderRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
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
                                            FolderObj folderObj = document.toObject(FolderObj.class);
                                            folderObj.setId(document.getId());
                                            folderObj.setOwner(auth.getCurrentUser().getUid());

                                            firestore.collection("folderPublic").document(folderObj.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            folderObj.setIspublic(document.getString("public"));

                                                            myfolderObjs.add(folderObj);
                                                        }

                                                        adapter.setItem(myfolderObjs);
                                                        adapter.notifyDataSetChanged();

                                                    } else {
                                                        System.out.println("Error getting documents: " + task.getException());
                                                    }
                                                }
                                            });

//                                            myfolderObjs.add(folderObj);
                                        }

//                                        adapter.setItem(myfolderObjs);
//                                        adapter.notifyDataSetChanged();

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

                        // 현재 로그인한 유저의 닉네임
                        firestore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    nickName = document.get("nickName").toString();

                                    // 구독한 폴더 중 전체 유저 수정 가능이거나 수정권한이 있는 폴더 데이터 가져오기
                                    for (String key : userSubsFolderDTO.getSubscribefolders().keySet()) {

                                        firestore.collection("folderEditors").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String edit = document.get("edit_auth").toString();

                                                    if (edit.equals("전체 유저")) {
                                                        firestore.collection("folders").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        FolderObj folderObj = document.toObject(FolderObj.class);
                                                                        folderObj.setId(document.getId());
                                                                        folderObj.setOwner("notMine");
                                                                        folderObj.setEditable("가능");
                                                                        myfolderObjs.add(folderObj);
                                                                    }

                                                                    adapter.setItem(myfolderObjs);
                                                                    adapter.notifyDataSetChanged();

                                                                } else {
                                                                    System.out.println("Error getting documents: " + task.getException());
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else if (edit.equals("초대한 유저")) {
                                                        firestore.collection("folderEditorList").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document.exists()) {
                                                                    FolderEditorListDTO folderEditorListDTO = document.toObject(FolderEditorListDTO.class);

                                                                    for (String nickname: folderEditorListDTO.getEditors().keySet()) {
                                                                        if (nickname.equals(nickName)) { // 수정 권한이 있는 경우
                                                                            firestore.collection("folders").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        DocumentSnapshot document = task.getResult();
                                                                                        if (document.exists()) {
                                                                                            FolderObj folderObj = document.toObject(FolderObj.class);
                                                                                            folderObj.setId(document.getId());
                                                                                            folderObj.setOwner("notMine");
                                                                                            folderObj.setEditable("가능");
                                                                                            myfolderObjs.add(folderObj);
                                                                                        }

                                                                                        adapter.setItem(myfolderObjs);
                                                                                        adapter.notifyDataSetChanged();

                                                                                    } else {
                                                                                        System.out.println("Error getting documents: " + task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
                                                   }
                                                }
                                            }
                                        });

                                    }

                                }
                            }
                        });


                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

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
/*
    @Override
    public void onCheckbox(Map<String, Boolean> FolderId) {
        for(FolderObj folderObj : myfolderObjs) {
            String folderId = folderObj.getId();
            CheckedFolderId fId = new CheckedFolderId();
            if(FolderId.get(folderId)) { // 해당 폴더id가 해시맵에 존재하고, 그 값이 true이면 폴더id 리스트에 저장
                fId.setFolderId(folderId);
                folderIds.add(fId);
            }
        }

    }
*/
    @Override
    public void onCheckbox(String FolderId, String FolderOwner, String FolderName) {
        folderId = FolderId;
        folderOwner = FolderOwner;
        folderName = FolderName;
    }

    public void addFolderObj(FolderObj folderObj) {
        myfolderObjs.add(folderObj);
        adapter.setItem(myfolderObjs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startActivity(createFolderIntent);
                break;
        }
    }
}
