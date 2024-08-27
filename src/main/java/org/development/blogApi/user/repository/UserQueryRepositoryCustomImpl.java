package org.development.blogApi.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.PaginationHelper;
import org.development.blogApi.user.dto.request.QueryUserDto;
import org.development.blogApi.user.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.utils.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserQueryRepositoryCustomImpl implements UserQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PaginationDto<ViewUserDto> findAllUsersWithCustomQueries(QueryUserDto queryUserParams) {
        String filters = getUsersFilters(queryUserParams);
        String jpql = "SELECT u FROM UserEntity u LEFT JOIN FETCH u.emailConfirmation ec " +
                "LEFT JOIN FETCH u.passwordRecovery pr "
                //+ " LEFT JOIN FETCH u.saUserBan sb "
                + filters +
                " ORDER BY u." + queryUserParams.getSortBy() + " " + queryUserParams.getSortDirection().toUpperCase();

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        if (queryUserParams.getSearchLoginTerm() != null && !queryUserParams.getSearchLoginTerm().isEmpty()) {
            query.setParameter("searchLoginTerm", "%" + queryUserParams.getSearchLoginTerm() + "%");
        }

        if (queryUserParams.getSearchEmailTerm() != null && !queryUserParams.getSearchEmailTerm().isEmpty()) {
            query.setParameter("searchEmailTerm", "%" + queryUserParams.getSearchEmailTerm() + "%");
        }

        Long totalCount = getTotalCountWithFilters(queryUserParams);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, queryUserParams.getPageSize());
        int skipValue = PaginationHelper.getSkipValue(queryUserParams.getPageNumber(), queryUserParams.getPageSize());

        query.setFirstResult(skipValue);
        query.setMaxResults(queryUserParams.getPageSize());

        List<UserEntity> userEntities = query.getResultList();
        List<ViewUserDto> viewUserDto = userEntities.stream().map((user) -> UserMapper.toView(user)).collect(Collectors.toList());
        System.out.println(viewUserDto);


        return new PaginationDto<ViewUserDto>(
                pagesCount,
                queryUserParams.getPageNumber(),
                queryUserParams.getPageSize(),
                totalCount,
                viewUserDto
        );
    }

    private Long getTotalCountWithFilters(QueryUserDto queryUserParams) {
        String filters = getUsersFilters(queryUserParams);
        String jpql = "SELECT COUNT(u) FROM UserEntity u " + filters;

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        if (queryUserParams.getSearchLoginTerm() != null && !queryUserParams.getSearchLoginTerm().isEmpty()) {
            query.setParameter("searchLoginTerm", "%" + queryUserParams.getSearchLoginTerm() + "%");
        }
        if (queryUserParams.getSearchEmailTerm() != null && !queryUserParams.getSearchEmailTerm().isEmpty()) {
            query.setParameter("searchEmailTerm", "%" + queryUserParams.getSearchEmailTerm() + "%");
        }

        return query.getSingleResult();
    }

    private String getUsersFilters(QueryUserDto queryObj) {
        StringBuilder sqlFilters = new StringBuilder();
        List<String> filters = new ArrayList<>();

//        if ("banned".equals(queryObj.getBanStatus())) {
//            filters.add("u.saUserBan IS NOT NULL");
//        } else if ("notBanned".equals(queryObj.getBanStatus())) {
//            filters.add("u.saUserBan IS NULL");
//        }

        if (queryObj.getSearchLoginTerm() != null && !queryObj.getSearchLoginTerm().isEmpty()) {
            filters.add("u.login LIKE :searchLoginTerm");
        }

        if (queryObj.getSearchEmailTerm() != null && !queryObj.getSearchEmailTerm().isEmpty()) {
            filters.add("u.email LIKE :searchEmailTerm");
        }

        if (!filters.isEmpty()) {
            sqlFilters.append(" WHERE ").append(String.join(" AND ", filters));
        }

        return sqlFilters.toString();
    }

}
