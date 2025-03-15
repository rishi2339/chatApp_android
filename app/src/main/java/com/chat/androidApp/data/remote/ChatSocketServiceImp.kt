package com.chat.androidApp.data.remote

import com.chat.androidApp.data.remote.dto.MessageDto
import com.chat.androidApp.domian.model.Message
import com.chat.androidApp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.set
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class ChatSocketServiceImp(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(userName: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url.set { ("${ChatSocketService.EndPoints.ChatSocket.url}?userName=${userName}") }
            }
            if(socket?.isActive == true){
                Resource.Success(Unit)
            } else Resource.Error("Couldn't establish the connection")
        } catch (e: Exception){
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun sendMessage(message: String) {
       try {
            socket?.send(Frame.Text(message))
       } catch (e:Exception){
           e.printStackTrace()
       }
    }

    override fun observeMessages(): Flow<Message> {
        return  try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {  }
        } catch (e: Exception){
            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}