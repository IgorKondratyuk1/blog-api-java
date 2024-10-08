package org.development.blogApi.superAdmin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.user.UserService;
import org.development.blogApi.auth.dto.request.RegistrationDto;
import org.development.blogApi.auth.dto.request.QueryUserDto;
import org.development.blogApi.auth.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.UserQueryRepository;
import org.development.blogApi.user.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sa/users")
public class SuperAdminUsersController {

    private final UserService userService;
    private final UserQueryRepository userQueryRepository;

    @Autowired
    public SuperAdminUsersController(UserService userService, UserQueryRepository userQueryRepository) {
        this.userService = userService;
        this.userQueryRepository = userQueryRepository;
    }

//    @PutMapping("/{userId}/ban")
//    public ResponseEntity<Void> banUser(@PathVariable("userId") String userId, @RequestBody BanUserDto banUserDto) {
//        commandBus.execute(new BanUserBySaCommand(userId, banUserDto));
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    @PostMapping
    public ResponseEntity<ViewUserDto> create(@RequestBody @Valid RegistrationDto createUserDto) {
        UserEntity createdUser = userService.create(createUserDto, true);
        ViewUserDto viewUserDto = UserMapper.toView(createdUser);
        return new ResponseEntity<>(viewUserDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginationDto<ViewUserDto>> findAll(QueryUserDto queryUserDto) {
        PaginationDto<ViewUserDto> result = userQueryRepository.findAllUsersWithCustomQueries(queryUserDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> remove(@PathVariable("userId") String userId) {
        userService.remove(UUID.fromString(userId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
