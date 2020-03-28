package kr.ac.hansung.demap.data.remote.source

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single

class FirebaseDatasourceImpl : FirebaseDataSource {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun emailLogin(email: String, password: String): Completable =
        Completable.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onComplete()
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
            }
        }
    }


    override fun firebaseAuthWithGoogle(credential: AuthCredential): Completable = Completable.create { emitter ->
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                emitter.onComplete()
            }
        }
    }

    override fun checkNickName(): Single<Boolean> = Single.create { emitter ->
        firestore.collection("NickNames").document(firebaseAuth.currentUser?.uid!!).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emitter.onSuccess(task.result!!.get("nickname") != null)
                    Log.d("task", task.result!!.get("nickname").toString())
                }
            }
    }

    override fun setNickName(nickname: String): Completable = Completable.create { emitter ->
        var data = hashMapOf("nickname" to nickname)
        firestore.collection("NickNames").document(firebaseAuth.currentUser?.uid!!).set(data).addOnCompleteListener {task->
            if(task.isSuccessful) {
                emitter.onComplete()
            }
        }
    }

}
