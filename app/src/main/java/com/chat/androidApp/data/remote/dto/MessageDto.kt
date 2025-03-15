package com.chat.androidApp.data.remote.dto

import com.chat.androidApp.domian.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class MessageDto(
    val text: String,
    val timeStamp: Long,
    val userName: String,
    val id: String
) {
    fun toMessage(): Message {
        val time = Date(timeStamp)
        return Message(
            text = text,
            formattedTime = DateFormat
                .getDateInstance(DateFormat.TIMEZONE_FIELD)
                .format(time),
            userName = userName
        )
    }
}