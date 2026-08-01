package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.ProgramDetailDto;
import com.example.Recon0.dto.ProgramDto;
import com.example.Recon0.services.ProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/programs")
@Tag(name="Program Info Api", description = "Program Info at User Side")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    @Operation(summary = "To get all programs for user or hacker")
    public ResponseEntity<ApiResponse<List<ProgramDto>>> getAllPrograms() {
        List<ProgramDto> programs = programService.getAllPrograms();
        return ResponseEntity.ok(ApiResponse.success(programs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "To get program detail using program id")
    public ResponseEntity<ApiResponse<ProgramDetailDto>> getProgramDetails(@PathVariable UUID id) {
        try {
            ProgramDetailDto program = programService.getProgramDetails(id);
            return ResponseEntity.ok(ApiResponse.success(program));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

