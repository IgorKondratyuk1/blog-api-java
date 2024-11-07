package org.development.blogApi.blogPlatform.core.post.repository;

import org.development.blogApi.blogPlatform.core.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
}
