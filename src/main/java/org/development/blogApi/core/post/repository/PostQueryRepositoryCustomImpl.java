package org.development.blogApi.core.post.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.FilterResult;
import org.development.blogApi.common.utils.PaginationHelper;
import org.development.blogApi.core.comment.dto.LikesDislikesCountDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.like.repository.LikeRepository;
import org.development.blogApi.core.like.entity.Like;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.core.post.utils.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PostQueryRepositoryCustomImpl implements PostQueryRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    private LikeRepository likeRepository;

    @Autowired
    public PostQueryRepositoryCustomImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public Optional<ViewPostDto> findOnePost(String postId, String currentUserId) {
        String jpql = "SELECT pt FROM Post pt " +
                "LEFT JOIN pt.blog bt " +
                "WHERE pt.id = :postId";
//                " AND pt.isBanned = FALSE";


        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class);
        query.setParameter("postId", UUID.fromString(postId));
        Post post = query.getSingleResult();

        if (post == null) {
            return Optional.empty();
        }

        List<Like> lastLikes = likeRepository.getLastLikesInfo(postId, LikeLocation.POST, 3);
        LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(UUID.fromString(postId), LikeLocation.POST);
        LikeStatus likeStatus = likeRepository.getUserLikeStatus(UUID.fromString(currentUserId), UUID.fromString(postId), LikeLocation.POST);

        return Optional.of(PostMapper.toView(
                post,
                likeStatus,
                likesDislikesCount.getLikesCount(),
                likesDislikesCount.getDislikesCount(),
                lastLikes
        ));
    }

    @Override
    public PaginationDto<ViewPostDto> findAllPosts(CommonQueryParamsDto commonQueryParamsDto, String currentUserId) {
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        FilterResult filterResult = getFilters(commonQueryParamsDto, true, null, null, null);


        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, null, null, null);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        // Fetch posts with filters and pagination
        String jpql = "SELECT pt " +
                "FROM Post pt " +
                "LEFT JOIN pt.user u " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery() +
                " ORDER BY pt." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Post> foundedPosts = query.getResultList();

        // Convert to ViewPostDto
        List<ViewPostDto> postViewModels = foundedPosts.stream()
                .map(post -> findOnePost(post.getId().toString(), currentUserId).get())
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                postViewModels
        );
    }

    @Override
    public PaginationDto<ViewPostDto> findPostsOfBlog(String blogId, CommonQueryParamsDto commonQueryParamsDto, String currentUserId) {
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        FilterResult filterResult = getFilters(commonQueryParamsDto, true, null, UUID.fromString(blogId), null);

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, null, UUID.fromString(blogId), null);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        // Fetch posts with filters and pagination
        String jpql = "SELECT pt " +
                "FROM Post pt " +
                "LEFT JOIN pt.user u " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery() +
                " ORDER BY pt." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Post> foundedPosts = query.getResultList();

        // Convert to ViewPostDto
        List<ViewPostDto> postViewModels = foundedPosts.stream()
                .map(post -> findOnePost(post.getId().toString(), currentUserId).get())
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                postViewModels
        );
    }

    @Override
    public PaginationDto<ViewPostDto> findPostsOfBlogByUserId(String blogId, CommonQueryParamsDto commonQueryParamsDto, String userId) {
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        FilterResult filterResult = getFilters(commonQueryParamsDto, true, UUID.fromString(userId), UUID.fromString(blogId), null);


        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, UUID.fromString(userId), UUID.fromString(blogId), null);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        // Fetch posts with filters and pagination
        String jpql = "SELECT pt " +
                "FROM Post pt " +
                "LEFT JOIN pt.user u " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery() +
                " ORDER BY pt." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Post> foundedPosts = query.getResultList();

        // Convert to ViewPostDto
        List<ViewPostDto> postViewModels = foundedPosts.stream()
                .map(post -> findOnePost(post.getId().toString(), userId).get())
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                postViewModels
        );
    }

    private Long getTotalCountWithFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, UUID blogId, UUID postId) {
        FilterResult filterResult = getFilters(commonQueryParamsDto, skipBannedComments, userId, blogId, postId);
        String jpql = "SELECT count(pt) FROM Post pt " +
                "LEFT JOIN pt.user u " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery();

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        filterResult.getParameters().forEach(query::setParameter);

        return query.getSingleResult();
    }

//    private String getFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, UUID blogId, UUID postId) {
//        StringBuilder filters = new StringBuilder("WHERE ");
//        boolean hasPreviousFilter = false;
//
//        if (skipBannedComments) {
//            filters.append("pt.isBanned = false");
//            hasPreviousFilter = true;
//        }
//
//        if (userId != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("pt.user.id = ").append(userId);
//            hasPreviousFilter = true;
//        }
//
//        if (blogId != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("bt.id = " + blogId);
//            hasPreviousFilter = true;
//        }
//
//        // TODO no name in post
//        if (commonQueryParamsDto.getSearchNameTerm() != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("pt.name LIKE '%").append(commonQueryParamsDto.getSearchNameTerm()).append("%'");
//            hasPreviousFilter = true;
//        }
//
//        if (postId != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("pt.id = ").append(postId);
//        }
//
//        return filters.toString();
//    }

    private FilterResult getFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, UUID blogId, UUID postId) {
        StringBuilder filters = new StringBuilder("WHERE ");
        Map<String, Object> params = new HashMap<>();
        boolean hasPreviousFilter = false;

        // Filter for banned comments
        if (skipBannedComments) {
            filters.append("pt.isBanned = false");
            hasPreviousFilter = true;
        }

        // Filter by userId
        if (userId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("pt.user.id = :userId");
            params.put("userId", userId);
            hasPreviousFilter = true;
        }

        // Filter by blogId
        if (blogId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("bt.id = :blogId");
            params.put("blogId", blogId);
            hasPreviousFilter = true;
        }

        // Filter by search name term
        if (commonQueryParamsDto.getSearchNameTerm() != null && !commonQueryParamsDto.getSearchNameTerm().isBlank()) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("pt.name LIKE :searchNameTerm");
            params.put("searchNameTerm", "%" + commonQueryParamsDto.getSearchNameTerm() + "%");
            hasPreviousFilter = true;
        }

        // Filter by postId
        if (postId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("pt.id = :postId");
            params.put("postId", postId);
        }

        return new FilterResult(filters.toString(), params);
    }

}
