package kr.ac.hansung.demap;

import android.app.Activity;
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
import kr.ac.hansung.demap.model.FolderPlacesDTO;
import kr.ac.hansung.demap.model.FolderSubsDTO;
import kr.ac.hansung.demap.model.PlaceDTO;
import kr.ac.hansung.demap.model.UserMyFolderDTO;
import kr.ac.hansung.demap.model.UserSubsFolderDTO;

public class MyFolderViewRecyclerAdapter extends RecyclerView.Adapter<MyFolderViewRecyclerAdapter.MyViewHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static Context context;

    //    private ArrayList<FolderDTO> folderDTOS = new ArrayList<>();
    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

    private UserMyFolderDTO userMyfolderDTO;

    private boolean isMyFolder;

    private String authId;

//    public MyFolderViewRecyclerAdapter(Context context) {
//        this.context = context;
//    }

    @Override
    public void onBindViewHolder(@NonNull MyFolderViewRecyclerAdapter.MyViewHolder holder, int position) {
        holder.onBind(folderObjs.get(position));

        if (isMyFolder) {
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
                                    Intent intent = new Intent(v.getContext(), CreateFolderActivity.class);
                                    intent.putExtra("folder_name", folderObjs.get(position).getName());
                                    intent.putExtra("folder_public", folderObjs.get(position).getIspublic());
                                    intent.putExtra("folder_tag", folderObjs.get(position).getTag());
                                    intent.putExtra("folder_img", folderObjs.get(position).getImageUrl());
                                    intent.putExtra("folder_id", folderObjs.get(position).getId());
                                    intent.putExtra("folder_edit_flag", "folder_edit");
                                    intent.putExtra("edit_position",position);
                                    v.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    return true;
                                case R.id.myfolder_menu_delete_folder:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setTitle("선택한 폴더를 삭제하시겠습니까?").setMessage("폴더 안의 데이터도 함께 삭제됩니다.");

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
//                                        Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
//                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                            String folderId = folderObjs.get(position).getId();
                                            String folderOwner = folderObjs.get(position).getOwner();
                                            firestore.collection("folders").document(folderId).delete();
                                            firestore.collection("folderEditors").document(folderId).delete();
                                            firestore.collection("folderOwner").document(folderId).delete();
                                            firestore.collection("folderPublic").document(folderId).delete();
                                            firestore.collection("folderSubscribers").document(folderId).delete();
                                            firestore.collection("folderTags").document(folderId).delete();
                                            firestore.collection("folderPlaces").document(folderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        FolderPlacesDTO folderPlacesDTO = document.toObject(FolderPlacesDTO.class);
                                                        for (String key: folderPlacesDTO.getPlaces().keySet()) {
                                                            firestore.collection("places").document(key).delete();
                                                        }

                                                        firestore.collection("folderPlaces").document(folderId).delete();
                                                    }
                                                }
                                            });

                                            firestore.collection("usersMyFolder").document(folderOwner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        userMyfolderDTO = document.toObject(UserMyFolderDTO.class);
                                                        userMyfolderDTO.getMyfolders().remove(folderId);
                                                        firestore.collection("usersMyFolder").document(folderOwner).set(userMyfolderDTO);
                                                    }
                                                }
                                            });

                                            removeItem(position);

                                            //구독 리스트에서 삭제하는건 생각해봤는데 유튜브처럼 document가 비어있으면 삭제된 폴더라구 띄워주고 사용자가 직접 삭제하는게 어때..?
//                                        firestore.collection("usersSubsFolder").document(folderObjs.get(position).getId()).delete();
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        }
        else {
            holder.textview_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.textview_option);
                    popupMenu.inflate(R.menu.subsfolder_option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.subsfolder_menu_delete_folder:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setMessage("선택한 폴더의 구독을 취소하시겠습니까?");

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            String folderId = folderObjs.get(position).getId();
                                            String folderOwner = folderObjs.get(position).getOwner();

                                            // 현재 폴더의 구독자 리스트에서 삭제
                                            firestore.collection("folderSubscribers").document(folderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            FolderSubsDTO folderSubsDTO = document.toObject(FolderSubsDTO.class);
                                                            folderSubsDTO.getSubscribers().remove(authId);
                                                            firestore.collection("folderSubscribers").document(folderId).set(folderSubsDTO);
                                                        }
                                                    } else {
                                                        System.out.println("Error getting documents: " + task.getException());
                                                    }
                                                }
                                            });


                                            // 내 구독 폴더 리스트에서 삭제
                                            firestore.collection("usersSubsFolder").document(authId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            UserSubsFolderDTO userSubsFolderDTO = document.toObject(UserSubsFolderDTO.class);
                                                            userSubsFolderDTO.getSubscribefolders().remove(folderId);
                                                            firestore.collection("usersSubsFolder").document(authId).set(userSubsFolderDTO);
                                                        }
                                                    } else {
                                                        System.out.println("Error getting documents: " + task.getException());
                                                    }
                                                }
                                            });


                                            // 구독자 카운트 - 1
                                            firestore.collection("folders").document(folderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            FolderDTO folderDTO = document.toObject(FolderDTO.class);
                                                            folderDTO.setSubscribeCount(folderDTO.getSubscribeCount() - 1);
                                                            firestore.collection("folders").document(folderId).set(folderDTO);
                                                        }
                                                    } else {
                                                        System.out.println("Error getting documents: " + task.getException());
                                                    }
                                                }
                                            });

                                            removeItem(position);

                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FolderContentActivity.class);

                intent.putExtra("folder_id", folderObjs.get(position).getId());
                intent.putExtra("folder_owner", folderObjs.get(position).getOwner());
                intent.putExtra("folder_name", folderObjs.get(position).getName());
//                intent.putExtra("folder_name", folderObjs.get(position).getName());
                intent.putExtra("folder_subs_count", folderObjs.get(position).getSubscribeCount());
                intent.putExtra("folder_public", folderObjs.get(position).getIspublic());

                intent.putExtra("isMyFolder", isMyFolder);

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

    void removeItem(int position) {
        folderObjs.remove(position);
        setItem(folderObjs);
    }

    void updateName(int position, FolderDTO folderDTO, String folderId) {
        FolderObj fObj = new FolderObj();
        fObj.setId(folderId);
        fObj.setName(folderDTO.getName());
        fObj.setPlaceCount(folderDTO.getPlaceCount());
        fObj.setSubscribeCount(folderDTO.getSubscribeCount());
        fObj.setImageUrl(folderDTO.getImageUrl());
        folderObjs.set(position, fObj);
        notifyDataSetChanged();
    }

    void setItem(ArrayList<FolderObj> folderObj) {
        folderObjs = folderObj;
        notifyDataSetChanged();
    }

    void setMyFolder(boolean isMyFolder) {
        this.isMyFolder = isMyFolder;
    }

    void setAuthId(String authId) {
        this.authId = authId;
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
            textview_folderview_place_count.setText(String.valueOf(folderObj.getPlaceCount()));
            textview_folderview_subs_count.setText(String.valueOf(folderObj.getSubscribeCount()));
        }

    }
}
