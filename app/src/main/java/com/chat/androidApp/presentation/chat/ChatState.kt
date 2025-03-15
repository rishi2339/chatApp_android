package com.chat.androidApp.presentation.chat

import com.chat.androidApp.domian.model.Message

data class ChatState(
    val message: List<Message> = emptyList(),
    val isLoading : Boolean = false
)
