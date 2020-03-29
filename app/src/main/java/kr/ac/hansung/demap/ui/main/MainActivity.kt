package kr.ac.hansung.demap.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
<<<<<<< HEAD
import kr.ac.hansung.demap.CreateFolderActivity

import kotlinx.android.synthetic.main.activity_main.*

import kr.ac.hansung.demap.R

=======
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.hansung.demap.CreateFolderActivity
import kr.ac.hansung.demap.R
>>>>>>> aec34c5c3589b06c1157c7e9c1534ba6907a5274

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_create_folder.setOnClickListener {
            createFolder()
        }
    }

    fun createFolder() {
        var intent = Intent(this, CreateFolderActivity::class.java)
        startActivity(intent)
    }
}
