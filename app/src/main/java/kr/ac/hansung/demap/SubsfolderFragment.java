package kr.ac.hansung.demap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class SubsfolderFragment extends Fragment {


    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();
    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private RecyclerView recyclerView;
    private MyFolderViewRecyclerAdapter adapter;

    private String authId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setFolderDTOs(ArrayList<FolderObj> folderObj) {
        folderObjs = folderObj;
    }
    void setAuthId(String authId) {
        this.authId = authId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subsfolder_tab_fragment, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.listView_folder_view);
        recyclerView.setHasFixedSize(true);
        adapter = new MyFolderViewRecyclerAdapter();
        adapter.setItem(folderObjs);
        adapter.setAuthId(authId);
        adapter.setMyFolder(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
