package com.example.shishusneh.ai

import com.google.ai.client.generativeai.GenerativeModel

object GeminiHelper {

    // Replace with your actual Gemini API key
    private const val API_KEY = "AIzaSyCo3YJA1eN1r9aRvLqWAVCsegm4uSx7378"

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    suspend fun ask(prompt: String): String {
        return try {
            val response = model.generateContent(prompt)
            response.text ?: "Sorry, I could not get a response."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}