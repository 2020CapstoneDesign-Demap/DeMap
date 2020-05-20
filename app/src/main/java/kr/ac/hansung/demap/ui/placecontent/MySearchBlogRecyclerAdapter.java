package kr.ac.hansung.demap.ui.placecontent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.hansung.demap.R;

public class MySearchBlogRecyclerAdapter extends RecyclerView.Adapter<MySearchBlogRecyclerAdapter.MyViewHolder> {


    private Context context;

    // adapter에 들어갈 folder list
    private String[] title = {"초기값","초기값","초기값","초기값","초기값"};
    private String[] description = {"초기값","초기값","초기값","초기값","초기값"};
    private String[] postdate = {"초기값","초기값","초기값","초기값","초기값"};
    private String[] link = {"초기값","초기값","초기값","초기값","초기값"};


    public MySearchBlogRecyclerAdapter() {

    }

    @Override
    public void onBindViewHolder(@NonNull MySearchBlogRecyclerAdapter.MyViewHolder holder, int position) {
        if (title[0]!="초기값")
            holder.onBind(title[position], description[position], postdate[position], link[position]);
        else {
            //holder.noBind();
        }
        checkNull(title);


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(link[position]);
                intent.setData(uri);

                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public MySearchBlogRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 folder_list.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        // create a new view
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_blog_result_list, parent, false);
        MySearchBlogRecyclerAdapter.MyViewHolder vh = new MySearchBlogRecyclerAdapter.MyViewHolder(view);
        context = parent.getContext();
        return vh;
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return title.length;
    }

    public void addItems(String[] title, String[] description, String[] postdate, String[] link) {
        // 외부에서 item을 추가시킬 함수입니다.
        for (int i=0; i<getItemCount(); i++) {
            this.title[i] = title[i];
            this.description[i] = description[i];
            this.postdate[i] = postdate[i];
            this.link[i] = link[i];
        }

    }

    private void checkNull(String[] title) {
        if(title[0] == null) {
            Toast.makeText(context,"검색 결과가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public TextView textView_searchresult_title;
        public TextView textView_searchresult_desc;
        public TextView textView_searchresult_date;
        public TextView textView_Options_myfolder;
        public ImageView imageView;
        public View diviber_view;


        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView_searchresult_title = itemView.findViewById(R.id.textview_blog_title);
            textView_searchresult_desc = itemView.findViewById(R.id.textview_blog_desc);
            textView_searchresult_date = itemView.findViewById(R.id.textview_blog_date);
            textView_Options_myfolder = itemView.findViewById(R.id.textView_Options_myfolder);
            imageView = itemView.findViewById(R.id.blog_img);
            diviber_view = itemView.findViewById(R.id.blog_diviber);

        }

        void onBind(String title, String description, String postdate, String link) {
            textView_searchresult_title.setText(title);
            textView_searchresult_desc.setText(description);
            textView_searchresult_date.setText(postdate);
            textView_Options_myfolder.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            diviber_view.setVisibility(View.VISIBLE);
        }

        void noBind() {
            textView_Options_myfolder.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            diviber_view.setVisibility(View.GONE);
        }
    }




}
