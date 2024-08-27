package org.development.blogApi.core.comment.repository;

import org.development.blogApi.core.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentQueryRepository extends JpaRepository<Comment, UUID>, CommentQueryRepositoryCustom {

    Optional<Comment> findByIdAndUserId(UUID id, UUID userId);
}
