package kr.ac.hansung.demap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.hansung.demap.model.NoticeSettingDTO;

public class SettingNoticeActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("알림");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_setting_notice);

        Intent intent = getIntent();

        uid = intent.getStringExtra("uid");

        SwitchMaterial switch_notice_myfolder_subs = findViewById(R.id.switch_notice_subs);
        SwitchMaterial switch_notice_subs_folder = findViewById(R.id.switch_notice_subs_folder);
        SwitchMaterial switch_notice_my_folder_place = findViewById(R.id.switch_notice_my_folder_place);

        switch_notice_myfolder_subs.setChecked(intent.getBooleanExtra("myfolderSubs", false));
        switch_notice_subs_folder.setChecked(intent.getBooleanExtra("subsfolderPlace", false));
        switch_notice_my_folder_place.setChecked(intent.getBooleanExtra("myfolderPlace", false));

        switch_notice_myfolder_subs.setOnCheckedChangeListener(new NoticeSwitchListener(1));
        switch_notice_subs_folder.setOnCheckedChangeListener(new NoticeSwitchListener(2));
        switch_notice_my_folder_place.setOnCheckedChangeListener(new NoticeSwitchListener(3));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class NoticeSwitchListener implements CompoundButton.OnCheckedChangeListener{

        private int flag;

        public NoticeSwitchListener(int flag) {
            this.flag = flag;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (flag == 1) { // 내 폴더 구독 알림
                if (isChecked) {
                    firestore.collection("userSettings").document(uid).update("myfolderSubs", true);
                } else {
                    firestore.collection("userSettings").document(uid).update("myfolderSubs", false);
                }
            }
            else if (flag == 2) { // 구독 폴더 장소 추가 알림
                if (isChecked) {
                    firestore.collection("userSettings").document(uid).update("subsfolderPlace", true);
                } else {
                    firestore.collection("userSettings").document(uid).update("subsfolderPlace", false);
                }
            }
            else if (flag == 3) { // 내 폴더 장소 추가 알림
                if (isChecked) {
                    firestore.collection("userSettings").document(uid).update("myfolderPlace", true);
                } else {
                    firestore.collection("userSettings").document(uid).update("myfolderPlace", false);
                }
            }
        }
    }
}
