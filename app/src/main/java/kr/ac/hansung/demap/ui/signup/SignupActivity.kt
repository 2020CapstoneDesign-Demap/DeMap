package kr.ac.hansung.demap.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.ac.hansung.demap.R
import kr.ac.hansung.demap.databinding.ActivitySignupBinding
import kr.ac.hansung.demap.ui.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SignupActivity : AppCompatActivity(), SignupContract.View {

    private val presenter: SignupContract.Presenter by inject {
        parametersOf(this)
    }
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.activity = this
        binding.presenter = presenter as SignupPresenter
    }

    fun signUp() {
        presenter.signUp()
    }

    override fun goToLogin() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }

    override fun showProgress() {
        binding.pbLoading.visibility = View.VISIBLE
    }
    override fun hideProgress() {
        binding.pbLoading.visibility = View.GONE
    }


    override fun showToastMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishView() {
        finish()
    }

    override fun onDestroy() {
        presenter.clearDisposable()
        super.onDestroy()
    }
}
