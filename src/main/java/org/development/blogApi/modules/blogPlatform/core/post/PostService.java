package org.development.blogApi.modules.blogPlatform.core.post;

import org.development.blogApi.modules.blogPlatform.core.blog.repository.BlogRepository;
import org.development.blogApi.modules.blogPlatform.core.blog.entity.Blog;
import org.development.blogApi.modules.blogPlatform.core.post.entity.Post;
import org.development.blogApi.modules.blogPlatform.core.post.exceptions.PostNotFoundException;
import org.development.blogApi.modules.blogPlatform.core.post.utils.PostMapper;
import org.development.blogApi.modules.blogPlatform.core.like.LikeService;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeLocation;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.response.ViewPostDto;
import org.development.blogApi.modules.blogPlatform.core.post.repository.PostRepository;
import org.development.blogApi.modules.blogPlatform.core.blog.exceptions.BlogNotFoundException;
import org.development.blogApi.modules.blogPlatform.core.post.exceptions.IncorrectPostDataException;
import org.development.blogApi.modules.user.exceptions.UserNotFoundException;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.development.blogApi.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postsRepository;
    private final BlogRepository blogsRepository;
    private final LikeService likeService;
    private final UserRepository userRepository;

    @Autowired
    public PostService(
            PostRepository postsRepository,
            BlogRepository blogsRepository,
            LikeService likeService,
            UserRepository userRepository) {
        this.postsRepository = postsRepository;
        this.blogsRepository = blogsRepository;
        this.likeService = likeService;
        this.userRepository = userRepository;
    }

    public Post findById(UUID postId) {
        return postsRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
    }

    public Post create(UUID userId, UUID blogId, CreatePostOfBlogDto createPostOfBlogDto) {
        Blog blog = blogsRepository.findById(blogId).orElseThrow(() -> new BlogNotFoundException());
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());

        if (!blog.getUser().getId().equals(userId)) {
            throw new IncorrectPostDataException("Blog doesn't belong to the current user");
        }

        Post post = Post.createInstance(user, blog, createPostOfBlogDto.getShortDescription(), createPostOfBlogDto.getContent(), createPostOfBlogDto.getTitle());
        return postsRepository.save(post);
    }

    public ViewPostDto createByAdmin(UUID blogId, CreatePostOfBlogDto createPostOfBlogDto) {
        Blog blog = blogsRepository.findById(blogId).orElseThrow(() -> new BlogNotFoundException());
        Post post = Post.createInstance(null, blog, createPostOfBlogDto.getShortDescription(), createPostOfBlogDto.getContent(), createPostOfBlogDto.getTitle());
        Post createdPost = postsRepository.save(post);
        return PostMapper.toView(createdPost);
    }

    public void updateLikeStatus(UUID id, String userId, String userLogin, LikeStatus status) {
        Post post = postsRepository.findById(id).orElseThrow(() -> new BlogNotFoundException());
        this.likeService.like(userId, userLogin, LikeLocation.POST, post.getId().toString(), status);
    }

    public void updateWithBlogId(String userId, String postId, String blogId, UpdatePostOfBlogDto updatePostDto) {
        Blog blog = blogsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new BlogNotFoundException());
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new PostNotFoundException());

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new IncorrectPostDataException("Can not update with wrong blog id");
        }

        if (!post.getUser().getId().toString().equals(userId)) {
            throw new IncorrectPostDataException("Can not update post with wrong user id");
        }

        post.updatePost(updatePostDto);
        postsRepository.save(post);
    }

    public void updateWithBlogIdByAdmin(String postId, String blogId, UpdatePostOfBlogDto updatePostDto) {
        blogsRepository.findById(UUID.fromString(blogId)).orElseThrow(() -> new BlogNotFoundException());
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new PostNotFoundException());

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new IncorrectPostDataException("Wrong blog id");
        }

        post.updatePost(updatePostDto);
        postsRepository.save(post);
    }

    public void removeWithBlogId(String userId, String postId, String blogId) {
        blogsRepository.findById(UUID.fromString(blogId)).orElseThrow(() -> new BlogNotFoundException());
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new PostNotFoundException());

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new IncorrectPostDataException("Post no belong to blog with id" + blogId);
        }

        if (!post.getUser().getId().toString().equals(userId)) {
            throw new IncorrectPostDataException("Can not remove post of other user");
        }

        postsRepository.deleteById(UUID.fromString(postId));
    }

    public void removeWithBlogIdByAdmin(String postId, String blogId) {
        blogsRepository.findById(UUID.fromString(blogId)).orElseThrow(() -> new BlogNotFoundException());
        Post post = postsRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new PostNotFoundException());

        if (!post.getBlog().getId().toString().equals(blogId)) {
            throw new IncorrectPostDataException("Post no belong to blog with id" + blogId);
        }

        postsRepository.deleteById(UUID.fromString(postId));
    }


//    public boolean setBanStatusByUserId(String userId, boolean isBanned) {
//        return postsRepository.setBanStatusByUserId(userId, isBanned);
//    }
//
//    public boolean setBanStatusByBlogId(String blogId, boolean isBanned) {
//        return postsRepository.setBanStatusByBlogId(blogId, isBanned);
//    }
}
