package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.dto.request.PhieuTraHangRequest;
import com.example.FashionStoreBE.service.OrderService;
import com.example.FashionStoreBE.service.ReturnService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {
    private final ReturnService returnService;

    private final TokenProvider tokenProvider;

    public ReturnController(ReturnService returnService, TokenProvider tokenProvider) {
        this.returnService = returnService;
        this.tokenProvider = tokenProvider;
    }

    private int extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        return tokenProvider.getUserIdFromToken(jwt);
    }

    @PostMapping
    public ResponseEntity<String> createReturn(@RequestBody PhieuTraHangRequest request,
                                               HttpServletRequest httpServletRequest) {
        int userId = extractUserIdFromRequest(httpServletRequest);
        String result = returnService.createReturnRequest(request, userId);
        return ResponseEntity.ok(result);
    }
}
