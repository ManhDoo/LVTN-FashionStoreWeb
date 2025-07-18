package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.model.ReturnReason;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/return-reasons")
public class ReturnReasonController {
    @GetMapping
    public List<ReturnReasonResponse> getAllReturnReasons() {
        return Arrays.stream(ReturnReason.values())
                .map(r -> new ReturnReasonResponse(r.name(), r.getDescription()))
                .collect(Collectors.toList());
    }

    record ReturnReasonResponse(String code, String label) {}
}
