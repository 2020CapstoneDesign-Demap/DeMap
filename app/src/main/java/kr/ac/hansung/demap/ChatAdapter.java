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

public class ChatAdapter extends BaseAdapter {
    private ArrayList<ChatItem> chatList = new ArrayList<>();

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
            viewHolder.chattextContainer = convertView.findViewById(R.id.chattextContainer);
            viewHolder.idTextView = convertView.findViewById(R.id.contentText1);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentText2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if(!chatList.get(position).getId().equals(ChattingActivity.currentUserId)) {
            viewHolder.chattextContainer.setGravity(Gravity.LEFT);
//            viewHolder.idTextView.setText(chatList.get(position).getId());
            viewHolder.idTextView.setText("시루");
            viewHolder.contentTextView.setBackground(parent.getContext().getResources().getDrawable(R.drawable.char2));
            viewHolder.contentTextView.setText(chatList.get(position).getContent());
        } else {
            viewHolder.chattextContainer.setGravity(Gravity.RIGHT);
            viewHolder.idTextView.setText("");
            viewHolder.idTextView.setVisibility(View.GONE);
            viewHolder.contentTextView.setBackground(parent.getContext().getResources().getDrawable(R.drawable.chat1));
            viewHolder.contentTextView.setTextColor(Color.WHITE);
            viewHolder.contentTextView.setText(chatList.get(position).getContent());
        }
        return convertView;
    }

    private class ViewHolder{
        LinearLayout chattextContainer;
        TextView idTextView;
        TextView contentTextView;
    }
}