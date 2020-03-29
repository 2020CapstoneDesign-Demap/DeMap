package kr.ac.hansung.demap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_folder.*
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_create_folder.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.folder_public_list.*

class CreateFolderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActionBar에 타이틀 변경
        getSupportActionBar()?.setTitle("TEST");
        // ActionBar의 배경색 변경
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())

        // 홈 아이콘 표시
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_create_folder)
        //리스트에 들어갈 아이템 생성
        val item_pub = arrayOf<String>("비공개", "공개")
        val item_desc = arrayOf<String>("나만 볼 수 있음", "모든 사용자가 검색/조회 가능")

        val item_edit_auth = arrayOf<String>("불가능", "초대한 유저", "전체 유저")
        val item_edit_desc = arrayOf<String>("나만 수정할 수 있음", "초대된 유저만 수정 가능", "모든 사용자가 수정 가능")

        val item_folder_tag1 = arrayOf<String>("맛집", "카페", "스포츠") //왼쪽 데이터
        val item_folder_tag2 = arrayOf<String>("관광지", "뷰티", "기타") //오른쪽 데이터

        //ArrayAdapter로 생성
        listView_public.adapter =
            MyAdapterForPublic(this, R.layout.folder_public_list, item_pub, item_desc)
        listView_edit_auth.adapter =
            MyAdapterForEditAuth(this, R.layout.folder_public_list, item_edit_auth, item_edit_desc)
        listView_folder_tag.adapter =
            MyAdapterForFolderTag(this, R.layout.folder_tag_list, item_folder_tag1, item_folder_tag2)



    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.getItemId()

        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}


