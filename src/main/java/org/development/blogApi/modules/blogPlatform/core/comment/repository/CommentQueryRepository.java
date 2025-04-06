package org.development.blogApi.modules.blogPlatform.core.comment.repository;

import org.development.blogApi.modules.blogPlatform.core.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentQueryRepository extends JpaRepository<Comment, UUID>, CommentQueryRepositoryCustom {
}
