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
    private String[] roadaddress = {"초기값","초기값","초기값","초기값","초기값"};
    //private String[] category = {"초기값","초기값","초기값","초기값","초기값"};
    private int[] mapx = {0,0,0,0,0};
    private int[] mapy = {0,0,0,0,0};

//    private ArrayList<FolderObj> folderObjs = new ArrayList<>();

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
            holder.onBind(title[position], roadaddress[position]/*, category[position]*/);

//        Log.d("log", "우왕"+searchFolderResult.get(position).getName());
//        holder.onBind(folderObjs.get(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(v.getContext(), FolderContentActivity.class);

                //intent.putExtra("folder_id", searchFolderResult.get(position).getId());
                //intent.putExtra("folder_name", searchFolderResult.get(position).getName());
//                intent.putExtra("folder_name", folderObjs.get(position).getName());
                //intent.putExtra("folder_subs_count", searchFolderResult.get(position).getSubscribeCount());

                //context.startActivity(intent);
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
         /*else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_naver_init, parent, false);
            MySearchNaverRecyclerAdapter.MyViewHolder vh = new MySearchNaverRecyclerAdapter.MyViewHolder(view,"초기값");
            context = parent.getContext();
            return vh;
        }*/
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return title.length;
    }

    void addItems(String[] title, String[] roadaddress, /*String[] category,*/ int[] mapx, int[] mapy) {
        // 외부에서 item을 추가시킬 함수입니다.
        //this.title = null;
        //this.roadaddress = null;
        //this.mapx = null;
        //this.mapy = null;
        for (int i=0; i<getItemCount(); i++) {
            this.title[i] = title[i];
            this.roadaddress[i] = roadaddress[i];
            //this.category[i] = category[i];
            this.mapx[i] = mapx[i];
            this.mapy[i] = mapy[i];
            System.out.println("어댑터로 넘어온 데이터 : " + (i+1) + this.title[i]);
            System.out.println("어댑터로 넘어온 데이터 : "+ (i+1) +this.roadaddress[i]);
            System.out.println("어댑터로 넘어온 mapx : " + this.mapx[i] + "mapy : " + this.mapy[i]);

        }

    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public TextView textView_searchresult_name;
        public TextView textView_searchresult_address;
        //public TextView textView_searchresult_category;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView_searchresult_name = itemView.findViewById(R.id.textview_searchresult_name);
            textView_searchresult_address = itemView.findViewById(R.id.textview_seatchresult_address);
            //textView_searchresult_category = itemView.findViewById(R.id.textview_searchresult_category);

        }

        //init뷰 띄울때 호출할 생성자
       /* public MyViewHolder(View itemView, String i) {
            super(itemView);
            view = itemView;
            textView_search_naver_init = itemView.findViewById(R.id.search_naver_init);
        }*/

        void onBind(String title, String roadaddress/*, String category*/) {
            textView_searchresult_name.setText(title);
            textView_searchresult_address.setText(roadaddress);
            //textView_searchresult_category.setText(category);
            // 화면에 띄운 결과 폴더는 리스트에서 삭제함
            //searchFolderResult.remove(folderObj);
        }
    }




}
