package kr.ac.hansung.demap.ui.foldercontent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import kr.ac.hansung.demap.ChattingActivity;
import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderPlacesDTO;
import kr.ac.hansung.demap.model.FolderSubsDTO;
import kr.ac.hansung.demap.model.NoticeDTO;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.User;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;


public class FolderContentActivity extends AppCompatActivity {

    public static Context mContext;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동

    private String docId;
    private boolean isMyFolder;

    private String folder_name;
    private String ownerNickName = "생성자";

    // 폴더 구독
    private Button btn_subscribe;
    private TextView tv_folder_subsCount;

    // 폴더 채팅
    private TextView tv_folder_chatting;

    private FolderDTO folderDTO;
    private FolderSubsDTO folderSubsDTO;

    private UserSubsFolderDTO userSubsFolderDTO = new UserSubsFolderDTO();

    private FolderPlacesDTO folderPlacesDTO;
    private ArrayList<PlaceDTO> placeDTOS = new ArrayList<PlaceDTO>();
    private ArrayList<String> placeId = new ArrayList<String>();

    public static PlaceListAdapter adapter;

    private TextView tv_folderCreator;
    private String ownerId;

    private String folder_public;
    private String editable;

    private String nickName;


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

        nickName = intent.getStringExtra("nickname");
        editable = intent.getStringExtra("editable");

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
        folder_public = intent.getStringExtra("folder_public");
        tv_folderPublic.setText(folder_public);

        tv_folderCreator = findViewById(R.id.tv_folder_content_owner_info);

        TextView textview_total_folder_count = findViewById(R.id.textview_total_folder_count);
        textview_total_folder_count.setText(String.valueOf(intent.getIntExtra("folder_placeCount", 0)));

        RecyclerView recyclerView = findViewById(R.id.listView_folder_content_place);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceListAdapter();
        adapter.setFolderId(docId);
        adapter.setUid(auth.getCurrentUser().getUid());
        recyclerView.setAdapter(adapter);

        btn_subscribe = findViewById(R.id.btn_folder_content_subscribe);

        tv_folder_chatting = findViewById(R.id.tv_folder_chatting);

        // 폴더 친구초대 설정
        getFolderEditor();

        if (isMyFolder) { // 내 폴더일 경우
            btn_subscribe.setBackground(getDrawable(R.drawable.background_btn_round_blue));
            btn_subscribe.setText("+  친구 초대");
            btn_subscribe.setTextColor(getColor(R.color.colorWhite));
            btn_subscribe.setEnabled(true);
            btn_subscribe.setVisibility(View.VISIBLE);

            ownerId = intent.getExtras().get("folder_owner").toString();
            setNickname(ownerId);

            adapter.setMyFolder(true);
            adapter.notifyDataSetChanged();

        } else { // 내 폴더가 아닐 경우
            setFolderData();
            setUserData();
            setOwner(docId);
            adapter.setMyFolder(false);
            adapter.notifyDataSetChanged();

            btn_subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscribeFolder();
                }
            });
        }


        setPlaceData();

    }

    // 폴더 친구 초대 설정 가져오기
    public void getFolderEditor() {
        firestore.collection("folderEditors").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String edit = document.getString("edit_auth");

                    if (isMyFolder) { // 내 폴더일 경우
                        btn_subscribe.setOnClickListener(new View.OnClickListener() { // 친구초대 버튼 클릭 리스너
                            @Override
                            public void onClick(View v) {
                                if (edit.equals("불가능")) {
                                    Toast.makeText(FolderContentActivity.this, "폴더 수정이 불가능합니다", Toast.LENGTH_SHORT).show();
                                } else if (edit.equals("전체 유저")) {
                                    Toast.makeText(FolderContentActivity.this, "모든 유저가 수정할 수 있습니다", Toast.LENGTH_SHORT).show();
                                } else if (edit.equals("초대한 유저")) {
                                    Intent intent = new Intent(FolderContentActivity.this, FolderContentEditorActivity.class);
                                    intent.putExtra("user_id", auth.getCurrentUser().getUid());
                                    intent.putExtra("folder_id", docId);
                                    intent.putExtra("folder_name", folder_name);
                                    startActivity(intent);
                                }
                            }
                        });

                        if (edit.equals("초대한 유저")) { // 내폴더&친구초대 폴더일 경우 채팅 버튼 활성화
                            tv_folder_chatting.setVisibility(View.VISIBLE);
                            tv_folder_chatting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(FolderContentActivity.this, ChattingActivity.class);
                                    intent.putExtra("user_id", auth.getCurrentUser().getUid());
                                    intent.putExtra("nickname", nickName);
                                    intent.putExtra("folder_id", docId);
                                    intent.putExtra("folder_name", folder_name);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    else { //구독 폴더일 경우
                        if (editable.equals("가능") && edit.equals("초대한 유저")) { // 구독폴더 수정가능&친구초대 폴더일 경우 채팅 버튼 활성화
                            tv_folder_chatting.setVisibility(View.VISIBLE);
                            tv_folder_chatting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(FolderContentActivity.this, ChattingActivity.class);
                                    intent.putExtra("user_id", auth.getCurrentUser().getUid());
                                    intent.putExtra("nickname", nickName);
                                    intent.putExtra("folder_id", docId);
                                    intent.putExtra("folder_name", folder_name);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                }
            }
        });
    }

    public void setAdapterItem(int position, PlaceDTO placeDTO) {
        adapter.setTag(position, placeDTO);
        adapter.notifyDataSetChanged();
    }

    public void updateAdapterItem(int position, int favorite, HashMap<String, Boolean> favorites) {
        adapter.updatePlaceDTO(position, favorite, favorites);
        adapter.notifyDataSetChanged();
    }

    // 구독 폴더일 경우 폴더 생성자 닉네임 가져오기
    public void setOwner(String docId) {

        // 구독자 count 갱신을 위한 folderDTO
        firestore.collection("folderOwner").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ownerId = document.get("owner").toString();
                        setNickname(ownerId);
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

    }

    // 폴더 생성자 닉네임 가져오는 메서드
    public void setNickname(String ownerId) {

        firestore.collection("users").document(ownerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        ownerNickName = user.getNickName();
                        tv_folderCreator.setText(ownerNickName);
                        System.out.println("폴더 생성자 닉네임 : " + ownerNickName);
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });

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

            // 폴더 주인에게 알림
            firestore.collection("folderOwner").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String ownerId = document.get("owner").toString();

                        firestore.collection("userSettings").document(ownerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    boolean noticeSetting = (Boolean) doc.get("myfolderSubs");
                                    if (noticeSetting) { // 폴더 소유자가 알림을 켜뒀다면
                                        firestore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String nickname = document.get("nickName").toString();
                                                    String notice = nickname + " 님이 회원님의 '" + folder_name + "' 폴더를 구독했습니다.";
                                                    NoticeDTO noticeDTO = new NoticeDTO();
                                                    noticeDTO.setNotice(notice);
                                                    noticeDTO.setFolder_id(docId);
                                                    noticeDTO.setNoticeType("구독알림");
                                                    noticeDTO.setTimestamp(System.currentTimeMillis());
                                                    firestore.collection("notices").document(ownerId).collection("notice").document().set(noticeDTO);

                                                }
                                            }
                                        });
                                    }
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.myfolder_content_option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.myfolder_menu_edit_user);

        // show the button when some condition is true
        if (isMyFolder) {
//            menuItem.setVisible(true);
            menuItem.setVisible(false);
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
//                            if (folder_public.equals("공개")) {
                                if (edit.equals("불가능")) {
                                    Toast.makeText(FolderContentActivity.this, "폴더 수정이 불가능합니다", Toast.LENGTH_SHORT).show();
                                } else if (edit.equals("전체 유저")) {
                                    Toast.makeText(FolderContentActivity.this, "모든 유저가 수정할 수 있습니다", Toast.LENGTH_SHORT).show();
                                } else if (edit.equals("초대한 유저")) {
                                    Intent intent = new Intent(FolderContentActivity.this, FolderContentEditorActivity.class);
                                    intent.putExtra("user_id", auth.getCurrentUser().getUid());
                                    intent.putExtra("folder_id", docId);
                                    intent.putExtra("folder_name", folder_name);
                                    startActivity(intent);
                                }
//                            }
//                            else {
//                                Toast.makeText(FolderContentActivity.this, "비공개 폴더입니다", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
