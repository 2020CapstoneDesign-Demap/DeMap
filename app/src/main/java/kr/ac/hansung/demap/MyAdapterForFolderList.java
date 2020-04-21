package kr.ac.hansung.demap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import kr.ac.hansung.demap.model.FolderDTO;

public class MyAdapterForFolderList extends FirestoreRecyclerAdapter<FolderDTO, MyAdapterForFolderList.MyViewHolder> {

    public MyAdapterForFolderList(FirestoreRecyclerOptions<FolderDTO> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, FolderDTO model) {
        holder.textView_folder_name.setText(String.valueOf(model.getName()));
        Log.d("log", model.getName());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_folder_name;
        public TextView textView_folder_tag;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView_folder_name = itemView.findViewById(R.id.textview_folderlist_name);
            textView_folder_tag = itemView.findViewById(R.id.textview_folderlist_tag);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 0;
    }

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
