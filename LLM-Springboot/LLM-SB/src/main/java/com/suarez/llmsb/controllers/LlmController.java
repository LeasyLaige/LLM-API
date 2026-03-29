package com.suarez.llmsb.controllers;

import com.suarez.llmdata.model.PromptRequestDto;
import com.suarez.llmdata.model.PromptResponseDto;
import com.suarez.llmdata.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/llm")
@CrossOrigin
public class LlmController {

    private final LlmService llmService;

    public LlmController(LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/ask")
    public ResponseEntity<PromptResponseDto> ask(@RequestBody PromptRequestDto request) {
        PromptResponseDto response = llmService.ask(request);
        return ResponseEntity.ok(response);
    }
}

