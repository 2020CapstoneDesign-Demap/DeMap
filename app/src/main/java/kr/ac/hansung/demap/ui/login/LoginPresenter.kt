package kr.ac.hansung.demap.ui.login

import com.google.firebase.auth.AuthCredential
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.ac.hansung.demap.data.remote.FirebaseRepository

class LoginPresenter(
    private val view: LoginContract.View,
    private val firebaseRepository: FirebaseRepository
) : LoginContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    var _email: String? = null
    var _password: String? = null



    override fun emailLogin() { //이메일로 로그인
        //validating email and password
        if (_email.isNullOrEmpty() || _password.isNullOrEmpty()) {
            view.showToastMessage("Invalid email or password")
            return
        }

        //calling login from repository to perform the actual authentication
        compositeDisposable.add(firebaseRepository.emailLogin(_email!!, _password!!).subscribeOn(
            Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                view.showProgress()
            }
            .doAfterTerminate {
                view.hideProgress()
            }
            .subscribe({
                checkNickName()
            }, {
                view.showToastMessage("이메일 로그인에 실패하였습니다. 이유: ${it.toString()}")
            }) )
    }

    override fun firebaseAuthWithGoogle(credential: AuthCredential) {
        compositeDisposable.add(firebaseRepository.firebaseAuthWithGoogle(credential).subscribeOn(
            Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                view.showProgress()
            }
            .doAfterTerminate {
                view.hideProgress()
            }
            .subscribe({
                checkNickName()
            },{
                view.showToastMessage("구글 로그인에 실패하였습니다. 이유: ${it.toString()}")
            })
        )
    }

    override fun checkNickName() {
        compositeDisposable.add(firebaseRepository.checkNickName()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                view.showProgress()
            }
            .doAfterTerminate {
                view.hideProgress()
            }
            .subscribe({ boolean ->
                if(boolean) {
                    view.goToMainPage()
                    view.finishView()
                }
                else {
                    view.goToNickName()
                    view.finishView()
                }
            }, {
                view.showToastMessage("에러 발생. 이유: ${it}")
        }))
    }

    override fun clearDisposable() {
        compositeDisposable.clear()
    }
}