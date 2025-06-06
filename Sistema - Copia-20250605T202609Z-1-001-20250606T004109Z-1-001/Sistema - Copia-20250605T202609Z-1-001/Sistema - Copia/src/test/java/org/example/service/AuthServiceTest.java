package org.example.service;

import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private AuthService authService;
    private UsuarioRepository usuarioRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();

        // Injeta as dependências via construtor
        authService = new AuthService(usuarioRepository, passwordEncoder);

        // Ajusta valores do JWT (normalmente vindos do application.yml)
        authService.jwtSecret = "MinhaChaveJWTSecreta123456";
        authService.jwtExpirationMs = 86400000L; // 1 dia
    }

    @Test
    void testRegistrar() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setId("1");
        novoUsuario.setNome("João");
        novoUsuario.setEmail("joao@email.com");
        novoUsuario.setSenha("senha123");
        novoUsuario.setRole("ROLE_ADMIN");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario registrado = authService.registrar(novoUsuario);

        assertNotNull(registrado.getSenha());
        assertNotEquals("senha123", registrado.getSenha());
        assertTrue(passwordEncoder.matches("senha123", registrado.getSenha()));
    }

    @Test
    void testLoginComCredenciaisCorretas() {
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setEmail("maria@email.com");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setRole("ROLE_MECANICO");

        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(usuario);

        String token = authService.login("maria@email.com", "senha123");

        assertNotNull(token);
        assertTrue(token.length() > 10); // simples validação de que um JWT foi gerado
    }

    @Test
    void testLoginComSenhaIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setEmail("maria@email.com");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setRole("ROLE_MECANICO");

        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(usuario);

        assertThrows(RuntimeException.class, () ->
                authService.login("maria@email.com", "senhaErrada")
        );
    }

    @Test
    void testLoginComUsuarioInexistente() {
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                authService.login("naoexiste@email.com", "qualquerSenha")
        );
    }
}
