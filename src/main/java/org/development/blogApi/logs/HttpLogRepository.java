package org.development.blogApi.logs;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HttpLogRepository extends JpaRepository<HttpLog, Integer> {
}
