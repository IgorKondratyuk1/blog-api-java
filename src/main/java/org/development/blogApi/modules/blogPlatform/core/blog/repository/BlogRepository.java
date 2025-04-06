package org.development.blogApi.modules.blogPlatform.core.blog.repository;

import org.development.blogApi.modules.blogPlatform.core.blog.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {
    List<Blog> findByUserId(UUID userId);
}
