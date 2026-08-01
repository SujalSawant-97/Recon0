package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.ai.AiChatRequest;
import com.example.Recon0.dto.ai.AiChatResponse;
import com.example.Recon0.dto.ai.EnhanceReportRequest;
import com.example.Recon0.dto.ai.EnhanceReportResponse;
import com.example.Recon0.services.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name="Gemini AI Api", description = "To Enhance the report")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/enhance-report")
    @Operation(summary = "To enhance the report using gemini")
    //@PreAuthorize("hasRole('HACKER')")
    public Mono<ResponseEntity<ApiResponse<EnhanceReportResponse>>> enhanceReport(@RequestBody EnhanceReportRequest request) {
        return aiService.enhanceReport(request)
                .map(enhancedReport -> ResponseEntity.ok(
                        ApiResponse.<EnhanceReportResponse>builder()
                                .success(true)
                                .data(enhancedReport)
                                .build()
                ))
                .doOnNext(responseEntity -> {
                    System.out.println("========================================");
                    System.out.println("Final API Response Prepared:");
                    System.out.println("Status Code: " + responseEntity.getStatusCode());
                    System.out.println("Headers: " + responseEntity.getHeaders());
                    System.out.println("Body: " + responseEntity.getBody());
                    System.out.println("========================================");
                })
        .onErrorResume(e -> {
                    // Log the error for debugging
                    e.printStackTrace();

                    ApiResponse<EnhanceReportResponse> errorResponse = ApiResponse.<EnhanceReportResponse>builder()
                            .success(false)
                            .message("Error enhancing report: " + e.getMessage())
                            .build();

                    // Return a Mono containing a 500 Internal Server Error response
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });


    }
    @PostMapping("/chat")
    public Mono<ResponseEntity<ApiResponse<AiChatResponse>>> chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.getChatbotResponse(request)
                .map(chatResponse -> ResponseEntity.ok(
                        ApiResponse.<AiChatResponse>builder()
                                .success(true)
                                .data(chatResponse)
                                .build()
                ));
    }
}
