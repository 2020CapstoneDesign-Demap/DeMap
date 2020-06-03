package kr.ac.hansung.demap.ui.nickname

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.ac.hansung.demap.data.remote.FirebaseRepository

class NickNamePresenter(
    private val view: NickNameContract.View,
    private val firebaseRepository: FirebaseRepository
) : NickNameContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun setNickName(nickname: String, context: Context) {
        compositeDisposable.add(firebaseRepository.setNickName(nickname, context).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                view.showProgress()
            }
            .doAfterTerminate {
                view.hideProgress()
            }
            .subscribe({
                view.showToastMessage("닉네임 설정에 성공 하였습니다.")
                view.goToMain()
                view.finishView()
            }, {
                view.showToastMessage("닉네임 설정에 실패하였습니다. 이유: ${it.toString()}")
            })
        )
    }

    override fun clearDisposable() {
        compositeDisposable.clear()
    }
}