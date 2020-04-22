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

public class MyFolderViewRecyclerAdapter extends RecyclerView.Adapter<MyFolderViewRecyclerAdapter.MyViewHolder> {

    private Context context;

    // adapter에 들어갈 folder list
    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();

    @Override
    public void onBindViewHolder(@NonNull MyFolderViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.onBind(folderDTOS.get(position));
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
        // RecyclerView의 총 개수 입니다.
        return folderDTOS.size();
    }

    void setItem(ArrayList<FolderDTO> folderDTO) {
        folderDTOS = folderDTO;
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textview_folderview_name;
        public TextView textview_folderview_info;

        public MyViewHolder(View itemView) {
            super(itemView);
            textview_folderview_name = itemView.findViewById(R.id.textview_folderview_name);
            textview_folderview_info = itemView.findViewById(R.id.textview_folderview_info);
        }

        void onBind(FolderDTO folderDTO) {
            textview_folderview_name.setText(folderDTO.getName());
            textview_folderview_info.setText(folderDTO.getTimestamp().toString());

        }
    }
}
