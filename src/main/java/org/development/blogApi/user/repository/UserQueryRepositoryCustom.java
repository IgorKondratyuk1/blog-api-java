package org.development.blogApi.user.repository;


import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.auth.dto.request.QueryUserDto;
import org.development.blogApi.auth.dto.response.ViewUserDto;

public interface UserQueryRepositoryCustom {
    PaginationDto<ViewUserDto> findAllUsersWithCustomQueries(QueryUserDto queryUserParams);
}
