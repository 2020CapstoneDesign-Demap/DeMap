package kr.ac.hansung.demap;

import android.content.Context;
import android.content.Intent;
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

public class MyAdapterForFolderList extends RecyclerView.Adapter<MyAdapterForFolderList.MyViewHolder> {

    private Context context;

    // adapter에 들어갈 folder list
    //private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();
    private static ArrayList<FolderObj> searchFolderResult = new ArrayList<FolderObj>(); // 폴더명 검색 결과 리스트를 저장 할 FolderObj ArrayList 생성

    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    public MyAdapterForFolderList(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBind(searchFolderResult.get(position));

//        Log.d("log", "우왕"+searchFolderResult.get(position).getName());
//        holder.onBind(folderObjs.get(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FolderContentActivity.class);

                intent.putExtra("folder_name", searchFolderResult.get(position).getName());
//                intent.putExtra("folder_name", folderObjs.get(position).getName());
                intent.putExtra("folder_subs_count", folderObjs.get(position).getSubscribeCount());

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
        return vh;
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return searchFolderResult.size();
    }

    void addItems(ArrayList<FolderObj> searchFolderParam) {
        // 외부에서 item을 추가시킬 함수입니다.
        searchFolderResult.clear();
        searchFolderResult.addAll(searchFolderParam);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public TextView textView_folder_name;
        public TextView textView_folder_tag;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView_folder_name = itemView.findViewById(R.id.textview_folderlist_name);
            textView_folder_tag = itemView.findViewById(R.id.textview_folderlist_tag);
        }

        void onBind(FolderObj folderObj) {
            textView_folder_name.setText(folderObj.getName());
            textView_folder_tag.setText(folderObj.getTag());
            // 화면에 띄운 결과 폴더는 리스트에서 삭제함
            //searchFolderResult.remove(folderObj);
        }
    }

}
