package com.CorpConnec.controller;

import com.CorpConnec.model.dto.request.LoginRequestDto;
import com.CorpConnec.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.authenticate(loginRequestDto);
        return ResponseEntity.ok(token);
    }
}
