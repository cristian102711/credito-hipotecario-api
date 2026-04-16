package com.creditohipotecario.api;

import com.creditohipotecario.api.dto.RegisterRequest;
import com.creditohipotecario.api.model.Rol;
import com.creditohipotecario.api.model.Usuario;
import com.creditohipotecario.api.repository.UsuarioRepository;
import com.creditohipotecario.api.security.JwtService;
import com.creditohipotecario.api.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Juan");
        registerRequest.setApellido("Pérez");
        registerRequest.setEmail("juan@test.com");
        registerRequest.setPassword("12345678");
        registerRequest.setTelefono("+56912345678");
        registerRequest.setRol(Rol.CLIENTE);
    }

    @Test
    void register_cuandoEmailNuevo_deberiaRetornarTokens() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("password-encriptado");
        when(usuarioRepository.save(any())).thenReturn(new Usuario());
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        var response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void register_cuandoEmailExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("El email ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}