package com.chat.androidApp.di

import com.chat.androidApp.data.remote.ChatSocketService
import com.chat.androidApp.data.remote.ChatSocketServiceImp
import com.chat.androidApp.data.remote.MessageService
import com.chat.androidApp.data.remote.MessageServiceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.serializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient{
        return HttpClient(CIO){
            install(Logging)
            install(WebSockets)
            install(ContentNegotiation){
                json()
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient):MessageService{
        return MessageServiceImp(client)
    }

    @Provides
    @Singleton
    fun provideChatSocketService(client: HttpClient):ChatSocketService{
        return ChatSocketServiceImp(client)
    }
}