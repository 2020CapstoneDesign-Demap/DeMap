package kr.ac.hansung.demap.ui.searchfolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.hansung.demap.ui.foldercontent.FolderContentActivity;
import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.getCount_interface;
import kr.ac.hansung.demap.model.FolderObj;

public class MyAdapterForFolderList extends RecyclerView.Adapter<MyAdapterForFolderList.MyViewHolder> {

    private Context context;

    private getCount_interface getCountInterface;


    // adapter에 들어갈 folder list
    //private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();
    private static ArrayList<FolderObj> searchFolderResult = new ArrayList<FolderObj>(); // 폴더명 검색 결과 리스트를 저장 할 FolderObj ArrayList 생성
    private static ArrayList<String> myFolderList = new ArrayList<String>();
    private static String currentUid;

    public MyAdapterForFolderList(getCount_interface getCountInterface) {
        this.getCountInterface = getCountInterface;
        searchFolderResult.clear();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(searchFolderResult.get(0).getName() == " 검색 결과가 존재하지 않습니다. ") {
            holder.nullBind();
        } else {
            holder.reBind();
            holder.onBind(searchFolderResult.get(position));
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FolderContentActivity.class);

                intent.putExtra("folder_id", searchFolderResult.get(position).getId());
                intent.putExtra("folder_owner", currentUid);
                intent.putExtra("folder_name", searchFolderResult.get(position).getName());
                intent.putExtra("isMyFolder", isMyFolder(position));
                intent.putExtra("folder_subs_count", searchFolderResult.get(position).getSubscribeCount());
                intent.putExtra("folder_public", searchFolderResult.get(position).getIspublic());
                intent.putExtra("folder_placeCount", searchFolderResult.get(position).getPlaceCount());

                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 folder_list.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        context = parent.getContext();
        return vh;
    }

    @Override
    public int getItemCount() {
        if(searchFolderResult.size() == 0) {
            FolderObj nullFolder = new FolderObj();
            nullFolder.setName(" 검색 결과가 존재하지 않습니다. ");
            searchFolderResult.add(nullFolder);
        } else {
            // RecyclerView의 총 개수 입니다.
            getCountInterface.getCount(searchFolderResult.size());
        }
        return searchFolderResult.size();
    }


    void addItems(ArrayList<FolderObj> searchFolderParam) {

        // 외부에서 item을 추가시킬 함수입니다.
        searchFolderResult.clear();
        searchFolderResult.addAll(searchFolderParam);
    }

    void setMyFolderList(ArrayList<String> myFolderParam, String myId) {
        // 외부에서 내폴더 리스트를 추가시킬 함수입니다.
        myFolderList.clear();
        myFolderList.addAll(myFolderParam);
        currentUid = myId;
    }

    private boolean isMyFolder(int position) {
        boolean isMyFolder = false;
        FolderObj folderObj1 = searchFolderResult.get(position);
        searchFolderResult.get(position).setOwner("null");

        for(String folderId1 : myFolderList) {
                if(folderObj1.getId().equals(folderId1)==true) {
                    searchFolderResult.get(position).setOwner(currentUid);
                    isMyFolder = true;
                    return isMyFolder;
                }
            }

        return isMyFolder;
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public AppCompatImageView img_folder_icon;

        public TextView textView_folder_name;
        public TextView textView_folder_tag;
        public TextView textView_folder_subs_count;
        public TextView tv_folder_tag;
        public TextView tv_folder_sub;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_folder_icon = itemView.findViewById(R.id.img_search_folder_list_icon);
            textView_folder_name = itemView.findViewById(R.id.textview_folderlist_name);
            textView_folder_tag = itemView.findViewById(R.id.textview_folderlist_tag);
            textView_folder_subs_count = itemView.findViewById(R.id.tv_folderlist_subs_count);
            tv_folder_tag = itemView.findViewById(R.id.tv_folder_tag);
            tv_folder_sub = itemView.findViewById(R.id.tv_folder_sub);
        }

        void nullBind() {
            img_folder_icon.setVisibility(View.INVISIBLE);
            textView_folder_name.setText(" 검색 결과가 존재하지 않습니다. ");
            textView_folder_tag.setVisibility(View.GONE);
            textView_folder_subs_count.setVisibility(View.GONE);
            tv_folder_tag.setVisibility(View.GONE);
            tv_folder_sub.setVisibility(View.GONE);
        }

        void reBind() {
            img_folder_icon.setVisibility(View.VISIBLE);
            textView_folder_name.setVisibility(View.VISIBLE);
            textView_folder_tag.setVisibility(View.VISIBLE);
            textView_folder_subs_count.setVisibility(View.VISIBLE);
            tv_folder_tag.setVisibility(View.VISIBLE);
            tv_folder_sub.setVisibility(View.VISIBLE);
        }

        void onBind(FolderObj folderObj) {
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
            textView_folder_name.setText(folderObj.getName());
            textView_folder_tag.setText(folderObj.getTag());
            textView_folder_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));
        }
    }

}
