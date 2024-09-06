package org.development.blogApi.core.blog;

import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BlogService {

    private final BlogRepository blogsRepository;

    private final UserRepository userRepository;

    @Autowired
    public BlogService(BlogRepository blogsRepository, UserRepository userRepository) {
        this.blogsRepository = blogsRepository;
        this.userRepository = userRepository;
    }

    public Optional<Blog> findById(UUID id) {
        return this.blogsRepository.findById(id);
    }

    public Blog create(UUID userId, CreateBlogDto createBlogDto) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Blog blog = Blog.createInstance(user, createBlogDto);
        return this.blogsRepository.save(blog);
    }

    public void update(UUID userId, UUID blogId, UpdateBlogDto updateBlogDto) {
        Blog blog = this.blogsRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));

        if (!blog.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cannot update a blog that does not belong to the user");
        }

        blog.updateBlog(updateBlogDto);
        this.blogsRepository.save(blog);
    }

    public void remove(UUID userId, UUID blogId) {
        Blog blog = this.blogsRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));

        if (!blog.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cannot delete a blog that does not belong to the user");
        }

        this.blogsRepository.deleteById(blogId);
    }

    public void removeByAdmin(UUID blogId) {
        Blog blog = this.blogsRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));
        this.blogsRepository.deleteById(blogId);
    }

    public void bindBlogWithUser(UUID userId, UUID blogId) {
        Blog blog = blogsRepository.findById(blogId).orElseThrow(() -> new RuntimeException("Wrong blogId"));

        if (blog.getUser().getId() != null) {
            throw new RuntimeException("blog is already bounded");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        blog.setUser(user);
        blogsRepository.save(blog);
    }

//    public void setBlogBanStatus(String blogId, BanBlogDto banBlogDto) {
//        Optional<Blog> optionalBlog = blogsRepository.findById(blogId);
//
//        if (!optionalBlog.isPresent()) {
//            return new CustomErrorDto(HttpStatus.NOT_FOUND.value(), "Blog not found");
//        }
//
//        Blog blog = optionalBlog.get();
//        blog.setIsBanned(banBlogDto.getIsBanned());
//        blogsRepository.save(blog);
//        return true;
//    }
}

