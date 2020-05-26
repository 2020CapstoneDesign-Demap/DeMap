package kr.ac.hansung.demap.ui.hotPlace;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;
import kr.ac.hansung.demap.model.UserMyHotPlaceDTO;

public class MyHotPlaceRecyclerAdapter extends RecyclerView.Adapter<MyHotPlaceRecyclerAdapter.ViewHolder> {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Context context;
    private HotPlaceDTO selectedHotPlaceDTO;
    private HotPlaceDTO hotPlaceDTO;
    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();
    private ArrayList<String> hotPlaceIds;
    private UserMyHotPlaceDTO userMyHotPlaceDTO = new UserMyHotPlaceDTO();
    private HashMap<String, Boolean> myHotPlace  = new HashMap<>();
    private String authId = auth.getCurrentUser().getUid();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myhotplace_list, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHotPlaceRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(hotPlaceList.get(position));

        holder.editCommentBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("코멘트 수정");
            final EditText input = new EditText(context);
            input.setText(holder.tv_myHotPlaceComment.getText().toString());
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String value = input.getText().toString();
                    readHotPlaceDB();
                    String hotPlaceId = hotPlaceIds.get(position);

                    firestore.collection("hotPlaces").document(hotPlaceId).update("comment",value);
                    hotPlaceList.get(position).setComment(value);
                    notifyDataSetChanged();

                    setHotPlaceList(hotPlaceList, hotPlaceIds);
                    Toast.makeText(context,"수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("NO", (dialog, which) -> {

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        holder.openInstagramBtn.setOnClickListener(v -> {
            String url = hotPlaceList.get(position).getPostUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        });

        holder.rmHotPlaceBtn.setOnClickListener(v -> {
            readHotPlaceDB();
            String hotPlaceId = hotPlaceIds.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("핫플레이스 삭제").setMessage("선택한 장소를 삭제하시겠습니까?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firestore.collection("hotPlaces").document(hotPlaceId).delete();
                    firestore.collection("usersHotPlace").document(authId).get().addOnCompleteListener(task -> {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userMyHotPlaceDTO = document.toObject(UserMyHotPlaceDTO.class);
                            userMyHotPlaceDTO.getMyhotplaces().remove(hotPlaceId);

                            DocumentReference dr = firestore.collection("usersHotPlace").document(authId);
                            dr.set(userMyHotPlaceDTO);

                            hotPlaceList.remove(position);
                            hotPlaceIds.remove(hotPlaceId);
                            setHotPlaceList(hotPlaceList, hotPlaceIds);

                            notifyDataSetChanged();
                        }
                    });
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return hotPlaceList.size();
    }

    public void clearHotPlace() {
        hotPlaceList.clear();
    }

    void setHotPlaceList(ArrayList<HotPlaceDTO> hotPlaceList, ArrayList<String> hotPlaceIds) {
        this.hotPlaceList = hotPlaceList;
        this.hotPlaceIds = hotPlaceIds;
    }

    void setAuthId(String authId) {
        this.authId = authId;
    }

    public void readHotPlaceDB () {
        firestore.collection("usersHotPlace").document(authId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //clearHotPlace();
                    hotPlaceIds = new ArrayList<>();
                    userMyHotPlaceDTO = document.toObject(UserMyHotPlaceDTO.class);

                    for(String key : userMyHotPlaceDTO.getMyhotplaces().keySet()){
                        firestore.collection("hotPlaces").document(key).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                DocumentSnapshot document1 = task1.getResult();
                                if (document1.exists()) {
                                    //hotPlaceDTO = document1.toObject(HotPlaceDTO.class);
                                    //hotPlaceList.add(hotPlaceDTO);
                                    hotPlaceIds.add(document1.getId());

                                    //Collections.sort(hotPlaceList, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

                                    setHotPlaceList(hotPlaceList, hotPlaceIds);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_myHotPlaceComment;
        private TextView tv_myHotPlaceTag;
        private ImageView iv_myHotPlace;
        private ImageButton rmHotPlaceBtn;
        private ImageButton editCommentBtn;
        private ImageButton openInstagramBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_myHotPlaceComment = itemView.findViewById(R.id.tv_hotPlaceComment);
            iv_myHotPlace = itemView.findViewById(R.id.iv_myHotPlace);
            tv_myHotPlaceTag = itemView.findViewById(R.id.tv_hotPlaceTag);

            rmHotPlaceBtn = itemView.findViewById(R.id.btn_remove_hotPlace);
            openInstagramBtn = itemView.findViewById(R.id.btn_open_instagram);
            editCommentBtn = itemView.findViewById(R.id.btn_edit_hpComment);
        }

        void onBind(HotPlaceDTO data) {
            tv_myHotPlaceComment.setText(data.getComment());
            tv_myHotPlaceTag.setText(data.getTag());
            Glide.with(context)
                    .load(data.getImageUrl())
                    .override(200, 200)
                    .into(iv_myHotPlace);
        }
    }
}
