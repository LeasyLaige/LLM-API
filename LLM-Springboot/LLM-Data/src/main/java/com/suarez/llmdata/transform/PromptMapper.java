package com.suarez.llmdata.transform;

import com.suarez.llmdata.entity.PromptInteraction;
import com.suarez.llmdata.model.PromptRequestDto;
import com.suarez.llmdata.model.PromptResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PromptMapper {

    public PromptInteraction toEntity(PromptRequestDto request, String response) {
        return PromptInteraction.builder()
                .prompt(request.getPrompt())
                .response(response)
                .build();
    }

    public PromptResponseDto toDto(PromptInteraction entity) {
        return new PromptResponseDto(
                entity.getPrompt(),
                entity.getResponse(),
                entity.getCreatedAt()
        );
    }
}

