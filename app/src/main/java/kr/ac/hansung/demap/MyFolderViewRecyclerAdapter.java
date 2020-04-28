package kr.ac.hansung.demap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

public class MyFolderViewRecyclerAdapter extends RecyclerView.Adapter<MyFolderViewRecyclerAdapter.MyViewHolder> {

//    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();
    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    @Override
    public void onBindViewHolder(@NonNull MyFolderViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.onBind(folderObjs.get(position));
    }

    @NonNull
    @Override
    public MyFolderViewRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfolder_list, parent, false);
        MyFolderViewRecyclerAdapter.MyViewHolder vh = new MyFolderViewRecyclerAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return folderObjs.size();
    }

    void setItem(ArrayList<FolderObj> folderObj) {
        folderObjs = folderObj;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textview_folderview_name;
        public TextView textview_folderview_place_count;
        public TextView textview_folderview_subs_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            textview_folderview_name = itemView.findViewById(R.id.textview_folderview_name);
            textview_folderview_place_count = itemView.findViewById(R.id.tv_folder_place_count);
            textview_folderview_subs_count = itemView.findViewById(R.id.tv_folder_subs_count);
        }

        void onBind(FolderObj folderObj) {
            textview_folderview_name.setText(folderObj.getName());
            textview_folderview_place_count.setText(String.valueOf(folderObj.getSubscribeCount()));
            textview_folderview_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));
        }
    }
}
