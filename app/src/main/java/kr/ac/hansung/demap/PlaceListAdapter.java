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
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                                    Intent intent = new Intent(v.getContext(), AddPlaceFormActivity.class);

                                    PlaceDTO placeDTO = placeDTOS.get(position);
                                    String placeId = placeIds.get(position);
                                    /*
                                    Map<String, Boolean> tags = new HashMap<>();
                                    ArrayList<String> taglist = new ArrayList<>();
                                    tags.putAll(placeDTO.getTags());
                                    // 하드 코딩 죄송합니다
                                    // 선택된 태그 장소 저장 폼으로 인텐트에 담아 전송
                                    if(tags.get("노키즈존") == true) {
                                        taglist.add("노키즈존");
                                    }
                                    if(tags.get("웰컴키즈존") == true) {
                                        taglist.add("웰컴키즈존");
                                    }
                                    if(tags.get("남녀화장실 분리") == true) {
                                        taglist.add("남녀화장실 분리");
                                    }
                                    if(tags.get("공용 화장실") == true) {
                                        taglist.add("공용 화장실");
                                    }
                                    if(tags.get("계단 있음") == true) {
                                        taglist.add("계단 있음");
                                    }
                                    if(tags.get("계단 없음") == true) {
                                        taglist.add("계단 없음");
                                    }
                                    if(tags.get("콘센트 많음") == true) {
                                        taglist.add("콘센트 많음");
                                    }
                                    if(tags.get("콘센트 적음") == true) {
                                        taglist.add("콘센트 적음");
                                    }
                                    if(tags.get("공부하기 좋은") == true) {
                                        taglist.add("공부하기 좋은");
                                    }
                                    if(tags.get("데이트하기 좋은") == true) {
                                        taglist.add("데이트하기 좋은");
                                    }
                                    if(tags.get("가족모임하기 좋은") == true) {
                                        taglist.add("가족모임하기 좋은");
                                    }
                                    if(tags.get("회식하기 좋은") == true) {
                                        taglist.add("회식하기 좋은");
                                    }
                                    if(tags.get("사진 찍기 좋은") == true) {
                                        taglist.add("사진 찍기 좋은");
                                    }
                                    if(tags.get("편안히 쉬기 좋은") == true) {
                                        taglist.add("편안히 쉬기 좋은");
                                    }

                                     */
                                    intent.putExtra("result_name", placeDTO.getName());
                                    intent.putExtra("edit_id",placeId);
                                    //intent.putStringArrayListExtra("edit_tags", taglist);
                                    intent.putExtra("flag", "edit");
                                    v.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceDTO tmpPlace = placeDTOS.get(position);

                Intent intent1 = new Intent(v.getContext(), NaverSearchContentActivity.class);

                intent1.putExtra("result_name", tmpPlace.getName());
                intent1.putExtra("result_addr", tmpPlace.getAddress());
                intent1.putExtra("result_category", tmpPlace.getCategory());
                intent1.putExtra("result_phone", tmpPlace.getTelephone());
                intent1.putExtra("result_mapx", tmpPlace.getX());
                intent1.putExtra("result_mapy", tmpPlace.getY());

                context.startActivity(intent1);
            }
        });

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

        public TextView textView_Options_place;

        public View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            textview_place_name = itemView.findViewById(R.id.tv_place_name);
            textview_place_address = itemView.findViewById(R.id.tv_place_address);
            textview_place_tag1 = itemView.findViewById(R.id.tv_place_tag1);
            textview_place_tag2 = itemView.findViewById(R.id.tv_place_tag2);

            textView_Options_place = itemView.findViewById(R.id.textView_Options_place);
        }

        void onBind(PlaceDTO placeDTO, boolean isMyFolder) {
            textview_place_name.setText(placeDTO.getName());
            textview_place_address.setText(placeDTO.getAddress());

            int i = 0;
            for (String key : placeDTO.getTags().keySet()) {
                if (i == 0) textview_place_tag1.setText("#" + key);
                else if (i == 1) textview_place_tag2.setText("#" + key);
                i++;
            }

            if (!isMyFolder) {
                textView_Options_place.setVisibility(View.INVISIBLE);
            }


        }

    }
}
