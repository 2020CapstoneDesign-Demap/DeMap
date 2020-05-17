package kr.ac.hansung.demap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class SubsfolderFragment extends Fragment {

    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private TextView tv_subsfolder_count;

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

        tv_subsfolder_count = view.findViewById(R.id.textview_total_subsfolder_count);
        tv_subsfolder_count.setText(String.valueOf(folderObjs.size()));

        recyclerView = (RecyclerView) view.findViewById(R.id.listView_folder_view);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        adapter = new MyFolderViewRecyclerAdapter();
        adapter.setItem(folderObjs);
        adapter.setAuthId(authId);
        adapter.setMyFolder(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
