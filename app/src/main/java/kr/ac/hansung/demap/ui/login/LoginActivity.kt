package kr.ac.hansung.demap.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kr.ac.hansung.demap.R
import kr.ac.hansung.demap.databinding.ActivityLoginBinding
import kr.ac.hansung.demap.ui.main.MainActivity
import kr.ac.hansung.demap.ui.nickname.NickNameActivity
import kr.ac.hansung.demap.ui.signup.SignupActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private val presenter: LoginContract.Presenter by inject {
        parametersOf(this)
    }

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.activity = this
        binding.presenter = presenter as LoginPresenter

    }

    override fun googleLogin() { //구글 계정으로 로그인
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(API_CODE)
            .requestEmail()
            .build()
        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess) {
                var account = result.signInAccount
                var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                firebaseAuthWithGoogle(credential)
            }
        }
    }

    fun firebaseAuthWithGoogle(credential: AuthCredential) {
        presenter.firebaseAuthWithGoogle(credential)
    }

    override fun goToMainPage() {
        Intent(this, MainActivity::class.java).also{
            startActivityForResult(it,  GOOGLE_LOGIN_CODE)
        }
        finish()
    }

    override fun goToSignup() {
        Intent(this, SignupActivity::class.java).also {
            startActivity(it)
        }
    }
    fun emailLogin() {
        presenter.emailLogin()
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishView() {
        finish()
    }


    override fun goToNickName() {
        Intent(this, NickNameActivity::class.java).also {
            startActivity(it)
        }
    }

    override fun showProgress() {
        binding.pbLoading.visibility = View.VISIBLE
    }
    override fun hideProgress() {
        binding.pbLoading.visibility = View.GONE
    }

    override fun onDestroy() {
        presenter.clearDisposable()
        super.onDestroy()
    }
    companion object {
        const val API_CODE =
            "998673808566-q099ljncri0spdg661anh6ocq6n660hd.apps.googleusercontent.com"
        const val GOOGLE_LOGIN_CODE = 9001
    }
}