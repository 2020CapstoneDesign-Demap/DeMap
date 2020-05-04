package kr.ac.hansung.demap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.ui.createfolder.List_onClick_interface;

public class AddPlaceToFolderRecyclerAdapter extends RecyclerView.Adapter<AddPlaceToFolderRecyclerAdapter.MyViewHolder> {

    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private int mSelectedPosition = -1; //선택된 아이템 위치(position)


    @Override
    public void onBindViewHolder(@NonNull AddPlaceToFolderRecyclerAdapter.MyViewHolder holder, int position) {
        holder.onBind(folderObjs.get(position), mSelectedPosition, position);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedPosition = position;
                notifyDataSetChanged();
            }
        });

    }

    
    @NonNull
    @Override
    public AddPlaceToFolderRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfolder_check_list, parent, false);
        AddPlaceToFolderRecyclerAdapter.MyViewHolder vh = new AddPlaceToFolderRecyclerAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return folderObjs.size();
    }

    void setItem(ArrayList<FolderObj> folderObj) {
        folderObjs = folderObj;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public TextView textview_folder_name;
        public TextView textview_folder_place_count;
        public TextView textview_folder_subs_count;

        public MaterialCheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            textview_folder_name = itemView.findViewById(R.id.tv_checkfolder_name);
            textview_folder_place_count = itemView.findViewById(R.id.tv_checkfolder_place_count);
            textview_folder_subs_count = itemView.findViewById(R.id.tv_checkfolder_subs_count);

            checkBox = itemView.findViewById(R.id.checkbox_checkfolder);
        }

        void onBind(FolderObj folderObj, int selectedPosition, int position) {
            textview_folder_name.setText(folderObj.getName());
            textview_folder_place_count.setText(String.valueOf(folderObj.getSubscribeCount()));
            textview_folder_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));

            if (selectedPosition == position) {
                checkBox.setChecked(true);
            }
            else {
                checkBox.setChecked(false);
            }
        }

    }
}
