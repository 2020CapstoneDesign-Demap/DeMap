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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.hansung.demap.model.FolderObj;
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

    TextView tv_placename;

    private ArrayList<String> listTags = new ArrayList<String>();

    private HashMap<String, Boolean> tags  = new HashMap();
    private HashMap<String, Boolean> tag  = new HashMap();

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
        String phone = intent.getStringExtra("result_phone");
        placeDTO.setTelephone(phone);
        int x = intent.getIntExtra("result_mapx",0);
        placeDTO.setX(x);
        int y = intent.getIntExtra("result_mapy",0);
        placeDTO.setY(y);

        System.out.println("인텐트로 가져온 장소 : " + placeDTO.getName()+placeDTO.getTelephone());

        tv_placename.setText(name);

        // 선택된 태그 가져오기
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


        // 확인 버튼 클릭
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace();
                Intent intent2 = new Intent(AddPlaceFormActivity.this, NaverSearchContentActivity.class);
                startActivity(intent2);
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
        firestore.collection("places").document().set(placeDTO).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("장소 추가 성공 : " + placeDTO);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("장소 추가 실패 : " + e);
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

        }






}
