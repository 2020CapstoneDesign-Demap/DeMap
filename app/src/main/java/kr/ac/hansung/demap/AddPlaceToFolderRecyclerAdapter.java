package kr.ac.hansung.demap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;

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

        public AppCompatImageView img_folder_icon;

        public TextView textview_folder_name;
        public TextView textview_folder_place_count;
        public TextView textview_folder_subs_count;

        public MaterialCheckBox checkBox;

        public ImageView img_folder_mini_icon;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            img_folder_icon = itemView.findViewById(R.id.img_myfolder_checklist_icon);

            textview_folder_name = itemView.findViewById(R.id.tv_checkfolder_name);
            textview_folder_place_count = itemView.findViewById(R.id.tv_checkfolder_place_count);
            textview_folder_subs_count = itemView.findViewById(R.id.tv_checkfolder_subs_count);

            checkBox = itemView.findViewById(R.id.checkbox_checkfolder);

            img_folder_mini_icon = itemView.findViewById(R.id.img_checkfolder_icon);
        }

        void onBind(FolderObj folderObj, int selectedPosition, int position) {
            switch (folderObj.getImageUrl()) {
                case "blue":
                    img_folder_icon.setImageResource(R.drawable.ic_folder_blue_24dp);
                    break;
                case "violet":
                    img_folder_icon.setImageResource(R.drawable.ic_folder_violet_24dp);
                    break;
                case "peach":
                    img_folder_icon.setImageResource(R.drawable.ic_folder_peach_24dp);
                    break;
                case "pink":
                    img_folder_icon.setImageResource(R.drawable.ic_folder_pink_24dp);
                    break;
                case "green":
                    img_folder_icon.setImageResource(R.drawable.ic_folder_green_24dp);
                    break;
                default:
                    img_folder_icon.setImageResource(R.drawable.ic_folder_blue_24dp);
                    break;
            }

            textview_folder_name.setText(folderObj.getName());
            textview_folder_place_count.setText(String.valueOf(folderObj.getPlaceCount()));
            textview_folder_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));

            if (selectedPosition == position)
                checkBox.setChecked(true);
            else
                checkBox.setChecked(false);

            if (folderObj.getOwner().equals("notMine")) {
                img_folder_mini_icon.setVisibility(View.INVISIBLE);
                if (folderObj.getEditable().equals("가능")) {
                    img_folder_mini_icon.setImageResource(R.drawable.ic_edit_gray_24dp);
                    img_folder_mini_icon.setVisibility(View.VISIBLE);
                }
            }
            else {
                img_folder_mini_icon.setVisibility(View.INVISIBLE);
                if (folderObj.getIspublic().equals("비공개")) {
                    img_folder_mini_icon.setImageResource(R.drawable.ic_lock_gray_24dp);
                    img_folder_mini_icon.setVisibility(View.VISIBLE);
                }
            }
        }

    }
}
