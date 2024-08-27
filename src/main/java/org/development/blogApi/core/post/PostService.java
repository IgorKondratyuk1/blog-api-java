package org.development.blogApi.core.post;

import jakarta.transaction.Transactional;
import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.like.LikeService;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.core.post.utils.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postsRepository;
    private final BlogRepository blogsRepository;
    private final LikeService likeService;

    @Autowired
    public PostService(
            PostRepository postsRepository,
            BlogRepository blogsRepository,
            LikeService likeService) {
        this.postsRepository = postsRepository;
        this.blogsRepository = blogsRepository;
        this.likeService = likeService;
    }

    public Post findById(UUID postId) {
        return postsRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public Post create(UUID userId, UUID blogId, CreatePostOfBlogDto createPostOfBlogDto) {
        Blog blog = blogsRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));

        if (!blog.getUserId().equals(userId)) {
            throw new RuntimeException("Blog doesn't belong to the current user");
        }

        Post post = Post.createInstance(userId, blog, createPostOfBlogDto.getShortDescription(), createPostOfBlogDto.getShortDescription(), createPostOfBlogDto.getTitle());
        return postsRepository.save(post);
    }

    @Transactional
    public ViewPostDto createByAdmin(UUID blogId, CreatePostOfBlogDto createPostOfBlogDto) {
        Blog blog = blogsRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));
        Post post = Post.createInstance(null, blog, createPostOfBlogDto.getShortDescription(), createPostOfBlogDto.getContent(), createPostOfBlogDto.getTitle());
        Post createdPost = postsRepository.save(post);
        return PostMapper.toView(createdPost);
    }

    @Transactional
    public void updateLikeStatus(UUID id, String userId, String userLogin, LikeStatus status) {
        Post post = postsRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
        this.likeService.like(userId, userLogin, LikeLocation.POST, post.getId().toString(), status);
    }

    @Transactional
    public void updateWithBlogId(String userId, String postId, String blogId, UpdatePostOfBlogDto updatePostDto) {
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new RuntimeException("Can not update with wrong blog id");
        }

        if (!post.getUserId().toString().equals(userId)) {
            throw new RuntimeException("Cannot update post with wrong user id");
        }

        post.updatePost(updatePostDto);
        postsRepository.save(post);
    }

    @Transactional
    public void updateWithBlogIdByAdmin(String postId, String blogId, UpdatePostOfBlogDto updatePostDto) {
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new RuntimeException("Wrong blog id");
        }

        post.updatePost(updatePostDto);
        postsRepository.save(post);
    }

    @Transactional
    public void removeWithBlogId(String userId, String postId, String blogId) {
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new RuntimeException("Wrong blog id");
        }

        if (!post.getUserId().toString().equals(userId)) {
            throw new RuntimeException("Can not remove a post that is not owned");
        }

        postsRepository.deleteById(UUID.fromString(postId));
    }

    @Transactional
    public void removeWithBlogIdByAdmin(String postId, String blogId) {
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new RuntimeException("Wrong blog id");
        }

        postsRepository.deleteById(UUID.fromString(postId));
    }


//    @Transactional
//    public boolean setBanStatusByUserId(String userId, boolean isBanned) {
//        return postsRepository.setBanStatusByUserId(userId, isBanned);
//    }
//
//    @Transactional
//    public boolean setBanStatusByBlogId(String blogId, boolean isBanned) {
//        return postsRepository.setBanStatusByBlogId(blogId, isBanned);
//    }
}
