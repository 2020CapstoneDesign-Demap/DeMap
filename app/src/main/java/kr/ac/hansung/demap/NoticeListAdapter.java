package kr.ac.hansung.demap;

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

import com.google.protobuf.StringValue;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.NoticeDTO;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.MyViewHolder> {

    private ArrayList<NoticeDTO> notices = new ArrayList<NoticeDTO>();

    @NonNull
    @Override
    public NoticeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list, parent, false);
        NoticeListAdapter.MyViewHolder vh = new NoticeListAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeListAdapter.MyViewHolder holder, int position) {
        holder.onBind(notices.get(position));
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    void setItem(ArrayList<NoticeDTO> notices) {
        this.notices = notices;
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
            if ((tm_ct/1000) < 60) {
                tm_ct = tm_ct/1000;
                time = tm_ct.toString() + "초";
            }
            else if ((tm_ct/1000)/60 < 60) {
                tm_ct = tm_ct/1000/60;
                time = tm_ct.toString() + "분";
            }
            else if ((tm_ct/1000)/60/60 < 24) {
                tm_ct = tm_ct/1000/60/60;
                time = tm_ct.toString() + "시간";
            }
            else if ((tm_ct/1000)/60/60/24 < 31) {
                tm_ct = tm_ct/1000/60/60/24;
                time = tm_ct.toString() + "일";
            }
            else if ((tm_ct/1000)/60/60/24/30 < 12) {
                tm_ct = tm_ct/1000/60/60/24/30;
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
