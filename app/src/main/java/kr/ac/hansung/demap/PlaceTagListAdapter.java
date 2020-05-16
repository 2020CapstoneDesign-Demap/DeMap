package kr.ac.hansung.demap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class PlaceTagListAdapter extends RecyclerView.Adapter<PlaceTagListAdapter.MyViewHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Context context;

    private ArrayList<String> placeTags = new ArrayList<String>();


    private static int tagCount;

    @NonNull
    @Override
    public PlaceTagListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_tag_list, parent, false);
        PlaceTagListAdapter.MyViewHolder vh = new PlaceTagListAdapter.MyViewHolder(view);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceTagListAdapter.MyViewHolder holder, int position) {
        if(position%3 == 0) {
            if (placeTags.get(position + 1) == null)
                holder.onOneBind(placeTags.get(position));
            else if(placeTags.get(position + 1) != null && placeTags.get(position + 2) == null)
                holder.onTwoBind(placeTags.get(position), placeTags.get(position + 1));
            else
                holder.onBind(placeTags.get(position), placeTags.get(position + 1),placeTags.get(position + 2));
        }
        else if(position%3 == 1 || position%3 == 2) {
            holder.onNoBind();
        }
        for(String tag : placeTags) {
            System.out.println("받아온 장소태그 : " + tag);
        }
        System.out.println("받아온 태그개수 : " + placeTags.size());

    }


    @Override
    public int getItemCount() {
        return placeTags.size();
    }

    void setItem(ArrayList<String> placeTags) {
        this.placeTags = placeTags;
        notifyDataSetChanged();
    }

    void setTagCount(ArrayList<String> placeTags) {
        this.tagCount = placeTags.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textview_place_tag1;
        public TextView textview_place_tag2;
        public TextView textview_place_tag3;

        public View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            textview_place_tag1 = itemView.findViewById(R.id.tag_left);
            textview_place_tag2 = itemView.findViewById(R.id.tag_middle);
            textview_place_tag3 = itemView.findViewById(R.id.tag_right);

        }

        void onBind(String key1, String key2, String key3) {
            textview_place_tag1.setText("#" + key1);
            textview_place_tag2.setText("#" + key2);
            textview_place_tag3.setText("#" + key3);

        }

        void onOneBind(String key1) {
            textview_place_tag1.setText("#" + key1);
            textview_place_tag2.setVisibility(View.INVISIBLE);
            textview_place_tag3.setVisibility(View.INVISIBLE);
        }

        void onTwoBind(String key1, String key2) {
            textview_place_tag1.setText("#" + key1);
            textview_place_tag2.setText("#" + key2);
            textview_place_tag3.setVisibility(View.INVISIBLE);
        }

        void onNoBind() {
            view.setVisibility(View.GONE);
            textview_place_tag1.setVisibility(View.GONE);
            textview_place_tag2.setVisibility(View.GONE);
            textview_place_tag3.setVisibility(View.GONE);
        }

    }
}
