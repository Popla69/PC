package com.popla.perplexitydemo.webagent.domain.impl

import android.content.Context
import com.google.gson.Gson
import com.popla.perplexitydemo.BuildConfig
import com.popla.perplexitydemo.webagent.data.model.*
import com.popla.perplexitydemo.webagent.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NaturalLanguageProcessor using OpenAI API for command parsing
 */
@Singleton
class NaturalLanguageProcessorImpl @Inject constructor(
    private val context: Context
) : NaturalLanguageProcessor {
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.AGENT_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()
    
    private val gson = Gson()
    
    override suspend fun parseCommand(input: String): TaskIntent = withContext(Dispatchers.IO) {
        try {
            // First try rule-based parsing for common patterns
            val ruleBasedResult = parseWithRules(input)
            if (ruleBasedResult.confidence > 0.7f) {
                return@withContext ruleBasedResult
            }
            
            // Fall back to AI-powered parsing for complex commands
            parseWithAI(input)
        } catch (e: Exception) {
            // Fallback to rule-based parsing if AI fails
            parseWithRules(input)
        }
    }
    
    override suspend fun extractEntities(input: String): List<Entity> = withContext(Dispatchers.IO) {
        val entities = mutableListOf<Entity>()
        
        // Email extraction
        val emailRegex = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b""")
        emailRegex.findAll(input).forEach { match ->
            entities.add(Entity(
                type = EntityType.EMAIL,
                value = match.value,
                confidence = 0.95f,
                startIndex = match.range.first,
                endIndex = match.range.last + 1
            ))
        }
        
        // Phone number extraction
        val phoneRegex = Regex("""\+?1?[-.\s]?\(?[0-9]{3}\)?[-.\s]?[0-9]{3}[-.\s]?[0-9]{4}""")
        phoneRegex.findAll(input).forEach { match ->
            entities.add(Entity(
                type = EntityType.PHONE,
                value = match.value,
                confidence = 0.9f,
                startIndex = match.range.first,
                endIndex = match.range.last + 1
            ))
        }
        
        // URL extraction
        val urlRegex = Regex("""https?://[^\s]+""")
        urlRegex.findAll(input).forEach { match ->
            entities.add(Entity(
                type = EntityType.WEBSITE_URL,
                value = match.value,
                confidence = 0.95f,
                startIndex = match.range.first,
                endIndex = match.range.last + 1
            ))
        }
        
        // Price extraction
        val priceRegex = Regex("""\$[0-9]+\.?[0-9]*""")
        priceRegex.findAll(input).forEach { match ->
            entities.add(Entity(
                type = EntityType.PRICE,
                value = match.value,
                confidence = 0.9f,
                startIndex = match.range.first,
                endIndex = match.range.last + 1
            ))
        }
        
        // Date extraction (basic patterns)
        val dateRegex = Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b|\b\w+\s+\d{1,2},?\s+\d{4}\b""")
        dateRegex.findAll(input).forEach { match ->
            entities.add(Entity(
                type = EntityType.DATE,
                value = match.value,
                confidence = 0.8f,
                startIndex = match.range.first,
                endIndex = match.range.last + 1
            ))
        }
        
        // Person name extraction (simple heuristic)
        val nameRegex = Regex("""\b[A-Z][a-z]+\s+[A-Z][a-z]+\b""")
        nameRegex.findAll(input).forEach { match ->
            // Only add if not already covered by other entities
            val overlaps = entities.any { entity ->
                match.range.first < entity.endIndex && match.range.last >= entity.startIndex
            }
            if (!overlaps) {
                entities.add(Entity(
                    type = EntityType.PERSON_NAME,
                    value = match.value,
                    confidence = 0.7f,
                    startIndex = match.range.first,
                    endIndex = match.range.last + 1
                ))
            }
        }
        
        entities
    }
    
    override suspend fun clarifyAmbiguity(
        intent: TaskIntent,
        context: UserContext
    ): List<ClarificationQuestion> {
        val questions = mutableListOf<ClarificationQuestion>()
        
        // Check for missing required information based on action type
        when (intent.action) {
            ActionType.FILL_FORM -> {
                if (context.personalData == null) {
                    questions.add(ClarificationQuestion(
                        question = "I need your personal information to fill forms. Would you like to provide your name, email, and phone number?",
                        options = listOf("Yes, I'll provide my details", "No, fill manually", "Skip this form"),
                        field = "personal_data"
                    ))
                }
            }
            ActionType.NAVIGATE -> {
                if (intent.target == null && !intent.parameters.containsKey("url")) {
                    questions.add(ClarificationQuestion(
                        question = "Where would you like to navigate to?",
                        options = null,
                        field = "target_url"
                    ))
                }
            }
            ActionType.SHOP -> {
                if (!intent.parameters.containsKey("product") && !intent.parameters.containsKey("category")) {
                    questions.add(ClarificationQuestion(
                        question = "What product or category are you looking for?",
                        options = null,
                        field = "product_query"
                    ))
                }
            }
            ActionType.MONITOR -> {
                if (!intent.parameters.containsKey("frequency")) {
                    questions.add(ClarificationQuestion(
                        question = "How often should I check for changes?",
                        options = listOf("Every 5 minutes", "Every hour", "Daily", "Custom interval"),
                        field = "monitoring_frequency"
                    ))
                }
            }
            else -> {
                // No specific clarifications needed for other actions
            }
        }
        
        // Check for ambiguous parameters
        intent.ambiguities.forEach { ambiguity ->
            questions.add(ClarificationQuestion(
                question = ambiguity.question,
                options = ambiguity.possibleValues,
                field = ambiguity.field
            ))
        }
        
        return questions
    }
    
    override suspend fun validateCommand(input: String): CommandValidation {
        val warnings = mutableListOf<String>()
        val alternatives = mutableListOf<String>()
        
        // Basic validation
        if (input.isBlank()) {
            return CommandValidation(
                isValid = false,
                isSafe = true,
                warnings = listOf("Command cannot be empty"),
                suggestedAlternatives = listOf(
                    "Try: 'Fill this form with my details'",
                    "Try: 'Navigate to google.com'",
                    "Try: 'Monitor this page for changes'"
                )
            )
        }
        
        // Safety checks
        val isSafe = !containsUnsafePatterns(input)
        if (!isSafe) {
            warnings.add("This command may perform potentially unsafe actions")
        }
        
        // Length validation
        if (input.length > 1000) {
            warnings.add("Command is very long and may not be processed accurately")
            alternatives.add("Try breaking this into smaller, more specific commands")
        }
        
        // Complexity validation
        val commandComplexity = assessComplexity(input)
        if (commandComplexity > 0.8f) {
            warnings.add("This command is complex and may require clarification")
            alternatives.add("Try using simpler, more direct language")
        }
        
        return CommandValidation(
            isValid = true,
            isSafe = isSafe,
            warnings = warnings,
            suggestedAlternatives = alternatives
        )
    }
    
    private fun parseWithRules(input: String): TaskIntent {
        val lowercaseInput = input.lowercase()
        
        val action = when {
            // Form filling patterns
            lowercaseInput.contains("fill") && (lowercaseInput.contains("form") || lowercaseInput.contains("field")) -> ActionType.FILL_FORM
            lowercaseInput.contains("complete") && lowercaseInput.contains("form") -> ActionType.FILL_FORM
            lowercaseInput.contains("enter") && (lowercaseInput.contains("information") || lowercaseInput.contains("details")) -> ActionType.FILL_FORM
            
            // Navigation patterns
            lowercaseInput.startsWith("go to") || lowercaseInput.startsWith("navigate to") -> ActionType.NAVIGATE
            lowercaseInput.contains("open") && (lowercaseInput.contains("page") || lowercaseInput.contains("site")) -> ActionType.NAVIGATE
            lowercaseInput.contains("visit") -> ActionType.NAVIGATE
            
            // Shopping patterns
            lowercaseInput.contains("buy") || lowercaseInput.contains("purchase") -> ActionType.SHOP
            lowercaseInput.contains("find") && (lowercaseInput.contains("price") || lowercaseInput.contains("deal")) -> ActionType.SHOP
            lowercaseInput.contains("add to cart") -> ActionType.SHOP
            
            // Monitoring patterns
            lowercaseInput.contains("monitor") || lowercaseInput.contains("watch") -> ActionType.MONITOR
            lowercaseInput.contains("track") && (lowercaseInput.contains("price") || lowercaseInput.contains("change")) -> ActionType.MONITOR
            lowercaseInput.contains("alert") && lowercaseInput.contains("when") -> ActionType.MONITOR
            
            // Data extraction patterns
            lowercaseInput.contains("extract") || lowercaseInput.contains("scrape") -> ActionType.EXTRACT_DATA
            lowercaseInput.contains("get") && (lowercaseInput.contains("data") || lowercaseInput.contains("information")) -> ActionType.EXTRACT_DATA
            lowercaseInput.contains("save") && lowercaseInput.contains("from") -> ActionType.EXTRACT_DATA
            
            // Click patterns
            lowercaseInput.contains("click") -> ActionType.CLICK
            lowercaseInput.contains("press") && lowercaseInput.contains("button") -> ActionType.CLICK
            
            // Search patterns
            lowercaseInput.contains("search for") -> ActionType.SEARCH
            lowercaseInput.contains("look for") -> ActionType.SEARCH
            
            // Booking patterns
            lowercaseInput.contains("book") && (lowercaseInput.contains("appointment") || lowercaseInput.contains("reservation")) -> ActionType.BOOK_APPOINTMENT
            lowercaseInput.contains("schedule") -> ActionType.BOOK_APPOINTMENT
            
            // Social media patterns
            lowercaseInput.contains("post") && (lowercaseInput.contains("social") || lowercaseInput.contains("twitter") || lowercaseInput.contains("facebook")) -> ActionType.SOCIAL_MEDIA_POST
            
            else -> ActionType.UNKNOWN
        }
        
        val confidence = when (action) {
            ActionType.UNKNOWN -> 0.2f
            else -> 0.8f
        }
        
        val target = extractTarget(input)
        val parameters = extractParameters(input, action)
        val ambiguities = detectAmbiguities(input, action, parameters)
        
        return TaskIntent(
            action = action,
            target = target,
            parameters = parameters,
            confidence = confidence,
            ambiguities = ambiguities
        )
    }
    
    private suspend fun parseWithAI(input: String): TaskIntent = withContext(Dispatchers.IO) {
        val prompt = """
            Parse this user command for web automation and return a JSON response with the following structure:
            {
                "action": "FILL_FORM|NAVIGATE|CLICK|SEARCH|MONITOR|EXTRACT_DATA|SHOP|BOOK_APPOINTMENT|SOCIAL_MEDIA_POST|UNKNOWN",
                "target": "target URL or element if specified",
                "parameters": {"key": "value pairs of extracted parameters"},
                "confidence": 0.0-1.0,
                "ambiguities": [{"field": "field_name", "possibleValues": ["option1", "option2"], "question": "clarification question"}]
            }
            
            User command: "$input"
        """.trimIndent()
        
        val requestBody = gson.toJson(mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to listOf(
                mapOf("role" to "user", "content" to prompt)
            ),
            "temperature" to 0.3,
            "max_tokens" to 500
        )).toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("${BuildConfig.AGENT_API_BASE}chat/completions")
            .post(requestBody)
            .build()
        
        val response = httpClient.newCall(request).execute()
        
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            val aiResponse = gson.fromJson(responseBody, AIApiResponse::class.java)
            val content = aiResponse.choices.firstOrNull()?.message?.content
            
            if (content != null) {
                try {
                    val parsedIntent = gson.fromJson(content, AITaskIntent::class.java)
                    return@withContext TaskIntent(
                        action = ActionType.valueOf(parsedIntent.action),
                        target = parsedIntent.target,
                        parameters = parsedIntent.parameters,
                        confidence = parsedIntent.confidence,
                        ambiguities = parsedIntent.ambiguities.map { 
                            Ambiguity(it.field, it.possibleValues, it.question)
                        }
                    )
                } catch (e: Exception) {
                    // Fall back to rule-based parsing
                    return@withContext parseWithRules(input)
                }
            }
        }
        
        // Fallback to rule-based parsing
        parseWithRules(input)
    }
    
    private fun extractTarget(input: String): String? {
        // Extract URLs
        val urlRegex = Regex("""https?://[^\s]+""")
        val urlMatch = urlRegex.find(input)
        if (urlMatch != null) {
            return urlMatch.value
        }
        
        // Extract domain names
        val domainRegex = Regex("""\b[a-zA-Z0-9-]+\.[a-zA-Z]{2,}\b""")
        val domainMatch = domainRegex.find(input)
        if (domainMatch != null && !domainMatch.value.contains("@")) {
            return "https://${domainMatch.value}"
        }
        
        return null
    }
    
    private fun extractParameters(input: String, action: ActionType): Map<String, String> {
        val params = mutableMapOf<String, String>()
        
        when (action) {
            ActionType.FILL_FORM -> {
                if (input.contains("signup", ignoreCase = true)) params["form_type"] = "signup"
                if (input.contains("login", ignoreCase = true)) params["form_type"] = "login"
                if (input.contains("contact", ignoreCase = true)) params["form_type"] = "contact"
                if (input.contains("checkout", ignoreCase = true)) params["form_type"] = "checkout"
            }
            ActionType.SHOP -> {
                val priceRegex = Regex("""under\s+\$?([0-9,]+)""", RegexOption.IGNORE_CASE)
                priceRegex.find(input)?.let { match ->
                    params["max_price"] = match.groupValues[1]
                }
            }
            ActionType.MONITOR -> {
                if (input.contains("price", ignoreCase = true)) params["track_type"] = "price"
                if (input.contains("stock", ignoreCase = true)) params["track_type"] = "availability"
                if (input.contains("content", ignoreCase = true)) params["track_type"] = "content"
            }
            else -> {
                // No specific parameters for other actions
            }
        }
        
        return params
    }
    
    private fun detectAmbiguities(input: String, action: ActionType, parameters: Map<String, String>): List<Ambiguity> {
        val ambiguities = mutableListOf<Ambiguity>()
        
        // Detect vague references
        if (input.contains("this", ignoreCase = true) && action != ActionType.FILL_FORM) {
            ambiguities.add(Ambiguity(
                field = "target_element",
                possibleValues = listOf("Current page", "Selected element", "Visible form"),
                question = "What specifically should I interact with on this page?"
            ))
        }
        
        return ambiguities
    }
    
    private fun containsUnsafePatterns(input: String): Boolean {
        val unsafePatterns = listOf(
            "delete", "remove", "clear all", "format", "reset",
            "admin", "root", "sudo", "password", "credit card"
        )
        
        return unsafePatterns.any { pattern ->
            input.contains(pattern, ignoreCase = true)
        }
    }
    
    private fun assessComplexity(input: String): Float {
        var complexity = 0f
        
        // Length factor
        complexity += (input.length / 1000f).coerceAtMost(0.3f)
        
        // Multiple actions
        val actionWords = listOf("and", "then", "after", "before", "while")
        actionWords.forEach { word ->
            if (input.contains(word, ignoreCase = true)) complexity += 0.2f
        }
        
        // Conditional statements
        val conditionalWords = listOf("if", "when", "unless", "provided")
        conditionalWords.forEach { word ->
            if (input.contains(word, ignoreCase = true)) complexity += 0.15f
        }
        
        return complexity.coerceAtMost(1f)
    }
}

// Data classes for AI API response
private data class AIApiResponse(
    val choices: List<AIChoice>
)

private data class AIChoice(
    val message: AIMessage
)

private data class AIMessage(
    val content: String
)

private data class AITaskIntent(
    val action: String,
    val target: String?,
    val parameters: Map<String, String>,
    val confidence: Float,
    val ambiguities: List<AIAmbiguity>
)

private data class AIAmbiguity(
    val field: String,
    val possibleValues: List<String>,
    val question: String
)