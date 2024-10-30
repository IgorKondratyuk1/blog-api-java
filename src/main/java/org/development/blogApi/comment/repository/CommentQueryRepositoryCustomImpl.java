package org.development.blogApi.comment.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import oracle.jdbc.proxy.annotation.Pre;
import org.development.blogApi.blog.repository.BlogRepository;
import org.development.blogApi.comment.dto.LikesDislikesCountDto;
import org.development.blogApi.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.comment.entity.Comment;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.infrastructure.common.dto.FilterResult;
import org.development.blogApi.infrastructure.common.utils.PaginationUtil;
import org.development.blogApi.blog.entity.Blog;
import org.development.blogApi.comment.utils.CommentMapper;
import org.development.blogApi.like.repository.LikeRepository;
import org.development.blogApi.like.enums.LikeLocation;
import org.development.blogApi.like.enums.LikeStatus;
import org.development.blogApi.post.repository.PostRepository;
import org.development.blogApi.post.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);

        Root<Comment> commentRoot = criteriaQuery.from(Comment.class);
        Predicate idPredicate = criteriaBuilder.equal(commentRoot.get("id"), commentId);
        criteriaQuery.where(idPredicate);
        TypedQuery<Comment> query = entityManager.createQuery(criteriaQuery);

        Comment comment;
        try {
            comment = query.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        LikeStatus likeStatus = likeRepository.getUserLikeStatus(currentUserId, commentId, LikeLocation.COMMENT);
        LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(commentId, LikeLocation.COMMENT);
        return Optional.of(CommentMapper.toPublicViewFromDomain(comment, likeStatus, likesDislikesCount.getLikesCount(), likesDislikesCount.getDislikesCount()));
    }

    @Override
    public PaginationDto<ViewPublicCommentDto> findCommentsOfPost(UUID postId, CommonQueryParamsDto commonQueryParamsDto, UUID currentUserId) {
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, null, null, postId);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);

        Root<Comment> commentRoot = criteriaQuery.from(Comment.class);
        Predicate filterPredicate = getFilters(commentRoot, commonQueryParamsDto, true, null, null, postId);
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get(commonQueryParamsDto.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(commentRoot.get(commonQueryParamsDto.getSortBy())));
        }

        TypedQuery<Comment> query = entityManager.createQuery(criteriaQuery);
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
        List<Blog> userBlogs = blogRepository.findByUserId(userId);
        List<UUID> blogIds = userBlogs.stream().map(Blog::getId).collect(Collectors.toList());

        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, userId, blogIds, null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);

        Root<Comment> commentRoot = criteriaQuery.from(Comment.class);
        Predicate filterPredicate = getFilters(commentRoot, commonQueryParamsDto, true, null, blogIds, null);
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get(commonQueryParamsDto.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(commentRoot.get(commonQueryParamsDto.getSortBy())));
        }

        TypedQuery<Comment> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Comment> foundedComments = query.getResultList();
        List<ViewBloggerCommentDto> commentsViewModels = foundedComments.stream().map(comment -> {
            LikeStatus likeStatus = likeRepository.getUserLikeStatus(userId, comment.getId(), LikeLocation.COMMENT);
            LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(comment.getId(), LikeLocation.COMMENT);
            Optional<Post> post = postRepository.findById(comment.getPost().getId());
            return CommentMapper.toBloggerView(comment, post.orElse(null), likeStatus, likesDislikesCount.getLikesCount(), likesDislikesCount.getDislikesCount());
        }).collect(Collectors.toList());

        return new PaginationDto<>(pagesCount, commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize(), totalCount, commentsViewModels);
    }

    private Long getTotalCountWithFilters(CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, List<UUID> blogIds, UUID postId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<Comment> commentRoot = criteriaQuery.from(Comment.class);
        Predicate filterPredicate = getFilters(commentRoot, commonQueryParamsDto, skipBannedComments, userId, blogIds, postId);

        criteriaQuery.where(filterPredicate);
        criteriaQuery.select(criteriaBuilder.count(commentRoot));
        
        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate getFilters(Root<Comment> commentRoot, CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, List<UUID> blogIds, UUID postId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> andPredicates = new ArrayList<>();

        // Filter for banned comments
//        if (skipBannedComments) {
//            filters.append("ct.isBanned = false");
//        }

        // Filter by userId
        if (userId != null) {
            andPredicates.add(criteriaBuilder.equal(commentRoot.get("user").get("id"), userId));
        }

        // Filter by blogIds (using IN clause)
        if (blogIds != null && !blogIds.isEmpty()) {
            andPredicates.add(commentRoot.get("post").get("blog").get("id").in(blogIds));
        }

        // Filter by search content term
        if (commonQueryParamsDto.getSearchNameTerm() != null && !commonQueryParamsDto.getSearchNameTerm().isBlank()) {
            andPredicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(commentRoot.get("content")),
                    "%" + commonQueryParamsDto.getSearchNameTerm().toUpperCase() + "%"));
        }

        // Filter by postId
        if (postId != null) {
            andPredicates.add(commentRoot.get("post").get("id").in(postId));
        }

        Predicate resultPredicate = andPredicates.isEmpty() ? criteriaBuilder.conjunction()
                                                            : criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
        return resultPredicate;
    }
}