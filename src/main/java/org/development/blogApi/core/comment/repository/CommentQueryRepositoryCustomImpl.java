package org.development.blogApi.core.comment.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.FilterResult;
import org.development.blogApi.common.utils.PaginationHelper;
import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.comment.dto.LikesDislikesCountDto;
import org.development.blogApi.core.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.comment.utils.CommentMapper;
import org.development.blogApi.core.like.repository.LikeRepository;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.core.post.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

// +
@Repository
public class CommentQueryRepositoryCustomImpl implements CommentQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private LikeRepository likeRepository;
    private BlogRepository blogRepository;
    private PostRepository postRepository;

    @Autowired
    public CommentQueryRepositoryCustomImpl(LikeRepository likeRepository, BlogRepository blogRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.blogRepository = blogRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Optional<ViewPublicCommentDto> findCommentByIdAndUserId(UUID commentId, UUID currentUserId) {
        String jpql = "SELECT ct FROM Comment ct " +
                "LEFT JOIN ct.user u " +
                "LEFT JOIN ct.post pt " +
                "WHERE ct.id = :commentId ";
                // + "AND ct.isBanned = false";

        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class);
        query.setParameter("commentId", commentId);
        Comment comment;

        try {
            comment = query.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }

        LikeStatus likeStatus = likeRepository.getUserLikeStatus(currentUserId, commentId, LikeLocation.COMMENT);
        LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(commentId, LikeLocation.COMMENT);

        return Optional.of(CommentMapper.toPublicViewFromDomain(comment, likeStatus, likesDislikesCount.getLikesCount(), likesDislikesCount.getDislikesCount()));
    }

    @Override
    public PaginationDto<ViewPublicCommentDto> findCommentsOfPost(UUID postId, CommonQueryParamsDto commonQueryParamsDto, UUID currentUserId) {
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, null, null, postId);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        FilterResult filterResult = getFilters(commonQueryParamsDto, true, null, null, postId);
        String jpql = "SELECT ct " +
                "FROM Comment ct " +
                "LEFT JOIN ct.user u " +
                "LEFT JOIN ct.post pt " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery() +
                " ORDER BY ct." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Comment> foundedComments = query.getResultList();
        List<ViewPublicCommentDto> commentsViewModels = foundedComments.stream().map(comment -> {
            LikeStatus likeStatus = likeRepository.getUserLikeStatus(currentUserId, comment.getId(), LikeLocation.COMMENT);
            LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(comment.getId(), LikeLocation.COMMENT);
            return CommentMapper.toPublicViewFromDomain(comment, likeStatus, likesDislikesCount.getLikesCount(), likesDislikesCount.getDislikesCount());
        }).collect(Collectors.toList());

        return new PaginationDto<ViewPublicCommentDto>(
                pagesCount,
                commonQueryParamsDto.getPageNumber(),
                commonQueryParamsDto.getPageSize(),
                totalCount,
                commentsViewModels);
    }

    @Override
    public PaginationDto<ViewBloggerCommentDto> findCommentsOfUserBlogs(UUID userId, CommonQueryParamsDto commonQueryParamsDto) {
        int skipValue = PaginationHelper.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();

        List<Blog> userBlogs = blogRepository.findByUserId(userId);
        List<UUID> blogIds = userBlogs.stream().map(Blog::getId).collect(Collectors.toList());

        FilterResult filterResult = getFilters(commonQueryParamsDto, true, null, blogIds, null);
        String jpql = "SELECT ct FROM Comment ct " +
                "LEFT JOIN ct.user u " +
                "LEFT JOIN ct.post pt " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery() +
                " ORDER BY ct." + commonQueryParamsDto.getSortBy() + " " + sortValue;

        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class);
        filterResult.getParameters().forEach(query::setParameter);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Comment> foundedComments = query.getResultList();
        List<ViewBloggerCommentDto> commentsViewModels = foundedComments.stream().map(comment -> {
            LikeStatus likeStatus = likeRepository.getUserLikeStatus(userId, comment.getId(), LikeLocation.COMMENT);
            LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(comment.getId(), LikeLocation.COMMENT);
            Optional<Post> post = postRepository.findById(comment.getPost().getId());
            return CommentMapper.toBloggerView(comment, post.orElse(null), likeStatus, likesDislikesCount.getLikesCount(), likesDislikesCount.getDislikesCount());
        }).collect(Collectors.toList());

        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, userId, blogIds, null);
        int pagesCount = PaginationHelper.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        return new PaginationDto<>(pagesCount, commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize(), totalCount, commentsViewModels);
    }

    private Long getTotalCountWithFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, List<UUID> blogIds, UUID postId) {
        FilterResult filterResult = getFilters(commonQueryParamsDto, skipBannedComments, userId, blogIds, postId);

        String jpql = "SELECT count(ct) FROM Comment ct " +
                "LEFT JOIN ct.user u " +
                "LEFT JOIN ct.post pt " +
                "LEFT JOIN pt.blog bt " +
                filterResult.getQuery();

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        filterResult.getParameters().forEach(query::setParameter);
        return query.getSingleResult();
    }

//    private String getFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, List<UUID> blogIds, UUID postId) {
//        StringBuilder filters = new StringBuilder("WHERE ");
//        boolean hasPreviousFilter = false;
//
//        if (skipBannedComments) {
//            filters.append("ct.isBanned = false");
//            hasPreviousFilter = true;
//        }
//
//        if (userId != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("ct.user.id = ").append(userId);
//            hasPreviousFilter = true;
//        }
//
//        if (blogIds != null && !blogIds.isEmpty()) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("bt.id IN (").append(blogIds.stream().map(String::valueOf).collect(Collectors.joining(","))).append(")");
//            hasPreviousFilter = true;
//        }
//
//        if (commonQueryParamsDto.getSearchNameTerm() != null) {
//            if (hasPreviousFilter) filters.append(" AND ");
//            filters.append("ct.name LIKE '%").append(commonQueryParamsDto.getSearchNameTerm()).append("%'");
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

    private FilterResult getFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, List<UUID> blogIds, UUID postId) {
        StringBuilder filters = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        boolean hasPreviousFilter = false;

        // Filter for banned comments
        if (skipBannedComments) {
//            filters.append("ct.isBanned = false");
//            hasPreviousFilter = true;
        }

        // Filter by userId
        if (userId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("ct.user.id = :userId");
            params.put("userId", userId);
            hasPreviousFilter = true;
        }

        // Filter by blogIds (using IN clause)
        if (blogIds != null && !blogIds.isEmpty()) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("bt.id IN :blogIds");
            params.put("blogIds", blogIds);
            hasPreviousFilter = true;
        }

        // Filter by search content term
        if (commonQueryParamsDto.getSearchNameTerm() != null && !commonQueryParamsDto.getSearchNameTerm().isBlank()) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("UPPER(ct.content) LIKE :searchNameTerm");
            params.put("searchNameTerm", "%" + commonQueryParamsDto.getSearchNameTerm().toUpperCase() + "%");
            hasPreviousFilter = true;
        }

        // Filter by postId
        if (postId != null) {
            if (hasPreviousFilter) filters.append(" AND ");
            filters.append("pt.id = :postId");
            params.put("postId", postId);
        }

        String finalQuery = filters.isEmpty() ? "" : "WHERE " + filters.toString();
        return new FilterResult(finalQuery, params);
    }

}
