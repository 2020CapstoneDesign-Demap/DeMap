package kr.ac.hansung.demap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.MyViewHolder> {

    private ArrayList<String> notices = new ArrayList<String>();

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

    void setItem(ArrayList<String> notices) {
        this.notices = notices;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textview_notice;

        public View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            textview_notice = itemView.findViewById(R.id.tv_notice);

        }

        void onBind(String notice) {
            textview_notice.setText(notice);

        }

    }
}
