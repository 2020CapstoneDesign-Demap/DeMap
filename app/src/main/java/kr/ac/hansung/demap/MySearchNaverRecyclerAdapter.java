package kr.ac.hansung.demap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ac.hansung.demap.model.FolderObj;

public class MySearchNaverRecyclerAdapter extends RecyclerView.Adapter<kr.ac.hansung.demap.MySearchNaverRecyclerAdapter.MyViewHolder> {


    private Context context;

    // adapter에 들어갈 folder list
    private String[] title = {"초기값","초기값","초기값","초기값","초기값"};
    private String[] category = {"초기값","초기값","초기값","초기값","초기값"};
    private String[] telephone = {"초기값","초기값","초기값","초기값","초기값"};
    private String[] roadaddress = {"초기값","초기값","초기값","초기값","초기값"};
    private int[] mapx = {0,0,0,0,0};
    private int[] mapy = {0,0,0,0,0};

    public MySearchNaverRecyclerAdapter() {
//        this.context = context;
        //title = null;
        //roadaddress = null;
        //mapx = null;
        //mapy = null;
    }

    @Override
    public void onBindViewHolder(@NonNull MySearchNaverRecyclerAdapter.MyViewHolder holder, int position) {
        if (title[0]!="초기값")
            holder.onBind(title[position], roadaddress[position], category[position], telephone[position]);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NaverSearchContentActivity.class);

                intent.putExtra("result_name", title[position]);
                intent.putExtra("result_addr", roadaddress[position]);
                intent.putExtra("result_category", category[position]);
                intent.putExtra("result_phone", telephone[position]);
                intent.putExtra("result_mapx", mapx[position]);
                intent.putExtra("result_mapy", mapy[position]);

                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public MySearchNaverRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 folder_list.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        // create a new view
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.naver_search_result_list, parent, false);
        MySearchNaverRecyclerAdapter.MyViewHolder vh = new MySearchNaverRecyclerAdapter.MyViewHolder(view);
        context = parent.getContext();
        return vh;
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return title.length;
    }

    public void addItems(String[] title, String[] roadaddress, String[] category, String[] telephone, int[] mapx, int[] mapy) {
        // 외부에서 item을 추가시킬 함수입니다.
        for (int i=0; i<getItemCount(); i++) {
            this.title[i] = title[i];
            this.roadaddress[i] = roadaddress[i];
            this.category[i] = category[i];
            this.telephone[i] = telephone[i];
            this.mapx[i] = mapx[i];
            this.mapy[i] = mapy[i];
        }

    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public TextView textView_searchresult_name;
        public TextView textView_searchresult_address;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView_searchresult_name = itemView.findViewById(R.id.textview_searchresult_name);
            textView_searchresult_address = itemView.findViewById(R.id.textview_seatchresult_address);
            //textView_searchresult_category = itemView.findViewById(R.id.textview_searchresult_category);

        }

        void onBind(String title, String roadaddress, String category, String phone) {
            textView_searchresult_name.setText(title);
            textView_searchresult_address.setText(roadaddress);
            //textView_searchresult_category.setText(category);
            // 화면에 띄운 결과 폴더는 리스트에서 삭제함
            //searchFolderResult.remove(folderObj);
        }
    }




}
