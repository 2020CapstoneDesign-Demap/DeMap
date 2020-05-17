package kr.ac.hansung.demap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.StringValue;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderObj;
import kr.ac.hansung.demap.model.NoticeDTO;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.MyViewHolder> {

    private Context context;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private ArrayList<NoticeDTO> notices = new ArrayList<NoticeDTO>();

    private String uid;

    private Intent intent;

    @NonNull
    @Override
    public NoticeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list, parent, false);
        NoticeListAdapter.MyViewHolder vh = new NoticeListAdapter.MyViewHolder(view);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeListAdapter.MyViewHolder holder, int position) {
        holder.onBind(notices.get(position));

        if (!notices.get(position).getNoticeType().equals("구독알림")) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(v.getContext(), FolderContentActivity.class);
                    firestore.collection("folders").document(notices.get(position).getFolder_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            FolderObj folderObj = document.toObject(FolderObj.class);
                            intent.putExtra("folder_id", document.getId());
                            intent.putExtra("folder_name",folderObj.getName());
                            intent.putExtra("folder_subs_count", folderObj.getSubscribeCount());

                            if (notices.get(position).getNoticeType().equals("내폴더장소추가알림")) {
                                intent.putExtra("isMyFolder", true);
                                intent.putExtra("folder_owner", uid);
                                firestore.collection("folderPublic").document(notices.get(position).getFolder_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        intent.putExtra("folder_public", doc.get("public").toString());
                                        context.startActivity(intent);
                                    }
                                });
                            }
                            else {
                                intent.putExtra("isMyFolder", false);
                                firestore.collection("folderOwner").document(notices.get(position).getFolder_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        intent.putExtra("folder_owner", doc.get("owner").toString());
                                        firestore.collection("folderPublic").document(notices.get(position).getFolder_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot doc2 = task.getResult();
                                                intent.putExtra("folder_public", doc2.get("public").toString());
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    });
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return notices.size();
    }

    void setItem(ArrayList<NoticeDTO> notices) {
        this.notices = notices;
        notifyDataSetChanged();
    }

    void setId(String uid) {
        this.uid = uid;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_notice_icon;
        public TextView textview_notice;

        public View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            img_notice_icon = itemView.findViewById(R.id.img_notice_icon);
            textview_notice = itemView.findViewById(R.id.tv_notice);

        }

        void onBind(NoticeDTO notice) {
            if (notice.getNoticeType().equals("구독알림")) {
                img_notice_icon.setImageResource(R.drawable.ic_stars_blue_24dp);
            }
            else if (notice.getNoticeType().equals("내폴더장소추가알림")) {
                img_notice_icon.setImageResource(R.drawable.demap_icon_1);
                img_notice_icon.setMaxHeight(60);
                img_notice_icon.setMaxWidth(60);
            }
            else if (notice.getNoticeType().equals("구독폴더장소추가알림")) {
                img_notice_icon.setImageResource(R.drawable.demap_icon_1);
                img_notice_icon.setMaxHeight(60);
                img_notice_icon.setMaxWidth(60);
            }
            else if (notice.getNoticeType().equals("폴더초대알림")) {
                img_notice_icon.setImageResource(R.drawable.ic_folder_theme_15dp);
            }
            String ntc = notice.getNotice();
            Long tm = notice.getTimestamp();
            Long tm_ct = System.currentTimeMillis() - tm;
            String time;
            tm_ct = tm_ct/1000;
            if (tm_ct < 60) {
                time = tm_ct.toString() + "초";
            }
            else if (tm_ct/60 < 60) {
                tm_ct = tm_ct/60;
                time = tm_ct.toString() + "분";
            }
            else if (tm_ct/60/60 < 24) {
                tm_ct = tm_ct/60/60;
                time = tm_ct.toString() + "시간";
            }
            else if (tm_ct/60/60/24 < 31) {
                tm_ct = tm_ct/60/60/24;
                time = tm_ct.toString() + "일";
            }
            else if (tm_ct/60/60/24/30 < 12) {
                tm_ct = tm_ct/60/60/24/30;
                time = tm_ct.toString() + "달";
            }
            else {
                time = "오래전";
            }

            String strChange = ntc + " " + "<font color=\"#AAAAAA\">" + time + "</font>";

            textview_notice.setText(Html.fromHtml(strChange));
        }

    }
}
