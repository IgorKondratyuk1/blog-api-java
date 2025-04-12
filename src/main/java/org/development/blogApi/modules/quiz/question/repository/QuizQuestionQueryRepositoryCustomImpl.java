package org.development.blogApi.modules.quiz.question.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.PaginationUtil;
import org.development.blogApi.common.utils.SortUtil;
import org.development.blogApi.modules.quiz.question.dto.QuestionQueryParamsDto;
import org.development.blogApi.modules.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.development.blogApi.modules.quiz.question.utils.QuestionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class QuizQuestionQueryRepositoryCustomImpl implements QuizQuestionQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PaginationDto<ViewQuestionDto> findAllQuestions(QuestionQueryParamsDto questionQueryParamsDto) {
        int skipValue = PaginationUtil.getSkipValue(questionQueryParamsDto.getPageNumber(), questionQueryParamsDto.getPageSize());
        String sortValue = questionQueryParamsDto.getSortDirection().toUpperCase();
        Long totalCount = getTotalCountWithFilters(questionQueryParamsDto);
        int pagesCount = PaginationUtil.getPagesCount(totalCount, questionQueryParamsDto.getPageSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QuizQuestionEntity> criteriaQuery = criteriaBuilder.createQuery(QuizQuestionEntity.class);

        Root<QuizQuestionEntity> quizQuestionRoot = criteriaQuery.from(QuizQuestionEntity.class);
        Predicate filterPredicate = getFilters(quizQuestionRoot, questionQueryParamsDto);
        criteriaQuery.where(filterPredicate);

        if (sortValue.equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(SortUtil.getNestedPath(quizQuestionRoot, questionQueryParamsDto.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(SortUtil.getNestedPath(quizQuestionRoot, questionQueryParamsDto.getSortBy())));
        }

        TypedQuery<QuizQuestionEntity> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(skipValue);
        query.setMaxResults(questionQueryParamsDto.getPageSize());

        List<QuizQuestionEntity> foundedQuizQuestions = query.getResultList();
        List<ViewQuestionDto> quizQuestionsViewModels = foundedQuizQuestions.stream()
                .map(quizQuestion -> QuestionMapper.toView(quizQuestion))
                .collect(Collectors.toList());

        return new PaginationDto<>(
                pagesCount,
                questionQueryParamsDto.getPageNumber(),
                questionQueryParamsDto.getPageSize(),
                totalCount,
                quizQuestionsViewModels
        );
    }

    private Long getTotalCountWithFilters(QuestionQueryParamsDto commonQueryParamsDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        Root<QuizQuestionEntity> quizQuestionRoot = criteriaQuery.from(QuizQuestionEntity.class);
        Predicate filterPredicate = getFilters(quizQuestionRoot, commonQueryParamsDto);

        criteriaQuery.where(filterPredicate);
        criteriaQuery.select(criteriaBuilder.count(quizQuestionRoot));

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate getFilters(Root<QuizQuestionEntity> quizQuestionRoot, QuestionQueryParamsDto queryObj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> andPredicates = new ArrayList<>();

        if (!queryObj.getBodySearchTerm().isBlank()) {
            andPredicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(quizQuestionRoot.get("body")),
                    "%" + queryObj.getBodySearchTerm().toUpperCase() + "%"));
        }

        System.out.println(queryObj);

        switch (queryObj.getPublishedStatus()) {
            case "all":
                break;
            case "published":
                andPredicates.add(criteriaBuilder.isTrue(quizQuestionRoot.get("published")));
                break;
            case "notPublished":
                andPredicates.add(criteriaBuilder.isFalse(quizQuestionRoot.get("published")));
                break;
            default:
                throw new RuntimeException("Wrong value for getPublishedStatus. Available values : all, published, notPublished");
        }

        Predicate resultPredicate = andPredicates.isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
        return resultPredicate;
    }
}
