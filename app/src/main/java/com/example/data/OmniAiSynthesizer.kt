package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SynthesizedMultiAnswer(
    val query: String,
    val deepSeekPerspective: String,
    val perplexityAcademicSummary: String,
    val stepByStepSolution: String,
    val stackOverflowAdvice: String,
    val isRealAi: Boolean
)

object OmniAiSynthesizer {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun queryAllSitesSynthesized(question: String): SynthesizedMultiAnswer = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackSimulatedAnswer(question)
        }

        val prompt = """
            You are the Nexus Omni-AI Universal Student Answer Synthesizer. A student has asked: "$question".
            You must synthesize a comprehensive answer by combining perspectives from 4 premier platforms.
            Respond strictly in valid JSON format with exactly these 4 string keys:
            {
               "deepSeekPerspective": "Algorithmic code, time complexity, or direct technical breakdown styling DeepSeek Coder...",
               "perplexityAcademicSummary": "Academic overview citing theoretical principles, historical context, and formal definitions...",
               "stepByStepSolution": "Clear Khan Academy / Wolfram style step-by-step math walkthrough or conceptual explanation...",
               "stackOverflowAdvice": "Pragmatic developer debugging advice, common edge cases, or best practices..."
            }
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val rawStr = response.body?.string() ?: ""
            
            if (response.isSuccessful && rawStr.isNotEmpty()) {
                val root = JSONObject(rawStr)
                val candidates = root.optJSONArray("candidates")
                val text = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: ""

                if (text.isNotEmpty()) {
                    val ansJson = JSONObject(text)
                    return@withContext SynthesizedMultiAnswer(
                        query = question,
                        deepSeekPerspective = ansJson.optString("deepSeekPerspective", "Algorithmic analysis generated."),
                        perplexityAcademicSummary = ansJson.optString("perplexityAcademicSummary", "Academic citation synthesized."),
                        stepByStepSolution = ansJson.optString("stepByStepSolution", "Step-by-step walkthrough complete."),
                        stackOverflowAdvice = ansJson.optString("stackOverflowAdvice", "Pragmatic developer advice formulated."),
                        isRealAi = true
                    )
                }
            }
            getFallbackSimulatedAnswer(question)
        } catch (e: Exception) {
            getFallbackSimulatedAnswer(question)
        }
    }

    private fun getFallbackSimulatedAnswer(q: String): SynthesizedMultiAnswer {
        return SynthesizedMultiAnswer(
            query = q,
            deepSeekPerspective = "DeepSeek Coder Perspective:\nFor query '$q', optimal execution complexity is O(N log N). Ensure clean separation of concerns and utilize asynchronous state flow to prevent thread contention.",
            perplexityAcademicSummary = "Perplexity Academic Overview:\nResearch literature identifies '$q' as a core foundational topic in computer science and STEM curricula. Theoretical models emphasize first-principles invariants.",
            stepByStepSolution = "Khan Academy Walkthrough:\n• Step 1: Define initial state and parse input constraints for '$q'.\n• Step 2: Apply invariant mathematical transformation.\n• Step 3: Validate boundary conditions and return formatted output.",
            stackOverflowAdvice = "StackOverflow Community Advice (384 Upvotes):\nAvoid manual wheel-reinvention. Verify dependency versions in Version Catalog and ensure Android lifecycle safety when updating state.",
            isRealAi = false
        )
    }
}
