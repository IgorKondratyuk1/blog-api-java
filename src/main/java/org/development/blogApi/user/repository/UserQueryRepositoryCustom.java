package org.development.blogApi.user.repository;


import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.user.dto.request.QueryUserDto;
import org.development.blogApi.user.dto.response.ViewUserDto;

public interface UserQueryRepositoryCustom {
    PaginationDto<ViewUserDto> findAllUsersWithCustomQueries(QueryUserDto queryUserParams);
}
