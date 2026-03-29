package com.suarez.llmdata.repository;

import com.suarez.llmdata.entity.PromptInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptInteractionRepository extends JpaRepository<PromptInteraction, Long> {
}

