package kr.ac.hansung.demap.ui.login

import com.google.firebase.auth.AuthCredential

interface LoginContract {

    interface View {
        fun googleLogin()
        fun goToMainPage()
        fun goToSignup()
        fun goToNickName()
        fun showProgress()
        fun hideProgress()
        fun showToastMessage(message: String)
        fun finishView()
    }

    interface Presenter {
        fun emailLogin()
        fun firebaseAuthWithGoogle(credential: AuthCredential)
        fun checkNickName()
        fun clearDisposable()
    }
}