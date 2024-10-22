package org.development.blogApi.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.PaginationUtil;
import org.development.blogApi.auth.dto.request.QueryUserDto;
import org.development.blogApi.auth.dto.response.ViewUserDto;
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
        System.out.println(queryUserParams);
        String filters = getUsersFilters(queryUserParams);
        String jpql = "SELECT u FROM UserEntity u LEFT JOIN FETCH u.emailConfirmation ec " +
                "LEFT JOIN FETCH u.passwordRecovery pr "
                //+ " LEFT JOIN FETCH u.saUserBan sb "
                + filters +
                " ORDER BY u." + queryUserParams.getSortBy() + " " + queryUserParams.getSortDirection().toUpperCase();

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        if (queryUserParams.getSearchLoginTerm() != null && !queryUserParams.getSearchLoginTerm().isEmpty()) {
            query.setParameter("searchLoginTerm", "%" + queryUserParams.getSearchLoginTerm().toUpperCase() + "%");
        }
        if (queryUserParams.getSearchEmailTerm() != null && !queryUserParams.getSearchEmailTerm().isEmpty()) {
            query.setParameter("searchEmailTerm", "%" + queryUserParams.getSearchEmailTerm().toUpperCase() + "%");
        }

        Long totalCount = getTotalCountWithFilters(queryUserParams);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, queryUserParams.getPageSize());
        int skipValue = PaginationUtil.getSkipValue(queryUserParams.getPageNumber(), queryUserParams.getPageSize());

        query.setFirstResult(skipValue);
        query.setMaxResults(queryUserParams.getPageSize());

        List<UserEntity> userEntities = query.getResultList();
        System.out.println(userEntities.size());
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
            query.setParameter("searchLoginTerm", "%" + queryUserParams.getSearchLoginTerm().toUpperCase() + "%");
        }
        if (queryUserParams.getSearchEmailTerm() != null && !queryUserParams.getSearchEmailTerm().isEmpty()) {
            query.setParameter("searchEmailTerm", "%" + queryUserParams.getSearchEmailTerm().toUpperCase() + "%");
        }

        return query.getSingleResult();
    }

    // TODO rewrite like in posts query repo
    private String getUsersFilters(QueryUserDto queryObj) {
        StringBuilder sqlFilters = new StringBuilder();
        List<String> filters = new ArrayList<>();

//        if ("banned".equals(queryObj.getBanStatus())) {
//            filters.add("u.saUserBan IS NOT NULL");
//        } else if ("notBanned".equals(queryObj.getBanStatus())) {
//            filters.add("u.saUserBan IS NULL");
//        }

        List<String> searchFilters = new ArrayList<>();
        if (queryObj.getSearchLoginTerm() != null && !queryObj.getSearchLoginTerm().isEmpty()) {
            searchFilters.add("UPPER(u.login) LIKE :searchLoginTerm");
        }

        if (queryObj.getSearchEmailTerm() != null && !queryObj.getSearchEmailTerm().isEmpty()) {
            searchFilters.add("UPPER(u.email) LIKE :searchEmailTerm");
        }

        if (searchFilters.size() > 0) {
            filters.add(String.join(" OR ", searchFilters));
        }

        if (!filters.isEmpty()) {
            sqlFilters.append(" WHERE ").append(String.join(" AND ", filters));
        }

        return sqlFilters.toString();
    }

}
