package com.example.shop.service;

import com.example.shop.dto.UserDTO;
import com.example.shop.model.type.UserRole;
import com.example.shop.model.users.UserAccount;
import com.example.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserAccount toEntity(UserDTO dto) {
        return UserAccount.builder()
                .id(dto.getId())
                .name(dto.getName())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .userRole(dto.getUserRole() != null ? dto.getUserRole() : UserRole.CUSTOMER)
                .build();
    }

    private UserDTO toDTO(UserAccount entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .userRole(entity.getUserRole())
                .build();
    }

    public UserDTO saveUser(UserDTO userDTO) {
        UserAccount user = toEntity(userDTO);
        UserAccount saved = userRepository.save(user);
        return toDTO(saved);
    }

    public List<UserDTO> getAllCustomers() {
        return userRepository.findAllByUserRole(UserRole.CUSTOMER).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO updateProfile(Long userId, UserDTO updatedDTO) {
        UserAccount currentUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied: Cannot update other user's profile");
        }
        UserAccount user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(updatedDTO.getName());
        user.setEmail(updatedDTO.getEmail());
        user.setPhone(updatedDTO.getPhone());
        user.setAddress(updatedDTO.getAddress());
        if (updatedDTO.getPassword() != null && !updatedDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedDTO.getPassword()));
        }
        UserAccount saved = userRepository.save(user);
        return toDTO(saved);
    }

    public void deleteUser(Long userId) {
        UserAccount currentUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("Only admin can delete users");
        }
        userRepository.deleteById(userId);
    }

    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserAccount admin = UserAccount.builder()
                    .name("Admin")
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .userRole(UserRole.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin created: username=admin, password=admin");
        }
    }
}