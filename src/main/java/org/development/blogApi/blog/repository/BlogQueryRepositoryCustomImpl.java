package org.development.blogApi.blog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.development.blogApi.blog.dto.response.ViewBlogDto;
import org.development.blogApi.blog.entity.Blog;
import org.development.blogApi.blog.utils.BlogMapper;
import org.development.blogApi.comment.entity.Comment;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.infrastructure.common.dto.FilterResult;
import org.development.blogApi.infrastructure.common.utils.PaginationUtil;
import org.development.blogApi.blog.dto.response.ViewExtendedBlogDto;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;
import java.util.stream.Collectors;


public class BlogQueryRepositoryCustomImpl implements BlogQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ViewBlogDto> findOneBlog(UUID blogId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Blog> criteriaQuery = criteriaBuilder.createQuery(Blog.class);

        Root<Blog> blogRoot = criteriaQuery.from(Blog.class);
        Predicate idPredicate = criteriaBuilder.equal(blogRoot.get("id"), blogId);
        criteriaQuery.where(idPredicate);
        TypedQuery<Blog> query = entityManager.createQuery(criteriaQuery);

        Blog blog;
        try {
            blog = query.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }

        return Optional.of(BlogMapper.toView(blog));
    }

    @Override
    public PaginationDto<ViewBlogDto> findAllBlogs(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedBlogs) {
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, skipBannedBlogs, null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Blog> criteriaQuery = criteriaBuilder.createQuery(Blog.class);

        Root<Blog> blogRoot = criteriaQuery.from(Blog.class);
        Predicate filterPredicate = getFilters(blogRoot, commonQueryParamsDto, skipBannedBlogs, null);
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(blogRoot.get(commonQueryParamsDto.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(blogRoot.get(commonQueryParamsDto.getSortBy())));
        }

        TypedQuery<Blog> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Blog> foundedBlogs = query.getResultList();
        List<ViewBlogDto> blogViewModels = foundedBlogs.stream()
                .map(blog -> BlogMapper.toView(blog))
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                blogViewModels
        );
    }

    @Override
    public PaginationDto<ViewExtendedBlogDto> findBlogsWithExtendedInfo(CommonQueryParamsDto commonQueryParamsDto) {
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, false, null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Blog> criteriaQuery = criteriaBuilder.createQuery(Blog.class);

        Root<Blog> blogRoot = criteriaQuery.from(Blog.class);
        Predicate filterPredicate = getFilters(blogRoot, commonQueryParamsDto, false, null);
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(blogRoot.get(commonQueryParamsDto.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(blogRoot.get(commonQueryParamsDto.getSortBy())));
        }

        TypedQuery<Blog> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Blog> foundedBlogs = query.getResultList();
        List<ViewExtendedBlogDto> blogViewModels = foundedBlogs.stream()
                .map(blog -> BlogMapper.toExtendedView(blog))
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                blogViewModels
        );
    }

    @Override
    public PaginationDto<ViewBlogDto> findBlogsByCreatedUserId(String userId, CommonQueryParamsDto commonQueryParamsDto) {
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, false, userId);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Blog> criteriaQuery = criteriaBuilder.createQuery(Blog.class);

        Root<Blog> blogRoot = criteriaQuery.from(Blog.class);
        Predicate filterPredicate = getFilters(blogRoot, commonQueryParamsDto, false, userId);
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(blogRoot.get(commonQueryParamsDto.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(blogRoot.get(commonQueryParamsDto.getSortBy())));
        }

        TypedQuery<Blog> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Blog> foundedBlogs = query.getResultList();
        List<ViewBlogDto> blogViewModels = foundedBlogs.stream()
                .map(blog -> BlogMapper.toView(blog))
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                blogViewModels
        );
    }

    private Long getTotalCountWithFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, String userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<Blog> blogRoot = criteriaQuery.from(Blog.class);
        Predicate filterPredicate = getFilters(blogRoot, commonQueryParamsDto, skipBannedComments, userId);

        criteriaQuery.where(filterPredicate);
        criteriaQuery.select(criteriaBuilder.count(blogRoot));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate getFilters(Root<Blog> blogRoot, CommonQueryParamsDto queryObj, boolean skipBannedComments, String userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> andPredicates = new ArrayList<>();

//        if (skipBannedComments) {
//            filters.append("bt.isBanned = false");
//            hasPreviousFilter = true;
//        }

        if (userId != null) {
            andPredicates.add(criteriaBuilder.equal(blogRoot.get("user").get("id"), UUID.fromString(userId)));
        }

        if (!queryObj.getSearchNameTerm().isBlank()) {
            andPredicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(blogRoot.get("name")),
                    "%" + queryObj.getSearchNameTerm().toUpperCase() + "%"));
        }

        Predicate resultPredicate = andPredicates.isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
        return resultPredicate;
    }
}
