package kr.ac.hansung.demap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderEditorListDTO;

public class FolderEditorRecyclerAdapter extends RecyclerView.Adapter<FolderEditorRecyclerAdapter.MyViewHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private ArrayList<String> nicknames = new ArrayList<String>();

    private String docId;

    @NonNull
    @Override
    public FolderEditorRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_editor_list, parent, false);
        FolderEditorRecyclerAdapter.MyViewHolder vh = new FolderEditorRecyclerAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderEditorRecyclerAdapter.MyViewHolder holder, int position) {
        holder.onBind(nicknames.get(position));

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("log", "수정 권한이 있는 유저 삭제");
                firestore.collection("folderEditorList").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FolderEditorListDTO folderEditorListDTO = document.toObject(FolderEditorListDTO.class);
                            folderEditorListDTO.getEditors().remove(nicknames.get(position));
                            firestore.collection("folderEditorList").document(docId).set(folderEditorListDTO);

                            nicknames.remove(position);
                            setItem(nicknames);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return nicknames.size();
    }

    void setItem(ArrayList<String> nicknames) {
        this.nicknames = nicknames;
        notifyDataSetChanged();
    }

    void setdocId(String docId) {
        this.docId = docId;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public TextView textview_nickname;

        public ImageView img_delete;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            textview_nickname = itemView.findViewById(R.id.tv_folder_editor_nickname);

            img_delete = itemView.findViewById(R.id.img_delete_editor);
        }

        void onBind(String nickname) {
            textview_nickname.setText(nickname);
        }

    }
}
