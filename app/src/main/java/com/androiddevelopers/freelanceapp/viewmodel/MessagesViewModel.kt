package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {

    val currentUserId = auth.currentUser?.let { it.uid }

    private var _messages = MutableLiveData<List<MessageModel>>()
    val messages : LiveData<List<MessageModel>>
        get() = _messages

    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus : LiveData<Resource<Boolean>>
        get() = _messageStatus



    fun sendMessage(
        chatId : String,
        messageData : String,
        messageReceiver: String,
    ) {
        val usersMessage = createChatModelForCurrentUser(
            messageData ,
            currentUserId ?: "" ,
            messageReceiver
        )

        _messageStatus.value = Resource.loading(null)
        repo.sendMessageToRealtimeDatabase(currentUserId ?: "id yok",chatId,usersMessage)
            .addOnSuccessListener {
                _messageStatus.value = Resource.success(null)
            }
            .addOnFailureListener { error ->
                _messageStatus.value = error.localizedMessage?.let { Resource.error(it,null) }
            }
        repo.addMessageInChatMatesRoom(messageReceiver,chatId,usersMessage)
    }



    private fun createChatModelForCurrentUser(
        messageData: String,
        messageSender: String,
        messageReceiver: String
    ) : MessageModel {
        val messageId = UUID.randomUUID().toString()
        return MessageModel(
            messageId,
            messageData,
            messageSender,
            messageReceiver,
            getCurrentTime()
        )
    }

    fun getMessages(chatId : String) {
        _messageStatus.value = Resource.loading(null)
        repo.getAllMessagesFromRealtimeDatabase(currentUserId ?: "",chatId).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<MessageModel>()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(MessageModel::class.java)
                        message?.let {
                            messageList.add(it)
                        }
                    }
                    val sortedList = sortListByDate(messageList)
                    _messages.value = sortedList
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageStatus.value =  Resource.error(error.message,null) }
            }
        )
    }
    private fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }
    fun sortListByDate(yourList: List<MessageModel>): List<MessageModel> {
        return yourList.sortedBy { it.timestamp }
    }
}