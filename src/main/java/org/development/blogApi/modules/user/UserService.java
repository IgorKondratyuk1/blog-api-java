package org.development.blogApi.modules.user;

import jakarta.transaction.Transactional;
import org.development.blogApi.modules.user.exceptions.UserNotFoundException;
import org.development.blogApi.modules.auth.dto.request.RegistrationDto;
import org.development.blogApi.modules.user.entity.RoleEntity;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.development.blogApi.modules.user.repository.RoleRepository;
import org.development.blogApi.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
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

    public UserEntity create(RegistrationDto createUserDto, boolean isConfirmed) {
        RoleEntity roles = roleRepository.findRoleEntityByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = UserEntity.createInstance(createUserDto, passwordEncoder.encode(createUserDto.getPassword()), Collections.singletonList(roles), isConfirmed);
        return userRepository.save(user);
    }

    public UserEntity findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    public void remove(UUID id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
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
