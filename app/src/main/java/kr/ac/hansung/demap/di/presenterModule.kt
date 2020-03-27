package kr.ac.hansung.demap.di

import kr.ac.hansung.demap.ui.login.LoginContract
import kr.ac.hansung.demap.ui.login.LoginPresenter
import org.koin.dsl.module

val presenterModule = module {

    factory<LoginContract.Presenter> { (view: LoginContract.View) -> LoginPresenter(view, get()) }

}