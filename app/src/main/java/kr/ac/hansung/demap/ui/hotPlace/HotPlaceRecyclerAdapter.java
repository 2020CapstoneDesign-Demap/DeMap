package kr.ac.hansung.demap.ui.hotPlace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;
import kr.ac.hansung.demap.model.UserMyHotPlaceDTO;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HotPlaceRecyclerAdapter extends RecyclerView.Adapter<HotPlaceRecyclerAdapter.ViewHolder> {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Context context;
    private HotPlaceDTO hotPlaceDTO = new HotPlaceDTO();
    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();
    private UserMyHotPlaceDTO userMyHotPlaceDTO = new UserMyHotPlaceDTO();
    private HashMap<String, Boolean> myHotPlace;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotplace_list, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotPlaceRecyclerAdapter.ViewHolder viewHolder, int position) {
        viewHolder.onBind(hotPlaceList.get(position));

        viewHolder.iv_hotPlace.setOnClickListener(v -> {
            if(viewHolder.ib_addBtn.getVisibility() == View.VISIBLE) {
                viewHolder.iv_hotPlace.clearColorFilter();
                viewHolder.ib_addBtn.setVisibility(View.INVISIBLE);
                viewHolder.ib_openBtn.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.iv_hotPlace.setColorFilter(Color.parseColor("#ABABAB"), PorterDuff.Mode.MULTIPLY);
                viewHolder.ib_addBtn.setVisibility(View.VISIBLE);
                viewHolder.ib_openBtn.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.ib_addBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("핫플레이스 저장");
            builder.setView(R.layout.hotplace_dialog);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog f = (Dialog) dialog;
                    EditText input = f.findViewById(R.id.et_dialog);
                    String value = input.getText().toString();

                    if(value.isEmpty()){
                        value = "내용이 없습니다.";
                    } else {
                        value = input.getText().toString();
                    }

                    saveHotPlace(position, value);
                    notifyDataSetChanged();

                    Toast.makeText(context,"저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("CANCEL", (dialog, which) -> {

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        viewHolder.ib_openBtn.setOnClickListener(v -> {
            String url = hotPlaceList.get(position).getPostUrl();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hotPlaceList.size();
    }

    public void clearHotPlace() {
        hotPlaceList.clear();
    }

    void setHotPlaceList(ArrayList<HotPlaceDTO> hotPlaceList) {
        this.hotPlaceList = hotPlaceList;
        notifyDataSetChanged();
    }

    public void saveHotPlace(int position, String comment) {
        String userUid = auth.getCurrentUser().getUid();

        myHotPlace  = new HashMap<>();
        hotPlaceDTO.setImageUrl(hotPlaceList.get(position).getImageUrl());
        hotPlaceDTO.setPostUrl(hotPlaceList.get(position).getPostUrl());
        hotPlaceDTO.setTag(hotPlaceList.get(position).getTag());
        hotPlaceDTO.setComment(comment);
        hotPlaceDTO.setTimestamp(System.currentTimeMillis());

        firestore.collection("hotPlaces")
                .add(hotPlaceDTO)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String hotPlaceId = documentReference.getId();

                        DocumentReference dr = firestore.collection("usersHotPlace").document(userUid);

                        myHotPlace.put(hotPlaceId, true);
                        userMyHotPlaceDTO.setMyhotplaces(myHotPlace);

                        dr.set(userMyHotPlaceDTO, SetOptions.merge());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_hotPlace;
        private ImageButton ib_addBtn;
        private ImageButton ib_openBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_hotPlace = itemView.findViewById(R.id.iv_hotPlace);
            ib_addBtn = itemView.findViewById(R.id.btn_add_hotPlace);
            ib_openBtn = itemView.findViewById(R.id.btn_open_instagram);
        }

        void onBind(HotPlaceDTO data) {
            Glide.with(context)
                    .load(data.getImageUrl())
                    .into(iv_hotPlace);
        }

    }


}
