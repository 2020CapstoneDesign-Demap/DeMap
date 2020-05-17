package kr.ac.hansung.demap.ui.nickname

import android.content.Context

interface NickNameContract {

    interface View{
        fun goToMain()
        fun showProgress()
        fun hideProgress()
        fun showToastMessage(message: String)
        fun finishView()
    }

    interface Presenter{
        fun setNickName(nickname: String, context: Context)
        fun clearDisposable()
    }
}