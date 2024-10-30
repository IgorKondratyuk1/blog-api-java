package org.development.blogApi.post.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.development.blogApi.comment.entity.Comment;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.infrastructure.common.dto.FilterResult;
import org.development.blogApi.infrastructure.common.utils.PaginationUtil;
import org.development.blogApi.infrastructure.common.utils.SortUtil;
import org.development.blogApi.comment.dto.LikesDislikesCountDto;
import org.development.blogApi.like.enums.LikeStatus;
import org.development.blogApi.like.repository.LikeRepository;
import org.development.blogApi.like.entity.Like;
import org.development.blogApi.like.enums.LikeLocation;
import org.development.blogApi.post.dto.response.ViewPostDto;
import org.development.blogApi.post.entity.Post;
import org.development.blogApi.post.utils.PostMapper;
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
        UUID userUuid = currentUserId == null ? null : UUID.fromString(currentUserId); // TODO refactor

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

        Root<Post> commentRoot = criteriaQuery.from(Post.class);
        Predicate idPredicate = criteriaBuilder.equal(commentRoot.get("id"), UUID.fromString(postId));
        criteriaQuery.where(idPredicate);
        TypedQuery<Post> query = entityManager.createQuery(criteriaQuery);

        Post post;
        try {
            post = query.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }

        List<Like> lastLikes = likeRepository.getLastLikesInfo(postId, LikeLocation.POST, 3);
        LikesDislikesCountDto likesDislikesCount = likeRepository.getLikesAndDislikesCount(UUID.fromString(postId), LikeLocation.POST);
        LikeStatus likeStatus = likeRepository.getUserLikeStatus(userUuid, UUID.fromString(postId), LikeLocation.POST);

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
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortBy = SortUtil.getSortBy(commonQueryParamsDto.getSortBy());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, null, null, null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

        Root<Post> postRoot = criteriaQuery.from(Post.class);
        Predicate filterPredicate = getFilters(postRoot, commonQueryParamsDto, true, null, null, null); // TODO do not put NULL values
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(postRoot.get(sortBy)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get(sortBy)));
        }

        TypedQuery<Post> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Post> foundedPosts = query.getResultList();
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
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortBy = SortUtil.getSortBy(commonQueryParamsDto.getSortBy());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, null, UUID.fromString(blogId), null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

        Root<Post> postRoot = criteriaQuery.from(Post.class);
        Predicate filterPredicate = getFilters(postRoot, commonQueryParamsDto, true, null, UUID.fromString(blogId), null); // TODO do not put NULL values
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(postRoot.get(sortBy)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get(sortBy)));
        }

        TypedQuery<Post> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Post> foundedPosts = query.getResultList();
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
        int skipValue = PaginationUtil.getSkipValue(commonQueryParamsDto.getPageNumber(), commonQueryParamsDto.getPageSize());
        String sortBy = SortUtil.getSortBy(commonQueryParamsDto.getSortBy());
        String sortValue = commonQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(commonQueryParamsDto, true, UUID.fromString(userId), UUID.fromString(blogId), null);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, commonQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);

        Root<Post> postRoot = criteriaQuery.from(Post.class);
        Predicate filterPredicate = getFilters(postRoot, commonQueryParamsDto, true, UUID.fromString(userId), UUID.fromString(blogId), null); // TODO do not put NULL values
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(postRoot.get(sortBy)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(postRoot.get(sortBy)));
        }

        TypedQuery<Post> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(commonQueryParamsDto.getPageSize());

        List<Post> foundedPosts = query.getResultList();
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<Post> postRoot = criteriaQuery.from(Post.class);
        Predicate filterPredicate = getFilters(postRoot, commonQueryParamsDto, skipBannedComments, userId, blogId, postId);
        criteriaQuery.where(filterPredicate);
        criteriaQuery.select(criteriaBuilder.count(postRoot));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate getFilters(Root<Post> postRoot, CommonQueryParamsDto commonQueryParamsDto, boolean skipBannedComments, UUID userId, UUID blogId, UUID postId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> andPredicates = new ArrayList<>();

        // Filter for banned comments
//        if (skipBannedComments) {
//            filters.append("pt.isBanned = false");
//            hasPreviousFilter = true;
//        }

        // Filter by userId
        if (userId != null) {
            andPredicates.add(criteriaBuilder.equal(postRoot.get("user").get("id"), userId));
        }

        // Filter by blogId
        if (blogId != null) {
            andPredicates.add(criteriaBuilder.equal(postRoot.get("blog").get("id"), blogId));
        }

        // Filter by search name term
        if (commonQueryParamsDto.getSearchNameTerm() != null && !commonQueryParamsDto.getSearchNameTerm().isBlank()) {
            andPredicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(postRoot.get("name")),
                    "%" + commonQueryParamsDto.getSearchNameTerm().toUpperCase() + "%"));
        }

        // Filter by postId
        if (postId != null) {
            andPredicates.add(postRoot.get("id").in(postId));
        }

        Predicate resultPredicate = andPredicates.isEmpty() ? criteriaBuilder.conjunction()
                                                            : criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
        return resultPredicate;
    }
}