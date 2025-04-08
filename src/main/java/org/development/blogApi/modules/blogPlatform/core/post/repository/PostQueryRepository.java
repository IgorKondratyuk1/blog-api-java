package org.development.blogApi.modules.blogPlatform.core.post.repository;

import org.development.blogApi.modules.blogPlatform.core.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostQueryRepository extends JpaRepository<Post, UUID>, PostQueryRepositoryCustom {
}
