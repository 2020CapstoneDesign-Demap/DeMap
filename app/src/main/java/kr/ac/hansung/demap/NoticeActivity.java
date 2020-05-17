package kr.ac.hansung.demap;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.NoticeDTO;

public class NoticeActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private NoticeListAdapter adapter;

    private ArrayList<NoticeDTO> noticeDTOS = new ArrayList<NoticeDTO>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("알림");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_notice);

        RecyclerView recyclerView = findViewById(R.id.listView_notice_list);
        recyclerView.setHasFixedSize(false);
        DividerItemDecoration decoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeListAdapter();
        recyclerView.setAdapter(adapter);

        firestore.collection("notices").document(auth.getCurrentUser().getUid()).collection("notice").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        NoticeDTO noticeDTO = document.toObject(NoticeDTO.class);
                        noticeDTOS.add(noticeDTO);
                    }
                    adapter.setItem(noticeDTOS);
                    adapter.setId(auth.getCurrentUser().getUid());
                    adapter.notifyDataSetChanged();
                }
            }
        });

//        firestore.collection("notices").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    NoticeDTO noticeDTO = document.toObject(NoticeDTO.class);
//
//                    ArrayList<String> notices = new ArrayList<>();
//                    for (String key: noticeDTO.getNotices().keySet()) {
//                        notices.add(key);
//                    }
//                    adapter.setItem(notices);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
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
