package kr.ac.hansung.demap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class SubableListThread implements Runnable {
    //private FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // firebase 연동
    private ArrayList<FolderObj> folderObjs = null; // 폴더 관련 모든 데이터를 저장 할 FolderObj ArrayList 생성
    private ArrayList<FolderObj> subableFolderObjs = null; // 구독 가능 폴더 id 리스트를 저장 할 FolderDTO ArrayList 생성
    private FirebaseFirestore firestore = null;

    public SubableListThread(FirebaseFirestore firestore, ArrayList<FolderObj> folderObjs, ArrayList<FolderObj> subableFolderObjs ) {
        this.folderObjs = folderObjs;
        this.subableFolderObjs = subableFolderObjs;
        this.firestore=firestore;
    }

    @Override
    public void run() {
        CollectionReference folderPublicRef = firestore.collection("folderPublic"); // firestore에서 folderPublic 내역 가져오기
        // folders에서 구독 가능한 foler 도큐먼트 가져오기
        folderPublicRef.whereEqualTo("public", "공개").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println(document.getId() + " => " + document.getData());

                                // 전체 folder 리스트에서 구독 가능한 폴더 정보만 가져와 어레이리스트에 저장
                                for ( FolderObj fObj : folderObjs ) {
                                    int index = folderObjs.indexOf(fObj);
                                    System.out.println("folderObjs index : " + index);
                                    if (document.getId().equals(fObj.getId())) {
                                        //FolderDTO publicfolderDTO = documentSub.toObject(FolderDTO.class);
                                        // 폴더 공개 여부를 "공개"로 설정 후 배열리스트에 삽입
                                        fObj.setIspublic("공개");

                                        folderObjs.set(folderObjs.indexOf(fObj), fObj);
                                        // 공개된 폴더를 구독가능폴더리스트에 저장
                                        subableFolderObjs.add(fObj);
                                    }
                                }

                                for (FolderObj fObj : folderObjs) {
                                    System.out.println("구독 가능 여부 : " + fObj.getId() + fObj.getName() + fObj.getIspublic());

                                }

                                //adapter.addItem(publicfolderDTO);
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
