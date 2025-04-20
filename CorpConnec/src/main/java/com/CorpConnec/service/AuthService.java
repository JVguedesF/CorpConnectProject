package com.CorpConnec.service;

import com.CorpConnec.exception.AuthenticationException;
import com.CorpConnec.exception.AuthorizationException;
import com.CorpConnec.exception.EntityNotFoundException;
import com.CorpConnec.model.dto.request.LoginRequestDto;
import com.CorpConnec.model.entity.User;
import com.CorpConnec.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String authenticate(LoginRequestDto loginDto) {

        User user = userRepository.findByUsername(loginDto.username())
                .orElseThrow(() -> new EntityNotFoundException("User", loginDto.username()));

        validateUserAccount(user);
        validatePassword(loginDto.password(), user.getPassword());

        return tokenService.generateAccessToken(user);
    }

    public void verifyOwnership(UUID resourceOwnerId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!resourceOwnerId.equals(authenticatedUserId)) {
            logger.warn("Tentativa de acesso não autorizado. Usuário autenticado: {}, Recurso: {}",
                    authenticatedUserId, resourceOwnerId);
            throw new AuthorizationException("Acesso não autorizado ao recurso");
        }
    }

    private void validateUserAccount(User user) {
        if (!user.isActive()) {
            throw new AuthenticationException("Conta desativada");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthenticationException("Credenciais inválidas");
        }
    }

    public UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Usuário não autenticado");
        }

        return extractUserIdFromPrincipal(authentication.getPrincipal());
    }

    private UUID extractUserIdFromPrincipal(Object principal) {
        if (principal instanceof Jwt jwt) {
            try {
                return UUID.fromString(jwt.getSubject());
            } catch (IllegalArgumentException e) {
                throw new AuthenticationException("Token inválido");
            }
        }

        throw new AuthenticationException("Tipo de autenticação não suportado");
    }
}