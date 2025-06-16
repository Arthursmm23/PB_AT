package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.model.Usuario;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realiza login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponse(responseCode = "200", description = "Login bem-sucedido")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getSenha());
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
    public ResponseEntity<Usuario> register(@Valid @RequestBody Usuario usuario) {
        Usuario registrado = authService.registrar(usuario);
        return ResponseEntity.ok(registrado);
    }

    
    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }


    public static class TokenResponse {
        private String token;

        public TokenResponse(String token) { this.token = token; }
        public String getToken() { return token; }
    }

    
    public static class ErrorResponse {
        private String mensagem;
        private LocalDateTime timestamp;

        public ErrorResponse(String mensagem, LocalDateTime timestamp) {
            this.mensagem = mensagem;
            this.timestamp = timestamp;
        }

        public String getMensagem() { return mensagem; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
