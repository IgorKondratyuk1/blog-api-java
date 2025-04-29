package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.PaginationUtil;
import org.development.blogApi.common.utils.SortUtil;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.GamePairQueryParams;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewGamePairDto;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.utils.GamePairMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class QuizGamePairQueryRepositoryCustomImpl implements QuizGamePairQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PaginationDto<ViewGamePairDto> findAllGamePairs(GamePairQueryParams queryParamsDto, String userId) {
        int skipValue = PaginationUtil.getSkipValue(queryParamsDto.getPageNumber(), queryParamsDto.getPageSize());
        String sortValue = queryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(userId);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, queryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GamePairEntity> criteriaQuery = criteriaBuilder.createQuery(GamePairEntity.class);

        Root<GamePairEntity> gamePairEntityRoot = criteriaQuery.from(GamePairEntity.class);
        Predicate filterPredicate = getFilters(gamePairEntityRoot, userId);
        criteriaQuery.where(filterPredicate);

        String DEFAULT_ORDER_FIELD = "pairCreatedDate";

        String sortBy = queryParamsDto.getSortBy();
        Path<?> sortPath = SortUtil.getNestedPath(gamePairEntityRoot, sortBy);
        Path<?> defaultPath = SortUtil.getNestedPath(gamePairEntityRoot, DEFAULT_ORDER_FIELD);

        Order fieldOrder = sortValue.equalsIgnoreCase("ASC")
                ? criteriaBuilder.asc(sortPath)
                : criteriaBuilder.desc(sortPath);

        Order defaultOrder = criteriaBuilder.desc(defaultPath);

        if (DEFAULT_ORDER_FIELD.equals(sortBy)) {
            criteriaQuery.orderBy(fieldOrder);
        } else {
            criteriaQuery.orderBy(fieldOrder, defaultOrder);
        }

        TypedQuery<GamePairEntity> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(queryParamsDto.getPageSize());

        List<GamePairEntity> foundedGamePairEntities = query.getResultList();
        List<ViewGamePairDto> gamePairViewModels = foundedGamePairEntities.stream()
                .map(gamePair -> GamePairMapper.toView(gamePair))
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                queryParamsDto.getPageNumber(),
                queryParamsDto.getPageSize(),
                totalCount,
                gamePairViewModels
        );
    }

    private Long getTotalCountWithFilters(String userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<GamePairEntity> gamePairEntityRoot = criteriaQuery.from(GamePairEntity.class);
        Predicate filterPredicate = getFilters(gamePairEntityRoot, userId);

        criteriaQuery.where(filterPredicate);
        criteriaQuery.select(criteriaBuilder.count(gamePairEntityRoot));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate getFilters(Root<GamePairEntity> gamePairEntityRoot, String userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        // firstPlayerProgress and its player must not be null
        predicates.add(
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(gamePairEntityRoot.get("firstPlayerProgress")),
                        criteriaBuilder.isNotNull(gamePairEntityRoot.get("firstPlayerProgress").get("player")),
                        criteriaBuilder.equal(
                                gamePairEntityRoot.get("firstPlayerProgress").get("player").get("id"),
                                UUID.fromString(userId)
                        )
                )
        );

        // secondPlayerProgress and its player must not be null
        predicates.add(
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(gamePairEntityRoot.get("secondPlayerProgress")),
                        criteriaBuilder.isNotNull(gamePairEntityRoot.get("secondPlayerProgress").get("player")),
                        criteriaBuilder.equal(
                                gamePairEntityRoot.get("secondPlayerProgress").get("player").get("id"),
                                UUID.fromString(userId)
                        )
                )
        );

        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }
}
