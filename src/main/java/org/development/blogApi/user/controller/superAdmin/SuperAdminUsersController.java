package org.development.blogApi.user.controller.superAdmin;

import jakarta.validation.Valid;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.user.UserService;
import org.development.blogApi.auth.dto.request.RegistrationDto;
import org.development.blogApi.auth.dto.request.QueryUserDto;
import org.development.blogApi.auth.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.UserQueryRepository;
import org.development.blogApi.user.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewUserDto create(@RequestBody @Valid RegistrationDto createUserDto) {
        UserEntity createdUser = userService.create(createUserDto, true);
        return UserMapper.toView(createdUser);
    }

    @GetMapping("/")
    public PaginationDto<ViewUserDto> findAll(QueryUserDto queryUserDto) {
        return userQueryRepository.findAllUsersWithCustomQueries(queryUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("userId") String userId) {
        userService.remove(UUID.fromString(userId));
    }

    //    @PutMapping("/{userId}/ban")
    //    public ResponseEntity<Void> banUser(@PathVariable("userId") String userId, @RequestBody BanUserDto banUserDto) {
    //        commandBus.execute(new BanUserBySaCommand(userId, banUserDto));
    //        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //    }
}
