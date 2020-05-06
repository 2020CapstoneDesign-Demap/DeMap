package kr.ac.hansung.demap.ui.hotPlace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.ac.hansung.demap.R;
import kr.ac.hansung.demap.model.HotPlaceDTO;

public class HotPlaceRecyclerAdapter extends RecyclerView.Adapter<HotPlaceRecyclerAdapter.ItemViewHolder> {

    private ArrayList<HotPlaceDTO> hotPlaceList = new ArrayList<>();

    @NonNull
    @Override
    public HotPlaceRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_play_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotPlaceRecyclerAdapter.ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.onBind(hotPlaceList.get(position));
    }

    @Override
    public int getItemCount() {
        return hotPlaceList.size();
    }

    public void clearHotPlace() {
        hotPlaceList.clear();
    }

    public void addHotPlace(HotPlaceDTO data) {
        hotPlaceList.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_hotPlaceTag;
        private ImageView iv_hotPlace;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hotPlaceTag = itemView.findViewById(R.id.tv_hotPlaceTag);
            iv_hotPlace = itemView.findViewById(R.id.iv_hotPlace);
        }

        void onBind(HotPlaceDTO data) {
            tv_hotPlaceTag.setText(data.getTag());
            Glide.with(itemView.getContext()).load(data.getImageUrl()).into(iv_hotPlace);
        }
    }
}
