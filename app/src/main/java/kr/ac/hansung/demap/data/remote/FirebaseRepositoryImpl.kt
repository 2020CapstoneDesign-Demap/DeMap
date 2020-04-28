package kr.ac.hansung.demap.data.remote

import com.google.firebase.auth.AuthCredential
import io.reactivex.Completable
import io.reactivex.Single
import kr.ac.hansung.demap.data.remote.source.FirebaseDataSource

class FirebaseRepositoryImpl(
    private val firebaseDataSource: FirebaseDataSource
) : FirebaseRepository {

    override fun emailLogin(email: String, password: String): Completable {
        return firebaseDataSource.emailLogin(email,password)
    }


    override fun emailRegister(
        email: String,
        password: String
    ): Completable = firebaseDataSource.emailRegister(email, password)

    override fun firebaseAuthWithGoogle(
        credential: AuthCredential
    ): Completable = firebaseDataSource.firebaseAuthWithGoogle(credential)


    override fun setNickName(
        nickname: String
    ): Completable = firebaseDataSource.setNickName(nickname)

    override fun checkNickName(): Single<Boolean> {
        return firebaseDataSource.checkNickName()
    }
}