package org.development.blogApi.blog.repository;

import org.development.blogApi.blog.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlogQueryRepository extends JpaRepository<Blog, UUID>, BlogQueryRepositoryCustom {
}
