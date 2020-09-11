package kr.ac.hansung.demap.ui.chatting;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.ui.chatting.ChatItem;

public class ChatAdapter extends BaseAdapter {
    private ArrayList<ChatItem> chatList = new ArrayList<>();

    private String nickname;

    public void add(ChatItem chatItem){
        chatList.add(chatItem);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_list,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.chattextContainer1 = convertView.findViewById(R.id.chattextContainer1);
            viewHolder.chattextContainer2 = convertView.findViewById(R.id.chattextContainer2);
            viewHolder.idTextView = convertView.findViewById(R.id.contentText1);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentText2);
            viewHolder.time1TextView = convertView.findViewById(R.id.tv_chat_time1);
            viewHolder.time2TextView = convertView.findViewById(R.id.tv_chat_time2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        long time = chatList.get(position).getTimestamp();
        SimpleDateFormat dayTime = new SimpleDateFormat("aa hh:mm", java.util.Locale.getDefault());
        String str = dayTime.format(new Date(time));

        if(!chatList.get(position).getId().equals(nickname)) { // 다른 사람의 채팅일 경우

            viewHolder.chattextContainer1.setGravity(Gravity.LEFT);
            viewHolder.chattextContainer2.setGravity(Gravity.LEFT);
            viewHolder.idTextView.setText(chatList.get(position).getId());
            viewHolder.idTextView.setVisibility(View.VISIBLE);
            viewHolder.contentTextView.setBackground(parent.getContext().getResources().getDrawable(R.drawable.char2));
            viewHolder.contentTextView.setTextColor(R.color.colorLineGray7);
            viewHolder.contentTextView.setText(chatList.get(position).getContent());
            viewHolder.time1TextView.setVisibility(View.GONE);
            viewHolder.time2TextView.setText(str);
            viewHolder.time2TextView.setVisibility(View.VISIBLE);
        } else { // 내 채팅일 경우
            viewHolder.chattextContainer1.setGravity(Gravity.RIGHT);
            viewHolder.chattextContainer2.setGravity(Gravity.RIGHT);
            viewHolder.idTextView.setText("");
            viewHolder.idTextView.setVisibility(View.GONE);
            viewHolder.contentTextView.setBackground(parent.getContext().getResources().getDrawable(R.drawable.chat1));
            viewHolder.contentTextView.setTextColor(Color.WHITE);
            viewHolder.contentTextView.setText(chatList.get(position).getContent());
            viewHolder.time2TextView.setVisibility(View.GONE);
            viewHolder.time1TextView.setText(str);
            viewHolder.time1TextView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder{
        LinearLayout chattextContainer1;
        LinearLayout chattextContainer2;
        TextView idTextView;
        TextView contentTextView;
        TextView time1TextView;
        TextView time2TextView;
    }
}
