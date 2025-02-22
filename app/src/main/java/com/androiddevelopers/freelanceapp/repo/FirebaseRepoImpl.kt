package com.androiddevelopers.freelanceapp.repo

import android.net.Uri
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    database: FirebaseDatabase,
    storage: FirebaseStorage
) : FirebaseRepoInterFace {
    //FirestoreRef
    private val userCollection = firestore.collection("users")
    private val freelancerPostCollection = firestore.collection("posts")
    private val employerPostCollection = firestore.collection("job_posting")
    private val discoverPostCollection = firestore.collection("discover_posts")
    //StorageRef
    private val imagesParentRef = storage.reference.child("user_images")
    //RealtimeRef
    private val messagesReference = database.getReference("users")
    private val userFollowRef = database.getReference("users_follow")


    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun forgotPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    override fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override fun addUserToFirestore(data: UserModel): Task<Void> {
        return userCollection.document(data.userId.toString()).set(data)
    }

    override fun deleteUserFromFirestore(documentId: String): Task<Void> {
        return userCollection.document(documentId).delete()
    }

    override fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot> {
        return userCollection.document(documentId).get()
    }

    override fun addFreelancerJobPostToFirestore(post: FreelancerJobPost): Task<Void> {
        return freelancerPostCollection.document(post.postId.toString()).set(post)
    }

    override fun getAllFreelancerJobPostFromFirestore(): Task<QuerySnapshot> {
        return freelancerPostCollection.get()
    }

    override fun getFreelancerJobPostWithDocumentByIdFromFirestore(documentId: String): Task<DocumentSnapshot> {
        return freelancerPostCollection.document(documentId).get()
    }

    override fun updateViewCountFreelancerJobPostWithDocumentById(
        postId: String,
        newCount: Int
    ): Task<Void> {
        return freelancerPostCollection.document(postId).update("viewCount", newCount)
    }

    override fun addEmployerJobPostToFirestore(job: EmployerJobPost): Task<Void> {
        return employerPostCollection.document(job.postId.toString()).set(job)
    }

    override fun getAllEmployerJobPostFromFirestore(): Task<QuerySnapshot> {
        return employerPostCollection.get()
    }

    override fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String): Task<DocumentSnapshot> {
        return employerPostCollection.document(documentId).get()
    }

    override fun updateViewCountEmployerJobPostWithDocumentById(
        postId: String,
        newCount: Int
    ): Task<Void> {
        return employerPostCollection.document(postId).update("viewCount", newCount)
    }

    override fun addImageToStorageForJobPosting(
        uri: Uri,
        uId: String,
        postId: String,
        file: String
    ): UploadTask {
        return imagesParentRef
            .child(uId)
            .child("job_posts")
            .child(postId)
            .child(file)
            .putFile(uri)
    }

    override fun addDiscoverPostImage(
        uri: Uri,
        uId: String,
        postId: String,
        file: String
    ): UploadTask {
        return imagesParentRef
            .child(uId)
            .child("job_posts")
            .child(postId)
            .child(file)
            .putFile(uri)
    }

    override fun sendMessageToRealtimeDatabase(
        userId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void> {
        return messagesReference.child(userId).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
    }

    override fun addMessageInChatMatesRoom(
        chatMateId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void> {
        return messagesReference.child(chatMateId).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
    }

    override fun getAllMessagesFromRealtimeDatabase(
        currentUserId: String,
        chatId: String
    ): DatabaseReference {
        return messagesReference.child(currentUserId).child(chatId).child("messages")
    }

    override fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void> {
        return messagesReference.child(currentUserId).child(chat.chatId.toString()).setValue(chat)
    }

    override fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void> {
        return messagesReference.child(userId).child(chat.chatId.toString()).setValue(chat)
    }

    override fun getAllChatRooms(currentUserId: String): DatabaseReference {
        return messagesReference.child(currentUserId)
    }

    override fun getUsersFromFirestore(): Task<QuerySnapshot> {
        return userCollection.get()
    }

    override fun uploadDiscoverPostToFirestore(post: DiscoverPostModel): Task<Void> {
        return discoverPostCollection.document(post.postId.toString()).set(post)
    }

    override fun getAllDiscoverPostsFromFirestore(): Task<QuerySnapshot> {
        return discoverPostCollection.get()
    }

    override fun uploadDataInUserNode(
        userId: String,
        data: Any,
        type: String,
        dataId: String
    ): Task<Void> {
        return userCollection.document(userId)
            .collection(type)
            .document(dataId)
            .set(data)
    }

   
   override fun getAllDiscoverPostsFromUser(userId : String): Task<QuerySnapshot> {
        return discoverPostCollection.whereEqualTo("postOwner", userId).get()
    }

    override fun getAllEmployerJobPostsFromUser(userId : String): Task<QuerySnapshot> {
        return employerPostCollection.whereEqualTo("employerId", userId).get()
    }

    override fun getAllFreelancerJobPostsFromUser(userId : String): Task<QuerySnapshot> {
        return freelancerPostCollection.whereEqualTo("freelancerId", userId).get()
    }

    override fun follow(currentUserId : String,followingId : String): Task<Void> {
        userFollowRef.child(followingId).child("followers").child(currentUserId).setValue(currentUserId)
        return userFollowRef.child(currentUserId).child("following").child(followingId).setValue(followingId)
    }
    override fun unFollow(currentUserId: String, followingId: String): Task<Void> {
        userFollowRef.child(followingId).child("followers").child(currentUserId).removeValue()
        return userFollowRef.child(currentUserId).child("following").child(followingId).removeValue()
    }
    override fun updateUserData(userId: String, updateData:  HashMap<String, Any?>): Task<Void> {
        return  userCollection.document(userId).update(updateData)
    }
    override fun getFollowers(userId: String): DatabaseReference {
        return userFollowRef.child(userId).child("followers")
    }

}