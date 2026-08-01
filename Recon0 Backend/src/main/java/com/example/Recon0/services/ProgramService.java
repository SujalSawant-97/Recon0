package com.example.Recon0.services;

import com.example.Recon0.dto.ProgramDetailDto;
import com.example.Recon0.dto.ProgramDto;
import com.example.Recon0.repositories.ProgramRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable("programs")
    public List<ProgramDto> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(ProgramDto::fromProgram)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(key="progbyid", value = "id")
    public ProgramDetailDto getProgramDetails(UUID id) {
        return programRepository.findById(id)
                .map(ProgramDetailDto::fromProgram)
                .orElseThrow(() -> new RuntimeException("Program not found"));
    }
}
