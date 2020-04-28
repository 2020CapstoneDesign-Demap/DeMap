package kr.ac.hansung.demap.ui.signup

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.ac.hansung.demap.data.remote.FirebaseRepository

class SignupPresenter(
    val view: SignupContract.View,
    private val firebaseRepository: FirebaseRepository
) : SignupContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    var _email: String? = null
    var _password: String? = null

    //Doing same thing with signup
    override fun signUp() {
        if (_email.isNullOrEmpty() || _password.isNullOrEmpty()) {
            view.showToastMessage("Invalid email or password")
            return
        }
        compositeDisposable.add(firebaseRepository.emailRegister(_email!!, _password!!).subscribeOn(
            Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                view.showProgress()
            }
            .doAfterTerminate {
                view.hideProgress()
            }
            .subscribe({
                view.showToastMessage("회원가입에 성공하였습니다")
                view.finishView()
            }, {
                view.showToastMessage("에러가 발생했습니다. 이유: $it" )
            })

        )
    }

    override fun clearDisposable() {
        compositeDisposable.clear()
    }

}