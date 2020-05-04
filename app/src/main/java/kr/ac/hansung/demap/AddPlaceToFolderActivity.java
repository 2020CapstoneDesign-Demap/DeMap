package kr.ac.hansung.demap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.ac.hansung.demap.generated.callback.OnClickListener;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;
import kr.ac.hansung.demap.ui.main.MainActivity;

public class AddPlaceToFolderActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private UserMyFolderDTO userMyfolderDTO = new UserMyFolderDTO();
    private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();

    private ArrayList<FolderObj> myfolderObjs = new ArrayList<FolderObj>();
    private ArrayList<FolderObj> subsfolderObjs = new ArrayList<FolderObj>();

    private PlaceDTO placeDTO = new PlaceDTO();

    private AddPlaceToFolderRecyclerAdapter adapter;

    // 장소에 관한 데이터를 받아올 인텐트
    private Intent intentForAddPlace;
    // 장소 추가 버튼
    private Button addButton;

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
        // 장소 추가 버튼 생성
        addButton = findViewById(R.id.btn_checkfolder_add);

        intentForAddPlace = getIntent();
        // 장소 정보를 placeDTO에 저장
        String name = intentForAddPlace.getStringExtra("result_name");
        placeDTO.setName(name);
        String addr = intentForAddPlace.getStringExtra("result_addr");
        placeDTO.setAddress(addr);
        String phone = intentForAddPlace.getStringExtra("result_phone");
        placeDTO.setTelephone(phone);
        int x = intentForAddPlace.getIntExtra("result_mapx",0);
        placeDTO.setX(x);
        int y = intentForAddPlace.getIntExtra("result_mapy",0);
        placeDTO.setY(y);

        System.out.println("인텐트로 가져온 장소 : " + placeDTO.getName()+placeDTO.getTelephone());
        // 장소를 저장할 내 폴더 리스트 가져오기
        setData();

        ArrayList<Integer> indexes = new ArrayList<Integer>();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 장소 저장 폼으로 이동
                Intent  placeFormIntent = new Intent(AddPlaceToFolderActivity.this, AddPlaceFormActivity.class);
                placeFormIntent.putExtra("result_mapx", placeDTO.getX());
                placeFormIntent.putExtra("result_mapy", placeDTO.getY());
                placeFormIntent.putExtra("result_name", placeDTO.getName());
                placeFormIntent.putExtra("result_addr", placeDTO.getAddress());
                placeFormIntent.putExtra("result_phone", placeDTO.getTelephone());
                startActivity(placeFormIntent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listView_checkfolder_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddPlaceToFolderRecyclerAdapter();
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
//                                        FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                            FolderObj folderObj = document.toObject(FolderObj.class);
                                            folderObj.setId(document.getId());
                                            folderObj.setOwner(auth.getCurrentUser().getUid());

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


}
