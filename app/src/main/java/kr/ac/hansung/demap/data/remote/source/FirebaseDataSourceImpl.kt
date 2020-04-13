package kr.ac.hansung.demap.data.remote.source

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single
import kr.ac.hansung.demap.model.User

class FirebaseDataSourceImpl : FirebaseDataSource {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun emailLogin(email: String, password: String): Completable =
        Completable.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(Throwable(task.exception))
                }
            }
        }

    override fun emailRegister(
        email: String,
        password: String
    ): Completable = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(Throwable(task.exception))
            }
        }
    }


    override fun firebaseAuthWithGoogle(credential: AuthCredential): Completable = Completable.create { emitter ->
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(Throwable(task.exception))
            }
        }
    }

    override fun checkNickName(): Single<Boolean> = Single.create { emitter ->
        firestore.collection(USERS_COLLECTION).document(firebaseAuth.currentUser?.uid!!).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emitter.onSuccess(task.result!!.get("nickName") != null)
                } else {
                    emitter.onError(Throwable(task.exception))
                }
            }
    }

    override fun setNickName(nickname: String): Completable = Completable.create { emitter ->
        var user = User(nickname, firebaseAuth.currentUser?.email)

        firestore.collection(USERS_COLLECTION).document(firebaseAuth.currentUser?.uid!!).set(user).addOnCompleteListener { task->
            if(task.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(Throwable(task.exception))
            }
        }
    }

    companion object{
        const val USERS_COLLECTION = "users"
    }
}
