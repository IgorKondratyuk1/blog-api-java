package org.development.blogApi.blogPlatform.core.comment.repository;

import org.development.blogApi.blogPlatform.core.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
