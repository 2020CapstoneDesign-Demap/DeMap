package kr.ac.hansung.demap.ui.myfolderlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class MyfolderFragment extends Fragment {

    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private TextView tv_myfolder_count;

    private RecyclerView recyclerView;
    private MyFolderViewRecyclerAdapter adapter;

    private Button btn_folder_edit;

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

    public void updateAdapterItem(int position, FolderDTO folderDTO, String folderId) {
        adapter.updateName(position, folderDTO, folderId);
        adapter.notifyDataSetChanged();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.myfolder_tab_fragment, container, false); // SubsFolderFragment와 이 부분만 다름

        tv_myfolder_count = view.findViewById(R.id.textview_total_myfolder_count);
        tv_myfolder_count.setText(String.valueOf(folderObjs.size()));

        recyclerView = (RecyclerView) view.findViewById(R.id.listView_folder_view);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        adapter = new MyFolderViewRecyclerAdapter();
        adapter.setItem(folderObjs);
        adapter.setAuthId(authId);
        adapter.setMyFolder(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        btn_folder_edit = view.findViewById(R.id.btn_myfolder_edit);

        btn_folder_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
