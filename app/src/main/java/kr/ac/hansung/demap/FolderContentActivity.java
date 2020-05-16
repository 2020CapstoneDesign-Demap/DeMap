package kr.ac.hansung.demap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.FolderPlacesDTO;
import kr.ac.hansung.demap.model.FolderSubsDTO;
import kr.ac.hansung.demap.model.NoticeDTO;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;


public class FolderContentActivity extends AppCompatActivity {

    public static Context mContext;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동

//    private Intent intent = getIntent();

    private String docId;
    private boolean isMyFolder;

    private String folder_name;

    private Button btn_subscribe;
    private TextView tv_folder_subsCount;

    private FolderDTO folderDTO;
    private FolderSubsDTO folderSubsDTO;

    private UserMyFolderDTO userMyfolderDTO = new UserMyFolderDTO();
    private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();

    private FolderPlacesDTO folderPlacesDTO;
    private ArrayList<PlaceDTO> placeDTOS = new ArrayList<PlaceDTO>();
    private ArrayList<String> placeId = new ArrayList<String>();

    public static PlaceListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        setContentView(R.layout.activity_folder_content);

        mContext = this;

        isMyFolder = intent.getBooleanExtra("isMyFolder", false);

        docId = intent.getExtras().get("folder_id").toString();
        folder_name = intent.getExtras().get("folder_name").toString();

        TextView tv_foldername = findViewById(R.id.tv_folder_content_foldername);
        tv_foldername.setText(folder_name);

        tv_folder_subsCount = findViewById(R.id.tv_folder_content_subs_count);
        tv_folder_subsCount.setText(intent.getExtras().get("folder_subs_count").toString());

        TextView tv_folderPublic = findViewById(R.id.tv_folder_content_pub_info);
        tv_folderPublic.setText(intent.getExtras().get("folder_public").toString());

        TextView tv_folderCreator = findViewById(R.id.tv_folder_content_owner_info);
        tv_folderCreator.setText(intent.getExtras().get("folder_owner").toString());

        RecyclerView recyclerView = findViewById(R.id.listView_folder_content_place);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceListAdapter();
        adapter.setFolderId(docId);
        recyclerView.setAdapter(adapter);

        btn_subscribe = findViewById(R.id.btn_folder_content_subscribe);

        if (isMyFolder) { // 내 폴더일 경우
            btn_subscribe.setBackground(getDrawable(R.drawable.background_btn_round_gray));
            btn_subscribe.setText("구독하기");
            btn_subscribe.setTextColor(getColor(R.color.colorLineGray7));
            btn_subscribe.setEnabled(false);
            btn_subscribe.setVisibility(View.VISIBLE);

            adapter.setMyFolder(true);
            adapter.notifyDataSetChanged();
        } else { // 내 폴더가 아닐 경우
            setFolderData();
            setUserData();

            adapter.setMyFolder(false);
            adapter.notifyDataSetChanged();
        }
        btn_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeFolder();
            }
        });

        setPlaceData();

    }

    public void setAdapterItem(int position, PlaceDTO placeDTO) {
        adapter.setTag(position, placeDTO);
        adapter.notifyDataSetChanged();
    }

    public void setFolderData() {

        // 구독자 count 갱신을 위한 folderDTO
        firestore.collection("folders").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        folderDTO = document.toObject(FolderDTO.class);
                        adapter.setPlaceCount(folderDTO.getPlaceCount());
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

        // folderSubscribers 현재 폴더의 구독자 리스트 가져오기
        firestore.collection("folderSubscribers").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        folderSubsDTO = document.toObject(FolderSubsDTO.class);

                        // 만약 구독자 리스트에 존재한다면
                        if (folderSubsDTO.getSubscribers().containsKey(auth.getCurrentUser().getUid())) {
                            btn_subscribe.setBackground(getDrawable(R.drawable.background_btn_round_blue_border));
                            btn_subscribe.setText("구독취소");
                            btn_subscribe.setTextColor(getColor(R.color.colorTheme));
                            btn_subscribe.setVisibility(View.VISIBLE); //버튼 보이기
                        } else {
                            btn_subscribe.setVisibility(View.VISIBLE); //버튼 보이기
                        }
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

    }

    public void setUserData() {

        // usersMyFolder의 현재 로그인한 유저가 소유한 폴더 도큐먼트 이름 가져오기
//        firestore.collection("usersMyFolder").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        userMyfolderDTO = document.toObject(UserMyFolderDTO.class);
//
//                        // 폴더가 내 폴더일 경우
//                        if (userMyfolderDTO.getMyfolders().containsKey(docId)) {
//                            btn_subscribe.setBackground(getDrawable(R.drawable.background_btn_round_gray));
//                            btn_subscribe.setText("구독하기");
//                            btn_subscribe.setTextColor(getColor(R.color.colorLineGray7));
//                            btn_subscribe.setEnabled(false);
//                            btn_subscribe.setVisibility(View.VISIBLE); //버튼 보이기
//
//                            adapter.setMyFolder(true);
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            adapter.setMyFolder(false);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    System.out.println("Error getting documents: " + task.getException());
//                }
//            }
//        });

        // usersSubsFolder의 현재 로그인한 유저가 구독한 폴더 도큐먼트 이름 가져오기
        firestore.collection("usersSubsFolder").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userSubsFolderDTO = document.toObject(UserSubsFolderDTO.class);
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });



    }

    public void setPlaceData() {

        // 폴더에 저장된 장소 id 가져오기
        firestore.collection("folderPlaces").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        folderPlacesDTO = document.toObject(FolderPlacesDTO.class);

                        // 장소 데이터 가져오기
                        for (String key : folderPlacesDTO.getPlaces().keySet()) {
                            firestore.collection("places").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            PlaceDTO placeDTO = document.toObject(PlaceDTO.class);
                                            placeDTOS.add(placeDTO);
                                            placeId.add(document.getId());
                                        }
                                        adapter.setItem(placeDTOS, placeId);
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

    // 폴더 구독 메서드
    public void subscribeFolder() {

        if (btn_subscribe.getText().equals("구독하기")) {

            // 구독자 리스트에 추가
            folderSubsDTO.getSubscribers().put(auth.getCurrentUser().getUid(), true);
            firestore.collection("folderSubscribers").document(docId).set(folderSubsDTO);

            // 내 구독 폴더 리스트에 추가
            userSubsFolderDTO.getSubscribefolders().put(docId, true);
            firestore.collection("usersSubsFolder").document(auth.getCurrentUser().getUid()).set(userSubsFolderDTO);

            // 구독자 카운트 + 1
            folderDTO.setSubscribeCount(folderDTO.getSubscribeCount() + 1);
            firestore.collection("folders").document(docId).set(folderDTO);

            btn_subscribe.setBackground(getDrawable(R.drawable.background_btn_round_blue_border));
            btn_subscribe.setText("구독취소");
            btn_subscribe.setTextColor(getColor(R.color.colorTheme));

            tv_folder_subsCount.setText(String.valueOf(folderDTO.getSubscribeCount()));

            firestore.collection("folderOwner").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String ownerId = document.get("owner").toString();
                        firestore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String nickname = document.get("nickName").toString();
                                    String notice = nickname + " 님이 회원님의 '" + folder_name + "' 폴더를 구독했습니다.";
                                    firestore.collection("notices").document(ownerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                NoticeDTO noticeDTO = document.toObject(NoticeDTO.class);
                                                noticeDTO.getNotices().put(notice, true);
                                                firestore.collection("notices").document(ownerId).set(noticeDTO);
                                            }
                                            else {
                                                NoticeDTO noticeDTO = new NoticeDTO();
                                                noticeDTO.getNotices().put(notice, true);
                                                firestore.collection("notices").document(ownerId).set(noticeDTO);
                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }
                }
            });

        } else if (btn_subscribe.getText().equals("구독취소")) {

            // 구독자 리스트에서 삭제
            folderSubsDTO.getSubscribers().remove(auth.getCurrentUser().getUid());
            firestore.collection("folderSubscribers").document(docId).set(folderSubsDTO);

            // 내 구독 폴더 리스트에서 삭제
            userSubsFolderDTO.getSubscribefolders().remove(docId);
            firestore.collection("usersSubsFolder").document(auth.getCurrentUser().getUid()).set(userSubsFolderDTO);

            // 구독자 카운트 - 1
            folderDTO.setSubscribeCount(folderDTO.getSubscribeCount() - 1);
            firestore.collection("folders").document(docId).set(folderDTO);

            btn_subscribe.setBackground(getDrawable(R.drawable.background_btn_round_blue));
            btn_subscribe.setText("구독하기");
            btn_subscribe.setTextColor(getColor(R.color.colorWhite));

            tv_folder_subsCount.setText(String.valueOf(folderDTO.getSubscribeCount()));
        }


        //폴더 구독자 정보 저장
//        Map<String, Object> subscriber  = new HashMap();
//        Map<String, Object> subscribers  = new HashMap();
//        subscribers.put(auth.getUid().toString(), true);
//        subscriber.put("subscribers", subscribers);
//        // 내 구독 폴더 목록 저장
//        Map<String, Boolean> subscribefolders  = new HashMap();
//        Map<String, Object> subscribefolder  = new HashMap();
//
//
//        //DocumentReference subsDoc = firestore.collection("folderSubscribers").document(auth?.currentUser?.uid!!);
//
//        //firestore.collection("folderSubscribers").document(subFolderIds.get(index)).set(subscriber);
//
//        // 내 구독 폴더에 추가
//        DocumentReference doc = firestore.collection("usersSubsFolder").document(auth.getUid());
//
//        //firestore.runTransaction();
//        UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();
////        subscribefolders.put(subFolderIds.get(index), true);
//
///* 자바에서 런트랜잭션을 어캐 쓸지 몰라서 못함
//            if (userSubsFolderDTO.get(doc).toObject(UserSubsFolderDTO.class) == null) { //리스트에 처음 들어갈 경우
//                userSubsFolderDTO.setSubscribefolders(subscribefolders);
//            }
//            else {
//                userSubsFolderDTO = it.get(doc).toObject(UserSubsFolderDTO.class);
//                userSubsFolderDTO.setSubscribefolders(subscribefolders);
//            }
//            it.set(doc, usermyfolderDTO);*/
//        // 구독 폴더 리스트 삽입 SetOptions.merge()쓰면 갱신해주는 옵션이래서 일단 이걸로 썼음
//        subscribefolder.put("subscribefolders", subscribefolders);
//        doc.set(subscribefolder, SetOptions.merge());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.myfolder_content_option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.myfolder_menu_edit_user);

        // show the button when some condition is true
        if (isMyFolder) {
            menuItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.myfolder_menu_edit_user) {
            if (item.getTitle().equals("수정 권한 관리")) {

                firestore.collection("folderEditors").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String edit = document.getString("edit_auth");
                            if (edit.equals("불가능")) {
                                Toast.makeText(FolderContentActivity.this, "폴더 수정이 불가능합니다", Toast.LENGTH_SHORT).show();
                            }
                            else if (edit.equals("전체 유저")) {
                                Toast.makeText(FolderContentActivity.this, "모든 유저가 수정할 수 있습니다", Toast.LENGTH_SHORT).show();
                            }
                            else if (edit.equals("초대한 유저")) {
                                Intent intent = new Intent(FolderContentActivity.this, FolderContentEditorActivity.class);
                                intent.putExtra("user_id", auth.getCurrentUser().getUid());
                                intent.putExtra("folder_id", docId);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
