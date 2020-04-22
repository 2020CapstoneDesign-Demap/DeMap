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

public class MyfolderFragment extends Fragment {

    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();

    private RecyclerView recyclerView;
    private MyFolderViewRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setFolderDTOs(ArrayList<FolderDTO> folderDTO) {
        folderDTOS = folderDTO;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.myfolder_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.listView_folder_view);
        recyclerView.setHasFixedSize(true);
        adapter = new MyFolderViewRecyclerAdapter();
        adapter.setItem(folderDTOS);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
