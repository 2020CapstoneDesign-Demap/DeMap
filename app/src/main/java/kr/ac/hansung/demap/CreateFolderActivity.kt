package kr.ac.hansung.demap

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_folder.*

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kr.ac.hansung.demap.model.FolderDTO
import kr.ac.hansung.demap.model.UserDTO
import org.koin.ext.getScopeName
import java.text.SimpleDateFormat
import java.util.*

class CreateFolderActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var folder_name_edittext: EditText
    private lateinit var folderCreateButton: Button

    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // ActionBar에 타이틀 변경
        getSupportActionBar()?.setTitle("새 폴더 추가");
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

//        val item_folder_tag1 = arrayOf<String>("맛집", "카페", "스포츠") //왼쪽 데이터
//        val item_folder_tag2 = arrayOf<String>("관광지", "뷰티", "기타") //오른쪽 데이터
        val item_folder_tag = arrayOf<String>("맛집", "카페", "스포츠", "관광지", "뷰티")

        val item_folder_icon = arrayOf<Int>(
            R.drawable.ic_folder_blue_24dp,
            R.drawable.ic_folder_violet_24dp,
            R.drawable.ic_folder_pink_24dp,
            R.drawable.ic_folder_peach_24dp,
            R.drawable.ic_folder_green_24dp
        )

        //ArrayAdapter로 생성
//        listView_public.adapter =
//            MyAdapterForPublic(this, R.layout.folder_public_list, item_pub, item_desc)
//        listView_edit_auth.adapter =
//            MyAdapterForEditAuth(this, R.layout.folder_public_list, item_edit_auth, item_edit_desc)
//        listView_folder_tag.adapter =
//            MyAdapterForFolderTag(this, R.layout.folder_tag_list, item_folder_tag1, item_folder_tag2)
//        listView_folder_icon.adapter =
//            MyAdapterForFolderIcon(this, R.layout.folder_icon_list, item_folder_icon)

        //공개 범위
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapterForPublic(item_pub, item_desc)
        recyclerView = findViewById<RecyclerView>(R.id.listView_public).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //수정 권한
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapterForPublic(item_edit_auth, item_edit_desc)
        recyclerView = findViewById<RecyclerView>(R.id.listView_edit_auth).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //폴더 태그
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapterForFolderTag(item_folder_tag)
        recyclerView = findViewById<RecyclerView>(R.id.listView_folder_tag).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //폴더 아이콘
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = MyAdapterForFolderIcon(item_folder_icon)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView_folder_icon).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        folder_name_edittext = findViewById(R.id.folder_name_edittext)
        folderCreateButton = findViewById(R.id.folder_create_btn)
        folderCreateButton.setOnClickListener {
            //create folder
            createFoler()
        }
    }

    fun createFoler() {

        //folder 데이터
        var folderDTO = FolderDTO()
        folderDTO.uid = auth?.currentUser?.uid //생성자 uid 일단 여기에 넣음(따로 빼서 저장하는 방법을 아직 모름)
        folderDTO.name = folder_name_edittext.text.toString()
        folderDTO.timestamp = System.currentTimeMillis()

        firestore?.collection("folders")?.document()?.set(folderDTO) //여기서 도큐먼트 이름(UID)이 랜덤하게 들어가는데

        //user 데이터
        var userDTO = UserDTO()
        userDTO.myfolders["documentUID"] = true //document의 UID를 어떻게 얻어서 넣을 것인지?

        firestore?.collection("users")?.document(auth?.currentUser?.uid!!)?.set(userDTO)

        //폴더 태그 저장
        //어댑터에서 받아온 데이터 저장
        //폴더 도큐먼트 UID를 알아내서 따로(새로운 컬렉션에) 저장해야하는데, 아직 방법을 모름
//        firestore?.collection("folderTags")?.document()

        //폴더 수정 권한(공개 범위) 저장
        //어댑터에서 받아온 데이터 저장
        //폴더 도큐먼트 UID를 알아내서 따로(새로운 컬렉션에) 저장해야하는데, 아직 방법을 모름
//        firestore?.collection("folderEditors")?.document()

        Toast.makeText(this, "폴더 생성 성공!", Toast.LENGTH_SHORT).show()
        finish()

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


