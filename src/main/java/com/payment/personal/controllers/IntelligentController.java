package com.payment.personal.controllers;

import com.payment.personal.service.IntelligenceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replay/smart")
@RequiredArgsConstructor
public class IntelligentController {

    private final IntelligenceClient intelligenceClient;

    @GetMapping("/transcribe")
    public String test() {

        return intelligenceClient
                .transcribe(
                        "/Users/smart/work/intelligence-service/input/9f751580-f985-4493-86c1-8a41505ababd_voice.m4a");
    }
}
