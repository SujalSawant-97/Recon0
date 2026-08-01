package com.example.Recon0.services;

import com.example.Recon0.dto.ai.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Service
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Autowired
    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }


    public Mono<EnhanceReportResponse> enhanceReport(EnhanceReportRequest request) {
        String prompt = buildEnhancementPrompt(request);

        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest geminiRequest = new GeminiRequest(List.of(content));

        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(geminiRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .doOnNext(response -> {
                    System.out.println("========================================");
                    System.out.println("Raw Gemini API Response Received:");
                    System.out.println(response);
                    System.out.println("========================================");
                })

                .map(this::extractEnhancedContent)
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
//                        .filter(this::isRetryable)
//                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
//                                new RuntimeException("AI service failed after multiple retries.", retrySignal.failure())
//                        )
//                )
                .onErrorMap(WebClientResponseException.class, this::handleAiApiException);
    }

    private String buildEnhancementPrompt(EnhanceReportRequest request) {
        return "You are a professional cybersecurity analyst. Please rewrite and enhance the following vulnerability report to be clear, professional, and detailed. Return the response as a simple JSON object with three keys: \"description\", \"stepsToReproduce\", and \"impact\". " +
                "IMPORTANT: The value for \"stepsToReproduce\" must be a single string, with each step separated by a newline character (\\n). Do not use a JSON array. " +
                "Do not include any other text or markdown formatting in your response.\n\n" +
                "Original Description: " + request.getDescription() + "\n" +
                "Original Steps to Reproduce: " + request.getStepsToReproduce() + "\n" +
                "Original Impact: " + request.getImpact();
    }

    private EnhanceReportResponse extractEnhancedContent(GeminiResponse geminiResponse) {
        if (geminiResponse != null && geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
            String rawText = geminiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();

            // Step 1: Clean the string by removing the Markdown wrapper.
            String cleanJson = rawText.trim().replace("```json", "").replace("```", "").trim();

            // Step 2: Use Jackson's ObjectMapper to safely parse the JSON.
            try {
                return objectMapper.readValue(cleanJson, EnhanceReportResponse.class);
            } catch (IOException e) {
                System.err.println("Failed to parse JSON from AI response: " + e.getMessage());
                return new EnhanceReportResponse("Failed to parse AI response.", "", "");
            }
        }
        return new EnhanceReportResponse("No content received from AI.", "", "");
    }
    private Throwable handleAiApiException(WebClientResponseException ex) {
        System.err.println("AI API responded with status code " + ex.getStatusCode() + " and body " + ex.getResponseBodyAsString());
        ex.printStackTrace(); // This prints the full error to the console for debugging.
        return new RuntimeException("AI service failed with status: " + ex.getStatusCode());
    }
    private boolean isRetryable(Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE;
    }

    public Mono<AiChatResponse> getChatbotResponse(AiChatRequest request) {
        String prompt = buildChatPrompt(request);
        return callGemini(prompt)
                .map(responseText -> new AiChatResponse(responseText));
    }

    private Mono<String> callGemini(String prompt) {
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest geminiRequest = new GeminiRequest(List.of(content));

        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(geminiRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
                    }
                    return "No response from AI.";
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new RuntimeException("AI service failed after multiple retries.", retrySignal.failure())
                        )
                );
    }
    private String buildChatPrompt(AiChatRequest request) {
        return "You are a helpful assistant for the Recon-0 bug bounty platform. Answer the following question concisely. The question is: " + request.getQuestion();
    }


}
