package org.development.blogApi.blog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.blog.dto.response.ViewBlogDto;
import org.development.blogApi.blog.entity.Blog;
import org.development.blogApi.blog.utils.BlogMapper;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.infrastructure.common.dto.FilterResult;
import org.development.blogApi.infrastructure.common.utils.PaginationUtil;
import org.development.blogApi.blog.dto.response.ViewExtendedBlogDto;

import java.util.*;
import java.util.stream.Collectors;


public class BlogQueryRepositoryCustomImpl implements BlogQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    // TODO refactor QueryRepositories
    @Override
    public Optional<ViewBlogDto> findOneBlog(UUID id) {
        String jpql = "SELECT bt FROM Blog bt WHERE bt.id = :blogId";

        // AND bt.isBanned = FALSE";

        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        query.setParameter("blogId", id);
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
        FilterResult filterResult = getFilters(commonQueryParamsDto, skipBannedBlogs, null);


        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, skipBannedBlogs, null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        // Fetch posts with filters and pagination
        String jpql = "SELECT bt FROM Blog bt " + filterResult.getQuery() +
                " ORDER BY bt." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Blog> foundedBlogs = query.getResultList();

        // Convert to ViewPostDto
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
        FilterResult filterResult = getFilters(commonQueryParamsDto, false, null);

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, false, null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        // Fetch posts with filters and pagination
        String jpql = "SELECT bt FROM Blog bt " + filterResult.getQuery() +
                " ORDER BY pt." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Blog> foundedBlogs = query.getResultList();

        // Convert to ViewPostDto
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

        FilterResult filterResult = getFilters(commonQueryParamsDto, false, userId);

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, false, userId);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        // Fetch posts with filters and pagination
        String jpql = "SELECT bt FROM Blog bt " + filterResult.getQuery() +
                " ORDER BY bt." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Blog> foundedBlogs = query.getResultList();

        // Convert to ViewPostDto
        List<ViewBlogDto> blogViewModels = foundedBlogs.stream()
                .map(blog -> BlogMapper.toView(blog))
                .collect(Collectors.toList());

        System.out.println(Arrays.toString(blogViewModels.toArray()));

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                blogViewModels
        );
    }

    private Long getTotalCountWithFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, String userId) {
        FilterResult filterResult = getFilters(commonQueryParamsDto, skipBannedComments, userId);
        String jpql = "SELECT count(bt) FROM Blog bt " + filterResult.getQuery();

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        filterResult.getParameters().forEach(query::setParameter);

        return query.getSingleResult();
    }

    private FilterResult getFilters(CommonQueryParamsDto queryObj, boolean skipBannedComments, String userId) {
        StringBuilder filters = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        boolean hasPreviousFilter = false;

        if (skipBannedComments) {
            filters.append("bt.isBanned = false");
            hasPreviousFilter = true;
        }

        if (userId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("bt.user.id = :userId");
            params.put("userId", UUID.fromString(userId));
            hasPreviousFilter = true;
        }

        if (!queryObj.getSearchNameTerm().isBlank()) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("UPPER(bt.name) LIKE :searchNameTerm");
            params.put("searchNameTerm", "%" + queryObj.getSearchNameTerm().toUpperCase() + "%");
        }


        String finalQuery = filters.isEmpty() ? "" : "WHERE " + filters.toString();
        return new FilterResult(finalQuery, params);
    }
}
