package org.development.blogApi.blog.utils;

import org.development.blogApi.blog.dto.response.ViewBlogDto;
import org.development.blogApi.blog.dto.response.BlogOwnerInfoDto;
import org.development.blogApi.blog.dto.response.ViewExtendedBlogDto;
import org.development.blogApi.blog.entity.Blog;

public class BlogMapper {
    public static ViewBlogDto toView(Blog blog) {
        if (blog == null) {
            return null;
        }

        return new ViewBlogDto(
                blog.getId().toString(),
                blog.getName(),
                blog.getDescription(),
                blog.getWebsiteUrl(),
                blog.getCreatedAt(),
                blog.isMembership()
        );
    }

    public static ViewExtendedBlogDto toExtendedView(Blog blog) {
        if (blog == null) {
            return null;
        }

        BlogOwnerInfoDto blogOwnerInfoDto = new BlogOwnerInfoDto(blog.getUser().getId().toString(), blog.getUser().getLogin());
        return new ViewExtendedBlogDto(
                blog.getId().toString(),
                blog.getName(),
                blog.getDescription(),
                blog.getWebsiteUrl(),
                blog.getCreatedAt(),
                blog.isMembership(),
                blogOwnerInfoDto
        );
    }
}
