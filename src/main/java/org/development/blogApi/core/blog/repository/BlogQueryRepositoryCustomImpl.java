package org.development.blogApi.core.blog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.FilterResult;
import org.development.blogApi.common.utils.PaginationHelper;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.dto.response.ViewExtendedBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.utils.BlogMapper;

import java.util.*;
import java.util.stream.Collectors;





public class BlogQueryRepositoryCustomImpl implements BlogQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public BlogQueryRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<ViewBlogDto> findOneBlog(String id) {
        String jpql = "SELECT bt FROM Blog bt WHERE bt.id = :blogId";
        // AND bt.isBanned = FALSE";

        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        query.setParameter("blogId", id);
        Blog blog = query.getSingleResult();

        if (blog == null) {
            return Optional.empty();
        }

        return Optional.of(BlogMapper.toView(blog));
    }

    @Override
    public PaginationDto<ViewBlogDto> findAllBlogs(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedBlogs) {
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        FilterResult filterResult = getFilters(commonQueryParamsDto, skipBannedBlogs, null);


        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, skipBannedBlogs, null);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

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
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        FilterResult filterResult = getFilters(commonQueryParamsDto, false, null);

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, false, null);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

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
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();

        FilterResult filterResult = getFilters(commonQueryParamsDto, false, UUID.fromString(userId));

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, false, UUID.fromString(userId));
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

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

    private Long getTotalCountWithFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId) {
        FilterResult filterResult = getFilters(commonQueryParamsDto, skipBannedComments, userId);
        String jpql = "SELECT count(bt) FROM Blog bt " + filterResult.getQuery();

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        filterResult.getParameters().forEach(query::setParameter);

        return query.getSingleResult();
    }

//    private String getFilters(CommonQueryParamsDto queryObj, boolean skipBannedComments, UUID userId) {
//        StringBuilder filters = new StringBuilder("WHERE ");
//        boolean hasPreviousFilter = false;
//
//        if (skipBannedComments) {
//            filters.append("bt.isBanned = false");
//            hasPreviousFilter = true;
//        }
//
//        if (userId != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("bt.user.id = ").append(userId);
//            hasPreviousFilter = true;
//        }
//
//        if (!queryObj.getSearchNameTerm().isBlank()) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("bt.name LIKE '%").append(queryObj.getSearchNameTerm()).append("%'");
//            hasPreviousFilter = true;
//        }
//
//        return filters.append(";").toString();
//    }

    private FilterResult getFilters(CommonQueryParamsDto queryObj, boolean skipBannedComments, UUID userId) {
        StringBuilder filters = new StringBuilder("WHERE ");
        Map<String, Object> params = new HashMap<>();
        boolean hasPreviousFilter = false;

        if (skipBannedComments) {
            filters.append("bt.isBanned = false");
            hasPreviousFilter = true;
        }

        if (userId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("bt.user.id = :userId");
            params.put("userId", userId);
            hasPreviousFilter = true;
        }

        if (!queryObj.getSearchNameTerm().isBlank()) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("bt.name LIKE :searchNameTerm");
            params.put("searchNameTerm", "%" + queryObj.getSearchNameTerm() + "%");
        }

        return new FilterResult(filters.toString(), params);
    }
}
