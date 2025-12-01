package com.financetracker.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OpenAIService — Refactored Version.
 * - Compliant with SonarQube (Clean Code).
 * - Optimized Regex & Reflection.
 * - Proper Logging.
 */
public class OpenAIService {

    private static final Logger logger = Logger.getLogger(OpenAIService.class.getName());

    // Constants to avoid "Magic Strings"
    private static final String MODEL_NAME = "gpt-5.1";
    private static final String SYSTEM_ROLE_PROMPT = 
            "Anda adalah penasihat keuangan pribadi yang berpengalaman. " +
            "Analisis data dengan konteks Indonesia, berikan saran praktis. " +
            "Setelah analisis ringkas, ajukan 1 pertanyaan lanjutan untuk memperjelas.";
    private static final int MAX_TOKENS = 4096;
    private static final double TEMPERATURE = 0.6;

    // Pre-compiled Patterns for Performance (java:S4248)
    private static final Pattern PATTERN_TEXT_QUOTED = Pattern.compile("text\\s*=\\s*\"([\\s\\S]*?)\"");
    private static final Pattern PATTERN_TEXT_SIMPLE = Pattern.compile("text\\s*=\\s*([^,\\}\\]]+)");
    private static final Pattern PATTERN_OUTPUT_TEXT = Pattern.compile("outputText\\s*=\\s*\\{[^}]*text\\s*=\\s*\"([\\s\\S]*?)\"\\}");

    private final OpenAIClient client;
    private final List<String> chatHistory = new ArrayList<>();
    private String currentReportContext = null;

    public OpenAIService() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Environment variable OPENAI_API_KEY tidak ditemukan. Set API key terlebih dahulu.");
        }
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    /**
     * Memulai sesi analisis berdasarkan ringkasan laporan.
     */
    public String startFinancialAdviceSession(String reportSummary) {
        String safeSummary = (reportSummary == null) ? "" : reportSummary;
        this.currentReportContext = safeSummary;
        this.chatHistory.clear();

        String userPrompt = "Berikut laporan keuangan pengguna:\n\n" + safeSummary +
                "\n\nTolong beri analisis utama dan akhiri dengan 1 pertanyaan lanjutan.";

        String combinedInput = SYSTEM_ROLE_PROMPT + "\n\n" + userPrompt;

        String aiReply = sendRequest(combinedInput);

        // Update history
        chatHistory.add("SYSTEM: " + SYSTEM_ROLE_PROMPT);
        chatHistory.add("USER: " + userPrompt);
        chatHistory.add("AI: " + aiReply);

        return aiReply;
    }

    /**
     * Lanjutan percakapan.
     */
    public String continueChat(String userMessage) {
        if (currentReportContext == null) {
            return "Sesi analisis belum dimulai. Klik 'Dapatkan Saran Keuangan (AI)' dulu.";
        }

        chatHistory.add("USER: " + userMessage);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Anda adalah penasihat keuangan yang sedang berdiskusi dengan user.\n")
                     .append("Berikut konteks laporan keuangan asli (referensi penting):\n")
                     .append(currentReportContext).append("\n\n")
                     .append("Riwayat percakapan:\n");

        for (String line : chatHistory) {
            promptBuilder.append(line).append("\n");
        }
        promptBuilder.append("\nPesan terbaru user:\n").append(userMessage);

        String aiReply = sendRequest(promptBuilder.toString());

        chatHistory.add("AI: " + aiReply);
        return aiReply;
    }

    public void endSession() {
        this.currentReportContext = null;
        this.chatHistory.clear();
    }

    // ---------------------
    // PRIVATE HELPER
    // ---------------------

    private String sendRequest(String input) {
        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(MODEL_NAME)
                    .input(input)
                    .maxOutputTokens(MAX_TOKENS)
                    .temperature(TEMPERATURE)
                    .build();

            Response resp = client.responses().create(params);
            return safeExtractText(resp);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Gagal menghubungi OpenAI", e);
            return "Maaf, terjadi kesalahan koneksi ke AI.";
        }
    }

    /**
     * Strategi ekstraksi berjenjang (Chain of Responsibility pattern sederhana).
     * 1. Coba Reflection (Struktur standard SDK).
     * 2. Fallback ke Regex pada toString().
     */
    private String safeExtractText(Response response) {
        if (response == null) return "[Tidak ada respons dari server]";

        // Strategi 1: Reflection Access
        String reflectedText = tryExtractViaReflection(response);
        if (reflectedText != null && !reflectedText.isBlank()) {
            return reflectedText;
        }

        // Strategi 2: Regex Parsing (Fallback)
        return tryExtractViaRegex(response);
    }

    // ---------------------
    // EXTRACTION STRATEGIES
    // ---------------------

    private String tryExtractViaReflection(Response response) {
        try {
            // Path: response.output() -> list.get(0)
            Object outputList = invokeIfExists(response, "output");
            if (!(outputList instanceof List) || ((List<?>) outputList).isEmpty()) {
                return null;
            }

            Object firstItem = ((List<?>) outputList).get(0);
            if (firstItem == null) return null;

            // Path A: item.text()
            Object directText = tryInvoke(firstItem, "text");
            if (directText != null) return directText.toString();

            // Path B: item.message().content() -> list.get(0).text()
            return extractFromMessageContent(firstItem);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Reflection extraction failed, falling back to regex.", e);
            return null;
        }
    }

    private String extractFromMessageContent(Object item) {
        Object messageObj = unwrapOptional(tryInvoke(item, "message"));
        if (messageObj == null) return null;

        Object contentObj = unwrapOptional(tryInvoke(messageObj, "content"));
        
        if (contentObj instanceof List && !((List<?>) contentObj).isEmpty()) {
            Object firstContent = ((List<?>) contentObj).get(0);
            
            // Try content.text()
            Object text = tryInvoke(firstContent, "text");
            if (text != null) return text.toString();

            // Try content.outputText().text()
            Object outputText = unwrapOptional(tryInvoke(firstContent, "outputText"));
            if (outputText != null) {
                Object deepText = tryInvoke(outputText, "text");
                if (deepText != null) return deepText.toString();
            }
        }
        return null;
    }

    private String tryExtractViaRegex(Response response) {
        try {
            Object outputList = invokeIfExists(response, "output");
            String raw = String.valueOf(outputList != null ? outputList.toString() : response.toString());

            Matcher m = PATTERN_TEXT_QUOTED.matcher(raw);
            if (m.find()) return m.group(1).trim();

            m = PATTERN_TEXT_SIMPLE.matcher(raw);
            if (m.find()) return m.group(1).replaceAll("[\\}\\]]+$", "").trim();

            m = PATTERN_OUTPUT_TEXT.matcher(raw);
            if (m.find()) return m.group(1).trim();

            // Last resort cleanup
            String cleaned = raw.replaceAll("\\s+", " ").trim();
            return (cleaned.length() > 1000) ? cleaned.substring(0, 1000) + "..." : cleaned;

        } catch (Exception t) {
            logger.log(Level.SEVERE, "Regex extraction failed", t);
            return "[Gagal parsing respons AI]";
        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    private static Object tryInvoke(Object target, String methodName) {
        if (target == null) return null;
        try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // Method not found or accessible, return null to continue chain
            return null;
        }
    }

    private static Object invokeIfExists(Object target, String methodName) {
        return tryInvoke(target, methodName);
    }

    private static Object unwrapOptional(Object possibleOptional) {
        if (possibleOptional instanceof Optional) {
            return ((Optional<?>) possibleOptional).orElse(null);
        }
        return possibleOptional;
    }
}