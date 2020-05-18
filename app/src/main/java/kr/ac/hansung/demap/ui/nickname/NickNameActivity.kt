package kr.ac.hansung.demap.ui.nickname

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import kr.ac.hansung.demap.R
import kr.ac.hansung.demap.databinding.ActivityNicknameBinding
import kr.ac.hansung.demap.ui.login.LoginActivity
import kr.ac.hansung.demap.ui.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class NickNameActivity : AppCompatActivity(), NickNameContract.View {

    private var isIntent: Int = 0

    private val presenter: NickNameContract.Presenter by inject {
        parametersOf(this)
    }

    private lateinit var binding: ActivityNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(R.color.colorWhite, theme)

        var intent = intent
        isIntent = intent.getIntExtra("flag", 0)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_nickname)

        binding.btnNicknameConfirm.setOnClickListener {
            setNickName(binding.etNickname.text.toString())
        }

        binding.btnNicknameCancel.setOnClickListener {
            if (isIntent == 0) {
                Toast.makeText(applicationContext, "다시 로그인 하세요", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                goToLogin()
                finish()
            }
            else if (isIntent == 1) {
                finish()
            }
        }
    }


    fun setNickName(nickName: String) {
        presenter.setNickName(nickName, applicationContext)
    }

    override fun goToMain() {
        Intent(this, MainActivity::class.java).also{
            startActivity(it)
        }
    }

    fun goToLogin() {
        Intent(this, LoginActivity::class.java).also{
            startActivity(it)
        }
    }


    override fun showToastMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishView() {
        finish()
    }

    override fun showProgress() {
        binding.pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.pbLoading.visibility = View.GONE
    }
    override fun onBackPressed() {
        if (isIntent == 0) {
            Toast.makeText(applicationContext, "다시 로그인 하세요", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            goToLogin()
            finish()
        }
        else if (isIntent == 1) {
            finish()
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        presenter.clearDisposable()
        super.onDestroy()
    }

}