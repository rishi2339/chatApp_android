package com.chat.androidApp.data.remote

import com.chat.androidApp.data.remote.dto.MessageDto
import com.chat.androidApp.domian.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.request

class MessageServiceImp(
    private val client: HttpClient
): MessageService {
    override suspend fun getAllMessages(): List<Message> {
        return try {
            client.get(MessageService.EndPoints.GetAllMessages.url)
                .body<List<MessageDto>>()
                .map { it.toMessage() }
        } catch (e: Exception){
            emptyList()
        }
    }
}