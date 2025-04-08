package org.development.blogApi.modules.user.repository;

import org.development.blogApi.modules.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserQueryRepository extends JpaRepository<UserEntity, UUID>, UserQueryRepositoryCustom {
}
