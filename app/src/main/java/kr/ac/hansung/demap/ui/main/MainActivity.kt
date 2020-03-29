package kr.ac.hansung.demap.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
<<<<<<< HEAD:app/src/main/java/kr/ac/hansung/demap/MainActivity.kt
import kotlinx.android.synthetic.main.activity_main.*
=======
import kr.ac.hansung.demap.R
>>>>>>> f4c76bfccccdb450f187bd7a91dd9d22a5e2efda:app/src/main/java/kr/ac/hansung/demap/ui/main/MainActivity.kt

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
