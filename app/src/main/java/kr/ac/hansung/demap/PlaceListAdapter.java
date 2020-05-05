package kr.ac.hansung.demap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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

import kr.ac.hansung.demap.model.FolderPlacesDTO;
import kr.ac.hansung.demap.model.PlaceDTO;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Context context;

    private ArrayList<PlaceDTO> placeDTOS = new ArrayList<PlaceDTO>();
    private ArrayList<String> placeIds = new ArrayList<String>();

    private String folderId;
    private FolderPlacesDTO folderPlacesDTO;
    private boolean isMyFolder;
    private int placeCount;

    @NonNull
    @Override
    public PlaceListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list, parent, false);
        PlaceListAdapter.MyViewHolder vh = new PlaceListAdapter.MyViewHolder(view);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceListAdapter.MyViewHolder holder, int position) {
        holder.onBind(placeDTOS.get(position), isMyFolder);

        if (isMyFolder) {
            holder.textView_Options_place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.textView_Options_place);
                    popupMenu.inflate(R.menu.myplace_option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.myplace_menu_edit:
                                    //handle menu1 click
                                    return true;
                                case R.id.myplace_menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setMessage("선택한 장소를 삭제하시겠습니까?");

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            String placeId = placeIds.get(position);
                                            firestore.collection("places").document(placeId).delete();
                                            firestore.collection("folders").document(folderId).update("placeCount", placeCount - 1);
                                            firestore.collection("folderPlaces").document(folderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        folderPlacesDTO = document.toObject(FolderPlacesDTO.class);
                                                        folderPlacesDTO.getPlaces().remove(placeId);
                                                        firestore.collection("folderPlaces").document(folderId).set(folderPlacesDTO);
                                                    }
                                                }
                                            });

                                            removeItem(position);

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
        }

    }

    private void removeItem(int position) {
        placeDTOS.remove(position);
        placeIds.remove(position);
    }

    @Override
    public int getItemCount() {
        return placeDTOS.size();
    }

    void setItem(ArrayList<PlaceDTO> placeDTOs, ArrayList<String> placeIds) {
        this.placeDTOS = placeDTOs;
        this.placeIds = placeIds;
        notifyDataSetChanged();
    }

    void setMyFolder(boolean isMyFolder) {
        this.isMyFolder = isMyFolder;
    }

    void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    void setPlaceCount(int placeCount) {
        this.placeCount = placeCount;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textview_place_name;
        public TextView textview_place_address;
        public TextView textview_place_tag1;
        public TextView textview_place_tag2;
        public TextView textview_place_tag3;

        public TextView textView_Options_place;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textview_place_name = itemView.findViewById(R.id.tv_place_name);
            textview_place_address = itemView.findViewById(R.id.tv_place_address);
            textview_place_tag1 = itemView.findViewById(R.id.tv_place_tag1);
            textview_place_tag2 = itemView.findViewById(R.id.tv_place_tag2);
            textview_place_tag3 = itemView.findViewById(R.id.tv_place_tag3);

            textView_Options_place = itemView.findViewById(R.id.textView_Options_place);
        }

        void onBind(PlaceDTO placeDTO, boolean isMyFolder) {
            textview_place_name.setText(placeDTO.getName());
            textview_place_address.setText(placeDTO.getAddress());

            int i = 0;
            for (String key : placeDTO.getTags().keySet()) {
                if (i == 0) textview_place_tag1.setText("#" + key);
                else if (i == 1) textview_place_tag2.setText("#" + key);
                else if (i == 2) textview_place_tag3.setText("#" + key);
                i++;
            }

            if (!isMyFolder) {
                textView_Options_place.setVisibility(View.INVISIBLE);
            }


        }

    }
}
