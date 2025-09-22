package com.project.odoo_235.presentation.screens.user.screen.chatbot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ChatMessage(
    val id: String,
    val content: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AppChatBotViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyASSY9fkUZY2Q9cYsCd-mTMK0sr98lPh30" // Replace this with your API key securely
    )

    init {
        _messages.value = listOf(
            ChatMessage(
                id = "welcome",
                content = "Hello! I'm your app assistant bot, here to help you with any questions about this app. How can I assist you today?",
                isFromBot = true
            )
        )
    }

    fun sendMessage(context: Context, message: String) {
        if (message.isBlank()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            content = message,
            isFromBot = false
        )
        _messages.value = _messages.value + userMessage

        val fullPrompt = buildPrompt(message)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(fullPrompt)

                val replyText = response.text ?: "Sorry, I couldn't understand. Please try again."

                val botReply = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = replyText,
                    isFromBot = true
                )

                _messages.value = _messages.value + botReply
            } catch (e: Exception) {
                val errorReply = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = "Something went wrong. Please try again.",
                    isFromBot = true
                )
                _messages.value = _messages.value + errorReply
            }
        }
    }

    private fun buildPrompt(question: String): String {


        return """
            You are the intelligent and friendly assistant bot of this app. Your job is to help users by answering their questions about app features, navigation, usage, and troubleshooting.

            User Name:
            Age: 

            User's Message:
            "$question"

            Respond clearly, helpfully, and politely, tailored to the user's context.
        """.trimIndent()
    }
}
