package kr.ac.hansung.demap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.FolderPlacesDTO;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;

public class AddPlaceFormActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //private UserMyFolderDTO userMyfolderDTO = new UserMyFolderDTO();
    //private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();

    //private ArrayList<FolderObj> myfolderObjs = new ArrayList<FolderObj>();
    //private ArrayList<FolderObj> subsfolderObjs = new ArrayList<FolderObj>();

    private PlaceDTO placeDTO = new PlaceDTO();

    //private AddPlaceToFolderRecyclerAdapter adapter;

    // 장소에 관한 데이터를 받아올 인텐트
    private Intent intent;
    // 장소 추가 버튼
    private Button addButton;
    // 분위기 체크박스
    CheckBox study;
    CheckBox dating;
    CheckBox family;
    CheckBox office;
    CheckBox photo;
    CheckBox rest;
    // 편의 체크박스
    CheckBox nokid;
    CheckBox welkid;
    CheckBox gendertoilet;
    CheckBox publictoilet;
    CheckBox stairs;
    CheckBox nostairs;
    CheckBox manyoulet;
    CheckBox lessoulet;

    TextView tv_placename;

    private ArrayList<String> listTags = new ArrayList<String>();

    private HashMap<String, Boolean> tags  = new HashMap();
    private HashMap<String, Boolean> tag  = new HashMap();

    // 저장할 폴더 ID
    String folder_id;

    ArrayList<CheckedFolderId> folder_ids;

    // 폴더에 저장된 장소 count
    int placeCount;

    // 폴더에 저장된 장소 ID 리스트
    FolderPlacesDTO folderPlacesDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_form);

        addButton = findViewById(R.id.addplace_form_btn);
        tv_placename = findViewById(R.id.tv_place_name);

        intent = getIntent();

        // 장소 정보를 placeDTO에 저장
        String name = intent.getStringExtra("result_name");
        placeDTO.setName(name);
        String addr = intent.getStringExtra("result_addr");
        placeDTO.setAddress(addr);
        String category = intent.getStringExtra("result_category");
        placeDTO.setCategory(category);
        String phone = intent.getStringExtra("result_phone");
        placeDTO.setTelephone(phone);
        int x = intent.getIntExtra("result_mapx",0);
        placeDTO.setX(x);
        int y = intent.getIntExtra("result_mapy",0);
        placeDTO.setY(y);

        //folder_ids = (ArrayList<CheckedFolderId>) intent.getSerializableExtra("folder_ids");
        folder_id = intent.getStringExtra("folder_id");

        System.out.println("인텐트로 가져온 장소 : " + placeDTO.getName());
        System.out.println("인텐트로 가져온 폴더 ID : " + folder_id);

        tv_placename.setText(name);

        // 선택된 분위기 태그 가져오기
        study = (CheckBox) findViewById(R.id.study_check);
        dating = (CheckBox)findViewById(R.id.dating_check);
        family = (CheckBox)findViewById(R.id.family_check);
        office = (CheckBox)findViewById(R.id.office_check);
        photo = (CheckBox)findViewById(R.id.photo_check);
        rest = (CheckBox)findViewById(R.id.rest_check);

        study.setOnCheckedChangeListener(this);
        dating.setOnCheckedChangeListener(this);
        family.setOnCheckedChangeListener(this);
        office.setOnCheckedChangeListener(this);
        photo.setOnCheckedChangeListener(this);
        rest.setOnCheckedChangeListener(this);

        // 선택된 편의 태그 가져오기
        nokid = (CheckBox) findViewById(R.id.nokid_check);
        welkid = (CheckBox)findViewById(R.id.welkid_check);
        gendertoilet = (CheckBox)findViewById(R.id.gendertoilet_check);
        publictoilet = (CheckBox)findViewById(R.id.publictoilet_check);
        stairs = (CheckBox)findViewById(R.id.stairs_check);
        nostairs = (CheckBox)findViewById(R.id.nostairs_check);
        manyoulet = (CheckBox)findViewById(R.id.manyoulet_check);
        lessoulet = (CheckBox)findViewById(R.id.lessoulet_check);

        nokid.setOnCheckedChangeListener(this);
        welkid.setOnCheckedChangeListener(this);
        gendertoilet.setOnCheckedChangeListener(this);
        publictoilet.setOnCheckedChangeListener(this);
        stairs.setOnCheckedChangeListener(this);
        nostairs.setOnCheckedChangeListener(this);
        manyoulet.setOnCheckedChangeListener(this);
        lessoulet.setOnCheckedChangeListener(this);


        // 확인 버튼 클릭
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace();
                Toast.makeText(AddPlaceFormActivity.this, "폴더에 장소를 넣었습니다!", Toast.LENGTH_SHORT).show();
                finish();
//                Intent intent2 = new Intent(AddPlaceFormActivity.this, NaverSearchContentActivity.class);
//                intent2.putExtra("result_mapx", placeDTO.getX());
//                intent2.putExtra("result_mapy", placeDTO.getY());
//                intent2.putExtra("result_name", placeDTO.getName());
//                intent2.putExtra("result_addr", placeDTO.getAddress());
//                intent2.putExtra("result_category", placeDTO.getCategory());
//                intent2.putExtra("result_phone", placeDTO.getTelephone());
//                startActivity(intent2);
            }
        });
    }


    public void addPlace() {

        //place 데이터
        placeDTO.setTimestamp(System.currentTimeMillis());
        for(String tag : listTags) {
            tags.put(tag, true);
        }
        placeDTO.setTags(tags);

        firestore.collection("places").add(placeDTO).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                // folder에 장소 id 넣기
                firestore.collection("folderPlaces").document(folder_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                folderPlacesDTO = document.toObject(FolderPlacesDTO.class);
                                folderPlacesDTO.getPlaces().put(documentReference.getId(), true);
                                firestore.collection("folderPlaces").document(folder_id).set(folderPlacesDTO);
                            }
                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    }
                });

                // 장소 count + 1
                firestore.collection("folders").document(folder_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                folderDTO.setPlaceCount(folderDTO.getPlaceCount() + 1);
                                firestore.collection("folders").document(folder_id).set(folderDTO);
                            }
                        }
                    }
                });

            }
        });



    }

    // 체크 박스를 클릭해서 체크상태가 되면 호출되는 콜백 메서드
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listTags.clear();

            if (study.isChecked() == true) {
                listTags.add("공부하기 좋은"); // 체크 활성화 됐으면 서치태그리스트에 추가
            } else {
                listTags.remove("공부하기 좋은"); // 체크 비활성화 되면 서치태그리스트에서 삭제
            }
            if (dating.isChecked() == true) {
                listTags.add("데이트하기 좋은");
            } else {
                listTags.remove("데이트하기 좋은");
            }
            if (family.isChecked() == true) {
                listTags.add("가족모임하기 좋은");
            } else {
                listTags.remove("가족모임하기 좋은");
            }
            if (office.isChecked() == true) {
                listTags.add("회식하기 좋은");
            } else {
                listTags.remove("회식하기 좋은");
            }
            if (photo.isChecked() == true) {
                listTags.add("사진 찍기 좋은");
            } else {
                listTags.remove("사진 찍기 좋은");
            }
            if (rest.isChecked() == true) {
                listTags.add("편안히 쉬기 좋은");
            } else {
                listTags.remove("편안히 쉬기 좋은");
            }
        if (nokid.isChecked() == true) {
            listTags.add("노키드존");
        } else {
            listTags.remove("노키드존");
        }
        if (welkid.isChecked() == true) {
            listTags.add("웰컴키드존");
        } else {
            listTags.remove("웰컴키드존");
        }
        if (gendertoilet.isChecked() == true) {
            listTags.add("남녀화장실 분리");
        } else {
            listTags.remove("남녀화장실 분리");
        }
        if (publictoilet.isChecked() == true) {
            listTags.add("공용 화장실");
        } else {
            listTags.remove("공용 화장실");
        }
        if (stairs.isChecked() == true) {
            listTags.add("계단 있음");
        } else {
            listTags.remove("계단 있음");
        }
        if (nostairs.isChecked() == true) {
            listTags.add("계단 없음");
        } else {
            listTags.remove("계단 없음");
        }
        if (manyoulet.isChecked() == true) {
            listTags.add("콘센트 많음");
        } else {
            listTags.remove("콘센트 많음");
        }
        if (lessoulet.isChecked() == true) {
            listTags.add("콘센트 적음");
        } else {
            listTags.remove("콘센트 적음");
        }

    }

}