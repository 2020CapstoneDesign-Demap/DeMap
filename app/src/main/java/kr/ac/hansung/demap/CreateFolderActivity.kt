package kr.ac.hansung.demap

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.ac.hansung.demap.model.FolderDTO
import kr.ac.hansung.demap.model.UserMyFolderDTO
import kr.ac.hansung.demap.ui.createfolder.List_onClick_interface
import kr.ac.hansung.demap.ui.createfolder.MyAdapterForFolderIcon
import kr.ac.hansung.demap.ui.createfolder.MyAdapterForFolderTag
import kr.ac.hansung.demap.ui.createfolder.MyAdapterForPublic

class CreateFolderActivity : AppCompatActivity(), List_onClick_interface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var folder_name_edittext: EditText
    private lateinit var folderCreateButton: Button

    var storage: FirebaseStorage? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    //어댑터에서 넘겨받은 아이템 리스트 위치 저장하는 변수
    var position = arrayOfNulls<Int>(4) //0:공개 1:수정권한 2:태그 3:폴더아이콘

    //리스트에 들어갈 아이템
    val item_pub = arrayOf<String>("비공개", "공개")
    val item_desc = arrayOf<String>("나만 볼 수 있음", "모든 사용자가 검색/조회 가능")
    val item_edit_auth = arrayOf<String>("불가능", "초대한 유저", "전체 유저")
    val item_edit_desc = arrayOf<String>("나만 수정할 수 있음", "초대된 유저만 수정 가능", "모든 사용자가 수정 가능")
    val item_folder_tag = arrayOf<String>("맛집", "카페", "스포츠", "관광지", "뷰티")
    val item_folder_icon = arrayOf<Int>(
        R.drawable.ic_folder_blue_24dp,
        R.drawable.ic_folder_violet_24dp,
        R.drawable.ic_folder_pink_24dp,
        R.drawable.ic_folder_peach_24dp,
        R.drawable.ic_folder_green_24dp
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // ActionBar에 타이틀 변경
        supportActionBar?.setTitle("새 폴더 추가");
        // ActionBar의 배경색 변경
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.colorWhite))
        //getSupportActionBar()?.setBackgroundDrawable(object : ColorDrawable(0xFF339999.toInt())

        window.statusBarColor = resources.getColor(R.color.colorWhite, theme)

        // 홈 아이콘 표시
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_create_folder)

        //공개 범위
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapterForPublic(
            0,
            item_pub,
            item_desc,
            this
        )
        recyclerView = findViewById<RecyclerView>(R.id.listView_public).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //수정 권한
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapterForPublic(
            1,
            item_edit_auth,
            item_edit_desc,
            this
        )
        recyclerView = findViewById<RecyclerView>(R.id.listView_edit_auth).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //폴더 태그
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapterForFolderTag(
            item_folder_tag,
            this
        )
        recyclerView = findViewById<RecyclerView>(R.id.listView_folder_tag).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //폴더 아이콘
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = MyAdapterForFolderIcon(
            item_folder_icon,
            this
        )
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
//        folderDTO.uid = auth?.currentUser?.uid //생성자 uid 일단 여기에 넣음(따로 빼서 저장해야함)
        folderDTO.name = folder_name_edittext.text.toString()
        folderDTO.timestamp = System.currentTimeMillis()
        firestore?.collection("folders")?.add(folderDTO)?.addOnSuccessListener {

            var folderID = it.id //도큐먼트 ID 가져옴

            //폴더 생성자 정보 저장
            var owner: MutableMap<String, Object> = HashMap()
            owner.put("owner", auth?.currentUser?.uid as Object)
            firestore?.collection("folderOwner")?.document(folderID)?.set(owner)

            //폴더 구독자 정보 저장
            var subscriber: MutableMap<String, Object> = HashMap()
            var subscribers : MutableMap<String, Object> = HashMap()
            subscriber.put("subscribers", subscribers as Object)
            firestore?.collection("folderSubscribers")?.document(folderID)?.set(subscriber)

            // 내 폴더에 추가
            var doc = firestore?.collection("usersMyFolder")?.document(auth?.currentUser?.uid!!)
            firestore?.runTransaction {
                var usermyfolderDTO = UserMyFolderDTO()
                if (it.get(doc!!).toObject(UserMyFolderDTO::class.java) == null) { //리스트에 처음 들어갈 경우
                    usermyfolderDTO.myfolders[folderID] = true
                }
                else {
                    usermyfolderDTO = it.get(doc!!).toObject(UserMyFolderDTO::class.java)!!
                    usermyfolderDTO!!.myfolders[folderID] = true
                }
                it.set(doc, usermyfolderDTO)
            }

            //폴더 공개 범위 저장
            var public: MutableMap<String, Object> = HashMap()
            public.put("public", item_pub[position[0]!!] as Object) //어댑터에서 받아온 데이터 저장
            firestore?.collection("folderPublic")?.document(folderID)?.set(public)

            //폴더 수정 권한 저장
            var edit_auth: MutableMap<String, Object> = HashMap()
            edit_auth.put("edit_auth", item_edit_auth[position[1]!!] as Object)
            firestore?.collection("folderEditors")?.document(folderID)?.set(edit_auth)

            //폴더 태그 저장
            var folderTag: MutableMap<String, Object> = HashMap()
            folderTag.put("folderTag", item_folder_tag[position[2]!!] as Object)
            firestore?.collection("folderTags")?.document(folderID)?.set(folderTag)

            //폴더 아이콘 저장
            var folderIcon: MutableMap<String, Object> = HashMap()
            folderIcon.put("folderIcon", item_folder_icon[position[3]!!].toString() as Object)
//          firestore?.collection("folderIcon")?.document(folderCountID.count.toString())?.set(folderIcon)
        }

        Toast.makeText(this, "폴더 생성 성공!", Toast.LENGTH_SHORT).show()
        finish()

    }

    //어댑터에서 체크한 데이터 위치 넘겨받음
    override fun onCheckbox(index: Int, p: Int) {
        position[index] = p
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


