package com.botirovka.sweetshopcompose.data

import android.util.Log
import com.botirovka.sweetshopcompose.models.Pie
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
}