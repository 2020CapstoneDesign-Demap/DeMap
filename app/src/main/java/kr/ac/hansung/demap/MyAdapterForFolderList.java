package kr.ac.hansung.demap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;

public class MyAdapterForFolderList extends RecyclerView.Adapter<MyAdapterForFolderList.MyViewHolder> /*FirestoreRecyclerAdapter<FolderDTO, MyAdapterForFolderList.MyViewHolder>*/ {

    // adapter에 들어갈 folder list
    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();


    //public MyAdapterForFolderList(ArrayList<FolderDTO> folderDTOS) {
    //    super();
    //    this.folderDTOS = folderDTOS;
    //}


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBind(folderDTOS.get(position));
        Log.d("log", "우왕"+folderDTOS.get(position).getName());
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
        return folderDTOS.size();
    }

    void addItem(FolderDTO folderDTO) {
        // 외부에서 item을 추가시킬 함수입니다.
        folderDTOS.add(folderDTO);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_folder_name;
        public TextView textView_folder_tag;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView_folder_name = itemView.findViewById(R.id.textview_folderlist_name);
            textView_folder_tag = itemView.findViewById(R.id.textview_folderlist_tag);
        }

        void onBind(FolderDTO folderDTO) {
            textView_folder_name.setText(folderDTO.getName());
            textView_folder_tag.setText(folderDTO.getTimestamp().toString());

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    //@Override
    //public int getItemCount() {
    //    return 0;
    //}

}

//class MyAdapterForFolderList() : RecyclerView.Adapter<MyAdapterForFolderList.MyViewHolder>() {
//    private ArrayList<FolderDTO> folderDTOs;
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.folder_public_list, parent, false)
//        return MyViewHolder(inflateView)
//    }
//
//    fun getSelectedItem(): Int {
//        return 0
//    }
//
//    override fun getItemCount(): Int {
//        return 0
//    }
//
//    override fun onBindViewHolder(holder: MyAdapterForFolderList.MyViewHolder, position: Int) {
//
//    }
//
//    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun bind(item: String, position: Int, selectedPosition: Int) {
//
//            //클릭리스너
//            itemView.folder_subscribe_btn.setOnClickListener {
//
//            }
//
//        }
//    }
//}