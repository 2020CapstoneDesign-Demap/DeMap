package kr.ac.hansung.demap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderDTO;
import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.UserMyFolderDTO;

public class MyFolderViewRecyclerAdapter extends RecyclerView.Adapter<MyFolderViewRecyclerAdapter.MyViewHolder> {

    private Context context;

    //    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();
    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private UserMyFolderDTO userMyfolderDTO;

//    public MyFolderViewRecyclerAdapter(Context context) {
//        this.context = context;
//    }

    @Override
    public void onBindViewHolder(@NonNull MyFolderViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.onBind(folderObjs.get(position));

        holder.textview_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.textview_option);
                popupMenu.inflate(R.menu.myfolder_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.myfolder_menu_edit_folder:
                                //handle menu1 click
                                return true;
                            case R.id.myfolder_menu_delete_folder:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("선택한 폴더를 삭제하시겠습니까?").setMessage("폴더 안의 데이터도 함께 삭제됩니다.");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
//                                        Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection("folders").document(folderObjs.get(position).getId()).delete();
                                        firestore.collection("folderEditors").document(folderObjs.get(position).getId()).delete();
                                        firestore.collection("folderOwner").document(folderObjs.get(position).getId()).delete();
                                        firestore.collection("folderPublic").document(folderObjs.get(position).getId()).delete();
                                        firestore.collection("folderSubscribers").document(folderObjs.get(position).getId()).delete();
                                        firestore.collection("folderTags").document(folderObjs.get(position).getId()).delete();
                                        firestore.collection("usersMyFolder").document(folderObjs.get(position).getOwner()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    userMyfolderDTO = document.toObject(UserMyFolderDTO.class);
                                                    userMyfolderDTO.getMyfolders().remove(folderObjs.get(position).getId());
                                                    firestore.collection("usersMyFolder").document(folderObjs.get(position).getOwner()).set(userMyfolderDTO);
                                                }
                                            }
                                        });

                                        // 아래 세줄 넣으면 삭제하고 다시 조회했을때 데이터가 하나씩 덜보이거나 강종
                                        folderObjs.remove(position);
//                                        notifyItemRemoved(position);
//                                        notifyItemRangeChanged(position, folderObjs.size());
                                        setItem(folderObjs);

                                        //구독 리스트에서 삭제하는건 생각해봤는데 유튜브처럼 document가 비어있으면 삭제된 폴더라구 띄워주고 사용자가 직접 삭제하는게 어때..?
//                                        firestore.collection("usersSubsFolder").document(folderObjs.get(position).getId()).delete();
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
//                                        Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FolderContentActivity.class);

                intent.putExtra("folder_id", folderObjs.get(position).getId());
                intent.putExtra("folder_name", folderObjs.get(position).getName());
//                intent.putExtra("folder_name", folderObjs.get(position).getName());
                intent.putExtra("folder_subs_count", folderObjs.get(position).getSubscribeCount());

                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public MyFolderViewRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myfolder_list, parent, false);
        MyFolderViewRecyclerAdapter.MyViewHolder vh = new MyFolderViewRecyclerAdapter.MyViewHolder(view);
        context = parent.getContext();
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

        public TextView textview_folderview_name;
        public TextView textview_folderview_place_count;
        public TextView textview_folderview_subs_count;

        public TextView textview_option;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            textview_folderview_name = itemView.findViewById(R.id.textview_folderview_name);
            textview_folderview_place_count = itemView.findViewById(R.id.tv_folder_place_count);
            textview_folderview_subs_count = itemView.findViewById(R.id.tv_folder_subs_count);

            textview_option = itemView.findViewById(R.id.textView_Options_myfolder);
        }

        void onBind(FolderObj folderObj) {
            textview_folderview_name.setText(folderObj.getName());
            textview_folderview_place_count.setText(String.valueOf(folderObj.getSubscribeCount()));
            textview_folderview_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));
        }

    }
}
