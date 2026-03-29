package com.suarez.llmsb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.suarez.llmsb", "com.suarez.llmdata"})
@AutoConfigurationPackage(basePackages = "com.suarez.llmdata.entity")
@EnableJpaRepositories(basePackages = "com.suarez.llmdata.repository")
public class LlmSbApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmSbApplication.class, args);
    }

}
