package kr.ac.hansung.demap.ui.foldercontent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.FolderEditorListDTO;
import kr.ac.hansung.demap.model.NoticeDTO;

public class FolderContentEditorActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String docId;
    private String folder_name;

    private String nick;

    private EditText et_editor_nickname;
    private Button btn_add_editor;

    private FolderEditorListDTO folderEditorListDTO;

    private FolderEditorRecyclerAdapter adapter;

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

        setContentView(R.layout.activity_folder_content_edit);

        Intent intent = getIntent();

        docId = intent.getStringExtra("folder_id");
        folder_name = intent.getStringExtra("folder_name");

        et_editor_nickname = findViewById(R.id.et_folder_editor_nickname);
        btn_add_editor = findViewById(R.id.btn_add_folder_editor);

        RecyclerView recyclerView = findViewById(R.id.listView_folder_editor_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FolderEditorRecyclerAdapter();
        adapter.setdocId(docId);
        recyclerView.setAdapter(adapter);

        firestore.collection("folderEditorList").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    folderEditorListDTO = document.toObject(FolderEditorListDTO.class);
                    ArrayList<String> nicknames = new ArrayList<>();
                    for (String nickname: folderEditorListDTO.getEditors().keySet()) {
                        nicknames.add(nickname);
                    }
                    adapter.setItem(nicknames);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btn_add_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = et_editor_nickname.getText().toString();

                if (!nick.equals("")) {
                    firestore.collection("users").whereEqualTo("nickName", nick).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot.isEmpty()) {
                                    Log.d("log", "document 존재하지 않음");
                                    Toast.makeText(FolderContentEditorActivity.this, "존재하지 않는 닉네임입니다", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("log", document.getId() + " => " + document.getData());

                                        firestore.collection("folderEditorList").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    folderEditorListDTO = document.toObject(FolderEditorListDTO.class);
                                                    folderEditorListDTO.getEditors().put(nick, true);
                                                    firestore.collection("folderEditorList").document(docId).set(folderEditorListDTO);

                                                    ArrayList<String> nicknames = new ArrayList<>();
                                                    for (String nickname: folderEditorListDTO.getEditors().keySet()) {
                                                        nicknames.add(nickname);
                                                        addNotice(nickname);
                                                    }
                                                    adapter.setItem(nicknames);
                                                    adapter.notifyDataSetChanged();

                                                } else {
                                                    folderEditorListDTO = new FolderEditorListDTO();
                                                    folderEditorListDTO.getEditors().put(nick, true);
                                                    firestore.collection("folderEditorList").document(docId).set(folderEditorListDTO);

                                                    ArrayList<String> nicknames = new ArrayList<>();
                                                    for (String nickname: folderEditorListDTO.getEditors().keySet()) {
                                                        nicknames.add(nickname);
                                                        addNotice(nickname);
                                                    }
                                                    adapter.setItem(nicknames);
                                                    adapter.notifyDataSetChanged();

                                                }
                                            }
                                        });
                                    }
                                }
                            } else {
                                Log.d("log", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(FolderContentEditorActivity.this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void addNotice(String nickname) {
        String notice = "'" + folder_name + "' 폴더에 초대되었습니다. 구독하러 가시겠습니까?";
        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setNotice(notice);
        noticeDTO.setFolder_id(docId);
        noticeDTO.setNoticeType("폴더초대알림");
        noticeDTO.setTimestamp(System.currentTimeMillis());
        firestore.collection("users").whereEqualTo("nickName", nickname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String noticeId = document.getId();
                        firestore.collection("notices").document(noticeId).collection("notice").document().set(noticeDTO);
                    }
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
