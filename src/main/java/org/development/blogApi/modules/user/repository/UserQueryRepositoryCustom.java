package org.development.blogApi.modules.user.repository;


import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.auth.dto.request.QueryUserDto;
import org.development.blogApi.modules.auth.dto.response.ViewUserDto;

public interface UserQueryRepositoryCustom {
    PaginationDto<ViewUserDto> findAllUsersWithCustomQueries(QueryUserDto queryUserParams);
}
