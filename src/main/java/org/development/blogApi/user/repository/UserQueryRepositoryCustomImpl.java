package org.development.blogApi.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.development.blogApi.comment.entity.Comment;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.infrastructure.common.utils.PaginationUtil;
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
        String sortValue = queryUserParams.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(queryUserParams);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, queryUserParams.getPageSize());
        int skipValue = PaginationUtil.getSkipValue(queryUserParams.getPageNumber(), queryUserParams.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);

        Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
        Predicate filterPredicate = getUsersFilters(userRoot, queryUserParams);

        criteriaQuery.where(filterPredicate);
        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(queryUserParams.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(queryUserParams.getSortBy())));
        }

        TypedQuery<UserEntity> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(queryUserParams.getPageSize());

        List<UserEntity> userEntities = query.getResultList();
        List<ViewUserDto> viewUserDto = userEntities.stream().map((user) -> UserMapper.toView(user)).collect(Collectors.toList());

        return new PaginationDto<ViewUserDto>(
                pagesCount,
                queryUserParams.getPageNumber(),
                queryUserParams.getPageSize(),
                totalCount,
                viewUserDto
        );
    }

    private Long getTotalCountWithFilters(QueryUserDto queryUserParams) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
        Predicate filterPredicate = getUsersFilters(userRoot, queryUserParams);

        criteriaQuery.where(filterPredicate);
        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate getUsersFilters(Root<UserEntity> userRoot, QueryUserDto queryObj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> andPredicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

//        if ("banned".equals(queryObj.getBanStatus())) {
//            filters.add("u.saUserBan IS NOT NULL");
//        } else if ("notBanned".equals(queryObj.getBanStatus())) {
//            filters.add("u.saUserBan IS NULL");
//        }

        if (queryObj.getSearchLoginTerm() != null && !queryObj.getSearchLoginTerm().isEmpty()) {
            orPredicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(userRoot.get("login")),
                    "%" + queryObj.getSearchLoginTerm().toUpperCase() + "%"));
        }

        if (queryObj.getSearchEmailTerm() != null && !queryObj.getSearchEmailTerm().isEmpty()) {
            orPredicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(userRoot.get("email")),
                    "%" + queryObj.getSearchEmailTerm().toUpperCase() + "%"));
        }

        Predicate orPredicate = orPredicates.isEmpty()  ? criteriaBuilder.conjunction()
                                                        : criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));

        Predicate andPredicate = andPredicates.isEmpty()    ? criteriaBuilder.conjunction()
                                                            : criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));

        return criteriaBuilder.and(andPredicate, orPredicate);
    }
}