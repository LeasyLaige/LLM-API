package com.suarez.llmdata.service;

import com.suarez.llmdata.model.PromptRequestDto;
import com.suarez.llmdata.model.PromptResponseDto;

public interface LlmService {
    PromptResponseDto ask(PromptRequestDto request);
}

