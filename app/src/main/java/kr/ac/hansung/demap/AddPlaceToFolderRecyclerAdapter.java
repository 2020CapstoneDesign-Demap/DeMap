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
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.ui.createfolder.List_onClick_interface;

public class AddPlaceToFolderRecyclerAdapter extends RecyclerView.Adapter<AddPlaceToFolderRecyclerAdapter.MyViewHolder> {

    private Map<String, Boolean> mCheckedFolders = new HashMap<>();

    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private int mSelectedPosition = -1; //선택된 아이템 위치(position)

    private FolderList_onClick_interface listOnClickInterface;

    public AddPlaceToFolderRecyclerAdapter(FolderList_onClick_interface listOnClickInterface) {
        this.listOnClickInterface = listOnClickInterface;
    }

    @Override
    public void onBindViewHolder(@NonNull AddPlaceToFolderRecyclerAdapter.MyViewHolder holder, int position) {

        holder.onBind(folderObjs.get(position), mSelectedPosition, position);

        /* 지우지 말 것
        // 폴더들의 체크 상태
        boolean isChecked = mCheckedFolders.get(folderObjs.get(mSelectedPosition).getId()) == null
                ? false
                : mCheckedFolders.get(folderObjs.get(mSelectedPosition).getId());

        holder.checkBox.setChecked(isChecked);

        listOnClickInterface.onCheckbox(mCheckedFolders); //체크한 폴더ID 넘겨주기
*/



        if ((mSelectedPosition == -1 && position == 0)) { //화면 생성시 첫번째 아이템은 체크상태로
            mSelectedPosition = 0;
            holder.checkBox.setChecked(true);
            listOnClickInterface.onCheckbox(folderObjs.get(mSelectedPosition).getId(), folderObjs.get(mSelectedPosition).getOwner(), folderObjs.get(mSelectedPosition).getName()); //체크한 폴더ID 넘겨주기
            holder.onBind(folderObjs.get(position), mSelectedPosition, position);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedPosition = position;
                listOnClickInterface.onCheckbox(folderObjs.get(position).getId(), folderObjs.get(position).getOwner(), folderObjs.get(position).getName()); //체크한 폴더ID 넘겨주기
                holder.onBind(folderObjs.get(position), mSelectedPosition, position);
                notifyDataSetChanged();
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedPosition = position;
                listOnClickInterface.onCheckbox(folderObjs.get(position).getId(), folderObjs.get(position).getOwner(), folderObjs.get(position).getName()); //체크한 폴더ID 넘겨주기
                holder.onBind(folderObjs.get(position), mSelectedPosition, position);
                notifyDataSetChanged();
            }
        });



    }

    public Map<FolderObj, Boolean> getCheckdFolders(Map<FolderObj, Boolean> mCheckedFolders) {
        return mCheckedFolders;
    }

    
    @NonNull
    @Override
    public AddPlaceToFolderRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfolder_check_list, parent, false);
        final AddPlaceToFolderRecyclerAdapter.MyViewHolder vh = new AddPlaceToFolderRecyclerAdapter.MyViewHolder(view);

        /* 지우지 말 것
        // 리사이클러뷰 체크박스 다중 선택 시 선택한 폴더들의 정보 전달해주기 위한 코드
        vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // 체크가 되면 해당 뷰 홀더의 포지션을 가지고 오고, 그 포지션에 해당하는 폴더객체를 가지고 옴
                FolderObj folderObj = folderObjs.get(vh.getAdapterPosition());
                String folderId = folderObj.getId();
                mCheckedFolders.put(folderId, isChecked);
            }
        });*/

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

    public void updateNewFolder(@Nullable FolderDTO folderDTO, @Nullable String folderId) {
        FolderObj fObj = new FolderObj();
        fObj.setId(folderId);
        fObj.setName(folderDTO.getName());
        fObj.setPlaceCount(folderDTO.getPlaceCount());
        fObj.setSubscribeCount(folderDTO.getSubscribeCount());
        fObj.setImageUrl(folderDTO.getImageUrl());
        folderObjs.add(fObj);
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
            textview_folder_place_count.setText(String.valueOf(folderObj.getPlaceCount()));
            textview_folder_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));

            if (selectedPosition == position)
                checkBox.setChecked(true);
            else
                checkBox.setChecked(false);
        }

    }
}
