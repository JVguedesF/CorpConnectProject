package com.CorpConnec.service;

import com.CorpConnec.exception.DuplicateEntityException;
import com.CorpConnec.exception.EntityNotFoundException;
import com.CorpConnec.exception.InvalidStatusException;
import com.CorpConnec.model.dto.request.UserRequestDto;
import com.CorpConnec.model.dto.response.UserResponseDto;
import com.CorpConnec.model.entity.Role;
import com.CorpConnec.model.entity.User;
import com.CorpConnec.model.mappers.UserMapper;
import com.CorpConnec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {

        validateUserUniqueness(requestDto.username(), requestDto.email());

        User user = userMapper.toEntity(requestDto);
        configureNewUser(user, requestDto.password());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    private void validateUserUniqueness(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateEntityException("Username já está em uso");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEntityException("Email já está em uso");
        }
    }

    private void configureNewUser(User user, String password) {
        user.setRole(Role.USER);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(password));
    }

    public User findActiveUserById(UUID userId) {
        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId.toString()));
    }

    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UserRequestDto requestDto) {

        User existingUser = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));

        authService.verifyOwnership(existingUser.getId());
        validateUpdateUniqueness(existingUser, requestDto);

        updateUserFields(existingUser, requestDto);
        User updatedUser = userRepository.save(existingUser);

        return userMapper.toResponse(updatedUser);
    }

    private void validateUpdateUniqueness(User existingUser, UserRequestDto requestDto) {
        if (!existingUser.getUsername().equals(requestDto.username()) &&
                userRepository.existsByUsername(requestDto.username())) {
            throw new DuplicateEntityException("Username já está em uso");
        }

        if (!existingUser.getEmail().equals(requestDto.email()) &&
                userRepository.existsByEmail(requestDto.email())) {
            throw new DuplicateEntityException("Email já está em uso");
        }
    }

    private void updateUserFields(User user, UserRequestDto requestDto) {
        user.setName(requestDto.name());
        user.setUsername(requestDto.username());
        user.setEmail(requestDto.email());

        if (requestDto.password() != null && !requestDto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
        }

        try {
            user.setStatus(requestDto.status());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Status inválido: " + requestDto.status());
        }
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));

        authService.verifyOwnership(user.getId());
        user.setActive(false);
        userRepository.save(user);
    }
}