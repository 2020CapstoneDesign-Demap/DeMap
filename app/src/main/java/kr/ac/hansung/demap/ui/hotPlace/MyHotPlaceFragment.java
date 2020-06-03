package kr.ac.hansung.demap.ui.hotPlace;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;
import kr.ac.hansung.demap.model.UserMyHotPlaceDTO;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MyHotPlaceFragment extends Fragment {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private HotPlaceDTO hotPlaceDTO = new HotPlaceDTO();
    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();
    private ArrayList<String> hotPlaceIds;
    private UserMyHotPlaceDTO userMyHotPlaceDTO = new UserMyHotPlaceDTO();

    private RecyclerView recyclerView;
    private MyHotPlaceRecyclerAdapter adapter;
    private String authId;
    private TextView totalHotPlace;

    void setHotPlaceDTO(ArrayList<HotPlaceDTO> hotPlaceDTOs) {
        this.hotPlaceList = hotPlaceDTOs;
    }

    void setAuthId(String authId) {
        this.authId = authId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.my_hotplace_fragment, container, false);

        totalHotPlace = view.findViewById(R.id.tv_myHotPlace_totalCnt);
        recyclerView = view.findViewById(R.id.rv_myHotPlace);
        recyclerView.setHasFixedSize(true);

        adapter = new MyHotPlaceRecyclerAdapter();
        setHotPlaceData();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                int itemCount = getItemCount();
                totalHotPlace.setText(itemCount+"");
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setHotPlaceData() {
        setAuthId(auth.getCurrentUser().getUid());

        final DocumentReference docRef = firestore.collection("usersHotPlace").document(authId);
        docRef.addSnapshotListener((snapshot, e) -> {
            if(e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                adapter.clearHotPlace();
                hotPlaceIds = new ArrayList<>();
                userMyHotPlaceDTO = snapshot.toObject(UserMyHotPlaceDTO.class);
                for (String key: userMyHotPlaceDTO.getMyhotplaces().keySet()) {
                    firestore.collection("hotPlaces").document(key).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                hotPlaceDTO = document.toObject(HotPlaceDTO.class);
                                hotPlaceList.add(hotPlaceDTO);
                                hotPlaceIds.add(document.getId());

                                //Collections.sort(hotPlaceList, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

                                adapter.setHotPlaceList(hotPlaceList, hotPlaceIds);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                    });
                }

            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

}
