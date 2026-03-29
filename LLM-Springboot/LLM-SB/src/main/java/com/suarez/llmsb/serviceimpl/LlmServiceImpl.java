package com.suarez.llmsb.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.suarez.llmdata.entity.PromptInteraction;
import com.suarez.llmdata.model.PromptRequestDto;
import com.suarez.llmdata.model.PromptResponseDto;
import com.suarez.llmdata.repository.PromptInteractionRepository;
import com.suarez.llmdata.service.LlmService;
import com.suarez.llmdata.transform.PromptMapper;

@Service
public class LlmServiceImpl implements LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmServiceImpl.class);
    private static final String DJANGO_LLM_URL = "http://llm-django:8000/api/generate/";

    private final RestTemplate restTemplate;
    private final PromptInteractionRepository repository;
    private final PromptMapper promptMapper;

    public LlmServiceImpl(RestTemplate restTemplate,
                           PromptInteractionRepository repository,
                           PromptMapper promptMapper) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.promptMapper = promptMapper;
    }

    @Override
    public PromptResponseDto ask(PromptRequestDto request) {
        log.info("Sending prompt to Django LLM service: {}", request.getPrompt());

        // Build body as a JSON String so RestTemplate sends Content-Length
        // instead of Transfer-Encoding: chunked (which Gunicorn/WSGI cannot
        // dechunk, causing the request body to arrive empty at Django).
        String promptEscaped = request.getPrompt()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        String jsonBody = "{\"prompt\":\"" + promptEscaped + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // Forward the prompt to the external Python/Django service
        PromptResponseDto djangoResponse = restTemplate.postForObject(
                DJANGO_LLM_URL,
                entity,
                PromptResponseDto.class
        );

        String responseText = (djangoResponse != null) ? djangoResponse.getResponse() : "No response";

        // Map to entity and save
        PromptInteraction interaction = promptMapper.toEntity(request, responseText);
        PromptInteraction saved = repository.save(interaction);
        log.info("Saved prompt interaction with id: {}", saved.getId());

        // Return the DTO
        return promptMapper.toDto(saved);
    }
}

