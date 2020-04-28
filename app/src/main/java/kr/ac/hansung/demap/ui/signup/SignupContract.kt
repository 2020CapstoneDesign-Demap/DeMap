package kr.ac.hansung.demap.ui.signup

interface SignupContract {

    interface View {
        fun goToLogin()
        fun showProgress()
        fun hideProgress()
        fun showToastMessage(message: String)
        fun finishView()
    }

    interface Presenter {
        fun signUp()
        fun clearDisposable()
    }
}