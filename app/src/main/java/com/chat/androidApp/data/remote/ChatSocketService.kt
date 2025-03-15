package com.chat.androidApp.data.remote

import com.chat.androidApp.domian.model.Message
import com.chat.androidApp.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        userName: String
    ): Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object{
        const val BASE_URL = "http://10.0.2.2:8080"
    }

    sealed class EndPoints(val url:String){
        object ChatSocket: EndPoints("$BASE_URL/chat-socket")
    }

}