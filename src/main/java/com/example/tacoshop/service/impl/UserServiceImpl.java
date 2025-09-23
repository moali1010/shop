package com.example.tacoshop.service.impl;

import com.example.tacoshop.dto.AuthResponse;
import com.example.tacoshop.dto.mapper.UserMapper;
import com.example.tacoshop.dto.request.UserLoginRequest;
import com.example.tacoshop.dto.request.UserRegistrationRequest;
import com.example.tacoshop.dto.response.UserResponse;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.UserRole;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.repository.UserRepository;
import com.example.tacoshop.security.JwtUtil;
import com.example.tacoshop.service.CreditService;
import com.example.tacoshop.service.UserService;
import com.example.tacoshop.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WalletService walletService;
    private final CreditService creditService;
    private static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(100000);

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists");
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.ROLE_CUSTOMER).build();
        User savedUser = userRepository.save(user);
        walletService.createWalletForUser(savedUser);
        creditService.createCreditForUser(savedUser);
        return UserMapper.toUserResponse(savedUser);
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElseThrow(() ->
                new BusinessException("USER_NOT_FOUND", "User not found"));
        if (!user.isEnabled()) {
            throw new BusinessException("USER_NOT_ACTIVE", "user deactivated");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token,
                new UserResponse(user.getId(), user.getUsername(), user.getRole().name()));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessException("USER_NOT_FOUND", "User not found"));
    }

}
