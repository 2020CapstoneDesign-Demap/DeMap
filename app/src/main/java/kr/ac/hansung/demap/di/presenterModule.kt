package kr.ac.hansung.demap.di

import kr.ac.hansung.demap.ui.login.LoginContract
import kr.ac.hansung.demap.ui.login.LoginPresenter
import kr.ac.hansung.demap.ui.signup.SignupContract
import kr.ac.hansung.demap.ui.signup.SignupPresenter
import org.koin.dsl.module

val presenterModule = module {

    factory<LoginContract.Presenter> { (view: LoginContract.View) -> LoginPresenter(view, get()) }
    factory<SignupContract.Presenter> { (view: SignupContract.View) -> SignupPresenter(view, get()) }

}