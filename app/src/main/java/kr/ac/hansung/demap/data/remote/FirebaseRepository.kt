package kr.ac.hansung.demap.data.remote

import android.content.Context
import com.google.firebase.auth.AuthCredential
import io.reactivex.Completable
import io.reactivex.Single

interface FirebaseRepository {

    fun emailLogin(
        email: String,
        password: String
    ) : Completable

    fun emailRegister(
        email: String,
        password: String
    ) : Completable

    fun firebaseAuthWithGoogle(
        credential: AuthCredential
    ): Completable

    fun setNickName(
        nickname: String,
        context: Context
    ) : Completable

    fun checkNickName(): Single<Boolean>

}