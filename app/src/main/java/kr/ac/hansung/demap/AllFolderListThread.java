package kr.ac.hansung.demap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class AllFolderListThread extends AppCompatActivity implements Runnable {
    //private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동
    private ArrayList<FolderObj> folderObjs = null; // 폴더 관련 모든 데이터를 저장 할 FolderObj ArrayList 생성
    //private ArrayList<FolderObj> subableFolderObjs = new ArrayList<FolderObj>(); // 구독 가능 폴더 id 리스트를 저장 할 FolderDTO ArrayList 생성
    private ArrayList<FolderDTO> folderDTOs = null; // 폴더 리스트를 저장 할 FolderDTO ArrayList 생성
    //private CollectionReference folderRef = null; // firestore에서 folder 내역 가져오기
    private FirebaseFirestore firestore = null;


    //private MyAdapterForFolderList adapter; // FolderList 어댑터


    public AllFolderListThread(FirebaseFirestore firestore, ArrayList<FolderDTO> folderDTOs, ArrayList<FolderObj> folderObjs) {
        this.firestore = firestore;
        this.folderDTOs = folderDTOs;
        this.folderObjs = folderObjs;

    }

    @Override
    public void run() {
            //MyAdapterForFolderList adapter;
            CollectionReference folderRef = firestore.collection("folders");
            // folders의 모든 도큐먼트 가져오기
            folderRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    System.out.println(document.getId() + " => " + document.getData());
                                    // 가져온 도큐먼트를 folderDTO객체로 저장
                                    FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                    // 가져온 도큐먼트를 folderObj객체로 저장
                                    FolderObj folderObj = document.toObject(FolderObj.class);
                                    folderObj.setId(document.getId()); // 폴더객체에 폴더도큐먼트id 삽입
                                    folderDTOs.add(folderDTO);
                                    folderObjs.add(folderObj);
                                    System.out.println("폴더디티오 : " + folderDTO.getName());
                                    System.out.println("폴더오비제 : " + folderObj.getName());
                                    //adapter.addItem(folderDTO);
                                }
                                //adapter.notifyDataSetChanged();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                                System.out.println("Error getting documents: " + task.getException());

                            }
                        }
                    });

    }
}
