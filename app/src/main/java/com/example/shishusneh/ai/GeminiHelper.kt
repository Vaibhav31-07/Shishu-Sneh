package com.example.shishusneh.ai

import android.util.Log
import com.example.shishusneh.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiHelper {

    private const val TAG = "GeminiHelper"
    
    // The key is now pulled from local.properties via BuildConfig for security
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
        }
    )

    suspend fun ask(prompt: String): String = withContext(Dispatchers.IO) {
        if (API_KEY.isEmpty() || API_KEY == "PASTE_YOUR_API_KEY_HERE") {
            return@withContext "Error: API Key missing. Please add your key to local.properties"
        }

        try {
            Log.d(TAG, "Requesting AI response...")
            val response = model.generateContent(prompt)
            response.text ?: "The AI could not generate a response. Please try rephrasing."
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unknown Error"
            Log.e(TAG, "Gemini Error: $errorMsg")
            
            if (errorMsg.contains("Unexpected Response", ignoreCase = true)) {
                "AI Error: 'Unexpected Response'.\n\nThis usually means your API Key is invalid or restricted. Please verify your key at https://aistudio.google.com/"
            } else {
                "AI Error: $errorMsg"
            }
        }
    }
}
