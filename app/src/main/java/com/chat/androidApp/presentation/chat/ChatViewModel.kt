package com.chat.androidApp.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chat.androidApp.data.remote.ChatSocketService
import com.chat.androidApp.data.remote.MessageService
import com.chat.androidApp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _messageText = mutableStateOf<String>("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        getAllMessages()
        savedStateHandle.get<String>("userName")?.let { userName ->
            viewModelScope.launch {
                val result = chatSocketService.initSession(userName)
                when(result){
                    is Resource.Success -> {
                        chatSocketService.observeMessages()
                            .onEach {
                                val newList = state.value.message.toMutableList().apply {
                                    add(0,it)
                                }
                                _state.value = state.value.copy(
                                    message =  newList
                                )
                            }.launchIn(viewModelScope)
                    }
                    else -> {
                        _toastEvent.emit(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun onMessageChange(message: String){
        _messageText.value = message
    }

    fun disconnect(){
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    fun getAllMessages(){
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = messageService.getAllMessages()
            _state.value = state.value.copy(
                message = result,
                isLoading = false
            )
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            if(messageText.value.isNotBlank())
            chatSocketService.sendMessage(messageText.value)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}