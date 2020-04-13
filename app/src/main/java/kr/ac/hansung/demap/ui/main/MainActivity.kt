package kr.ac.hansung.demap.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hansung.demap.CreateFolderActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.hansung.demap.FolderListActivity
import kr.ac.hansung.demap.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_create_folder.setOnClickListener {
            createFolder()
        }

        btn_view_folder_list.setOnClickListener {
            viewFolderList()
        }
    }

    fun createFolder() {
        var intent = Intent(this, CreateFolderActivity::class.java)
        startActivity(intent)
    }

    fun viewFolderList() {
        var intent = Intent(this, FolderListActivity::class.java)
        startActivity(intent)
    }
}
