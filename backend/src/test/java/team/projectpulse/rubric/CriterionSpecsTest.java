package team.projectpulse.rubric;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CriterionSpecsTest {

    @Test
    void containsCriterionUsesLocaleIndependentLowercaseSearchTerm() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            @SuppressWarnings("unchecked")
            Root<Criterion> root = mock(Root.class);
            @SuppressWarnings("unchecked")
            Path<String> criterion = mock(Path.class);
            @SuppressWarnings("unchecked")
            Expression<String> loweredCriterion = mock(Expression.class);
            CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
            @SuppressWarnings("unchecked")
            CriteriaQuery<?> query = mock(CriteriaQuery.class);

            when(root.<String>get("criterion")).thenReturn(criterion);
            when(criteriaBuilder.lower(criterion)).thenReturn(loweredCriterion);

            Specification<Criterion> specification = CriterionSpecs.containsCriterion("TITLE");
            specification.toPredicate(root, query, criteriaBuilder);

            verify(criteriaBuilder).like(loweredCriterion, "%title%");
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
