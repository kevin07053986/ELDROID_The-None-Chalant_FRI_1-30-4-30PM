package com.mab.buwisbuddyph.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Login method
    suspend fun loginUser(email: String, password: String): Result<User?> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User ID not found"))
            val userDocument = db.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Login failed: ${e.message}")
            Result.failure(e)
        }
    }

    // Sign-up method
    suspend fun registerUser(email: String, password: String, user: User): Result<User?> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User ID not found"))
            user.userID = uid  // Ensure the user object has the Firebase UID
            db.collection("users").document(uid).set(user).await()
            Result.success(user)
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("UserRepository", "User with this email already exists: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    // Forgot password method
    suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Password reset failed: ${e.message}")
            Result.failure(e)
        }
    }

    // Get user details from Firestore
    suspend fun getUserDetails(uid: String): Result<User?> {
        return try {
            val userDocument = db.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch user details: ${e.message}")
            Result.failure(e)
        }
    }

    // Update user details in Firestore
    suspend fun updateUserDetails(user: User): Result<Boolean> {
        return try {
            val uid = user.userID ?: return Result.failure(Exception("User ID is null"))
            db.collection("users").document(uid).set(user).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to update user details: ${e.message}")
            Result.failure(e)
        }
    }

    // Delete user account
    suspend fun deleteUserAccount(): Result<Boolean> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("User not signed in"))
            val uid = currentUser.uid

            // Delete user document from Firestore
            db.collection("users").document(uid).delete().await()

            // Delete user from Firebase Auth
            currentUser.delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to delete user account: ${e.message}")
            Result.failure(e)
        }
    }
}
