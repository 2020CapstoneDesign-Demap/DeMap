package kr.ac.hansung.demap;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;


public class FolderListActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동
    private CollectionReference folderRef = firestore.collection("folders"); // firestore에서 folder 내역 가져오기

    private MyAdapterForFolderList adapter; // FolderList 어댑터

    private ArrayList<FolderDTO> folderDTOs = new ArrayList<FolderDTO>(); // 폴더 리스트를 저장 할 FolderDTO ArrayList 생성


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("폴더 리스트");
        // ActionBar의 배경색 변경
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_folder_list);

        // folders의 모든 도큐먼트 가져오기
        folderRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println(document.getId() + " => " + document.getData());
                                FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                folderDTOs.add(folderDTO);
                                System.out.println(folderDTO.getName());
                                adapter.addItem(folderDTO);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                            System.out.println("Error getting documents: " + task.getException());

                        }
                    }
                });


        RecyclerView recyclerView = findViewById(R.id.listView_folder_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapterForFolderList();
        recyclerView.setAdapter(adapter);

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