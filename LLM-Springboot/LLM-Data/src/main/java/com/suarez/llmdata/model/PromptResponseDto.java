package com.suarez.llmdata.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponseDto {
    private String prompt;
    private String response;
    private LocalDateTime createdAt;
}
