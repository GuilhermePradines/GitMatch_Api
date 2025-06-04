package br.com.gitmatch.gitmatch.controller;

import br.com.gitmatch.gitmatch.dto.LoginRequestDTO;
import br.com.gitmatch.gitmatch.dto.LoginResponseDTO;
import br.com.gitmatch.gitmatch.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
