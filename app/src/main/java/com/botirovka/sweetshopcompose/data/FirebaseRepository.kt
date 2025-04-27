package com.botirovka.sweetshopcompose.data

import android.util.Log
import com.botirovka.sweetshopcompose.models.Pie
import com.botirovka.sweetshopcompose.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var user = User()
    fun userUid(): String = auth.currentUser?.uid ?: ""
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    suspend fun logout() = auth.signOut()


    suspend fun login(email: String, password: String): Response<AuthResult> {
        try {
            val data = auth.signInWithEmailAndPassword(email, password).await()
            return Response.Success(data)
        } catch (e: Exception) {
            return Response.Error(e.localizedMessage ?: "Oops, something went wrong")
        }
    }

    suspend fun register(email: String, password: String): Response<AuthResult>{
        try {
            val data = auth.createUserWithEmailAndPassword(email, password).await()
            return Response.Success(data)
        } catch (e: Exception) {
            return Response.Error(e.localizedMessage ?: "Oops, something went wrong")
        }
    }

    suspend fun getPies(): Response<List<Pie>>  {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("pies").get().await()
            val pies = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pie::class.java)?.apply { id = doc.id }
            }
            Log.d("mydebug", "getPies: $pies")
            return Response.Success(pies)
        } catch (e: Exception) {
            Log.d("mydebug", "getPies: ${e.message}")
            return Response.Error(e.localizedMessage ?: "Oops, something went wrong.")
        }
    }

    suspend fun uploadInfo(user: User): Response<Void?> {
        try {
            userUid().let { userId ->
                val db = FirebaseFirestore.getInstance()
                val userDocRef = db.collection("users").document(userId)
                val document = userDocRef.get().await()
                if (document.exists()) {
                    userDocRef.update(
                        mapOf(
                            "likedPies" to user.likedPies,
                        )
                    ).await()
                } else {
                    userDocRef.set(user).await()
                }
                return Response.Success(null)
            }
        } catch (e: Exception) {
            return Response.Error(e.localizedMessage ?: "Oops, something went wrong.")
        }
    }

    suspend fun getUserInfo(): Result<User> {
        return try {
            val userId = userUid()
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val userDto = document.toObject(User::class.java)
                userDto?.let { Result.success(it) }
                    ?: Result.failure(Exception("User data is null"))
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}