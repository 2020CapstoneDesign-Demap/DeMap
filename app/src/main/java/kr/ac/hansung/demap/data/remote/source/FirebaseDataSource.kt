package kr.ac.hansung.demap.data.remote.source

import com.google.firebase.auth.AuthCredential
import io.reactivex.Completable
import io.reactivex.Single

interface FirebaseDataSource {

    fun emailLogin(
        email: String,
        password: String
    ) : Completable

    fun emailRegister(
        email: String,
        password: String
    ): Completable

    fun firebaseAuthWithGoogle(credential: AuthCredential): Completable

    fun checkNickName() : Single<Boolean>

    fun setNickName(nickname: String) : Completable
}