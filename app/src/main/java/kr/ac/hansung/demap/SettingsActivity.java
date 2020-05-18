package kr.ac.hansung.demap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.hansung.demap.ui.nickname.NickNameActivity;


public class SettingsActivity extends AppCompatActivity {

    private String nickname;

    private TextView tv_setting_nickname;

    private LinearLayout linearLayout_nickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("설정");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");

        tv_setting_nickname = findViewById(R.id.tv_setting_nickname);
        tv_setting_nickname.setText(nickname);

        linearLayout_nickname = findViewById(R.id.linear_setting_nickname);
        linearLayout_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NickNameActivity.class);
                intent.putExtra("flag", 1);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout_alarm = findViewById(R.id.linear_setting_alarm);
        linearLayout_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
//                startActivity(intent);
            }
        });
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
}
