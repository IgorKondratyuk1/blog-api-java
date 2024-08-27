package org.development.blogApi.user;

import jakarta.transaction.Transactional;
import org.development.blogApi.user.dto.request.CreateUserDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.RoleRepository;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserEntity create(CreateUserDto createUserDto, boolean isConfirmed) {
        RoleEntity roles = roleRepository.findRoleEntityByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = UserEntity.createInstance(createUserDto, passwordEncoder.encode(createUserDto.getPassword()), isConfirmed,  Collections.singletonList(roles));
        return userRepository.save(user);
    }

    public UserEntity findById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found")); // TODO create own errors .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void remove(UUID id) {
        userRepository.deleteById(id);
    }

//    @Transactional
//    public boolean setUserBanStatus(String userId, BanUserDto banUserDto) {
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        user.setIsBanned(banUserDto.isBanned(), banUserDto.banReason());
//        userRepository.save(user);
//        return true;
//    }
}
