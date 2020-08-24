package kr.ac.hansung.demap;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.hansung.demap.ui.main.MainActivity;


public class ChatAdapter extends BaseAdapter {
    private ArrayList<ChatItem> chatList = new ArrayList<>();

    private int align;

    public void add(ChatItem chatItem){
        chatList.add(chatItem);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_list,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.idTextView = convertView.findViewById(R.id.contentText1);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentText2);
            viewHolder.chatContainer = convertView.findViewById(R.id.chatContainer);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if(!chatList.get(position).getId().equals(ChattingActivity.currentUserId)) {
            viewHolder.idTextView.setText(chatList.get(position).getId());
            viewHolder.contentTextView.setText(chatList.get(position).getContent());

            // 9 패치 이미지로 채팅 버블을 출력
            viewHolder.contentTextView.setBackground(parent.getContext().getResources().getDrawable(R.drawable.chat_bubble_gray));
            align = Gravity.LEFT;
            viewHolder.idTextView.setGravity(align);
            viewHolder.chatContainer.setGravity(align);


        } else {
            viewHolder.idTextView.setVisibility(View.GONE);
            viewHolder.contentTextView.setText(chatList.get(position).getContent());
            viewHolder.contentTextView.setTextColor(Color.WHITE);
            // 9 패치 이미지로 채팅 버블을 출력
            viewHolder.contentTextView.setBackground(parent.getContext().getResources().getDrawable(R.drawable.chat_bubble));
            align = Gravity.RIGHT;
            viewHolder.idTextView.setGravity(align);
            viewHolder.chatContainer.setGravity(align);

        }
        return convertView;
    }

    private class ViewHolder{
        LinearLayout chatContainer;
        TextView idTextView;
        TextView contentTextView;
    }
}
